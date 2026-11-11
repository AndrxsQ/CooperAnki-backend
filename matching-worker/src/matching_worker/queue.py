import os
from typing import Any

from redis import Redis
from rq import Queue
from rq import Retry

from matching_worker.scrapy_support import validate_spider
from matching_worker.tasks import run_scrapy_spider

QUEUE_NAME = "scraping"
JOB_TIMEOUT_SECONDS = 1800
MAX_RETRIES = 2


def get_redis_connection() -> Redis:
    redis_url = os.getenv("REDIS_URL", "redis://localhost:6379/0")
    return Redis.from_url(
        redis_url,
        decode_responses=False,
        socket_connect_timeout=3,
        socket_timeout=3,
    )


def enqueue_scraping_job(spider_name: str, spider_arguments: dict[str, str]) -> Any:
    validate_spider(spider_name, spider_arguments)
    queue = Queue(QUEUE_NAME, connection=get_redis_connection())
    return queue.enqueue(
        run_scrapy_spider,
        spider_name,
        spider_arguments,
        job_timeout=JOB_TIMEOUT_SECONDS,
        retry=Retry(max=MAX_RETRIES, interval=[30, 120]),
    )
