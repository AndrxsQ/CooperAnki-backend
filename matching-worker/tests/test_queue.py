from unittest.mock import Mock

import pytest

from matching_worker import queue
from matching_worker import scrapy_support


def test_enqueue_uses_scraping_queue_with_retry_and_timeout(monkeypatch) -> None:
    redis_connection = Mock()
    rq_queue = Mock()
    job = Mock(id="job-123")
    rq_queue.enqueue.return_value = job

    monkeypatch.setattr(queue, "validate_spider", Mock())
    monkeypatch.setattr(queue, "get_redis_connection", lambda: redis_connection)
    monkeypatch.setattr(queue, "Queue", Mock(return_value=rq_queue))

    result = queue.enqueue_scraping_job("portal_spider", {"region": "latam"})

    assert result is job
    assert rq_queue.enqueue.call_args.args == (
        queue.run_scrapy_spider,
        "portal_spider",
        {"region": "latam"},
    )
    assert rq_queue.enqueue.call_args.kwargs["job_timeout"] == queue.JOB_TIMEOUT_SECONDS
    assert rq_queue.enqueue.call_args.kwargs["retry"].max == queue.MAX_RETRIES


def test_rejects_unregistered_spider(monkeypatch) -> None:
    spider_loader = Mock()
    spider_loader.list.return_value = []
    monkeypatch.setattr(scrapy_support.SpiderLoader, "from_settings", lambda settings: spider_loader)

    with pytest.raises(ValueError, match="No existe un spider configurado"):
        queue.validate_spider("not_configured", {})


def test_rejects_invalid_spider_argument_name(monkeypatch) -> None:
    spider_loader = Mock()
    spider_loader.list.return_value = ["portal_spider"]
    monkeypatch.setattr(scrapy_support.SpiderLoader, "from_settings", lambda settings: spider_loader)

    with pytest.raises(ValueError, match="nombre del argumento"):
        queue.validate_spider("portal_spider", {"bad-name": "value"})
