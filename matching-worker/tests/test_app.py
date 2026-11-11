from unittest.mock import Mock

from fastapi import HTTPException
from fastapi.testclient import TestClient
from redis.exceptions import ConnectionError as RedisConnectionError

from matching_worker.app import app
from matching_worker.app import verify_worker_api_key
from matching_worker.embeddings import EmbeddingOutput


def test_health_check_reports_worker_and_queue_status(monkeypatch) -> None:
    redis_connection = Mock()
    monkeypatch.setattr("matching_worker.app.get_redis_connection", lambda: redis_connection)

    response = TestClient(app).get("/health")

    redis_connection.ping.assert_called_once_with()
    assert response.status_code == 200
    assert response.json() == {"status": "ok", "service": "matching-worker", "queue": "ok"}


def test_health_check_reports_unavailable_redis(monkeypatch) -> None:
    redis_connection = Mock()
    redis_connection.ping.side_effect = RedisConnectionError("connection refused")
    monkeypatch.setattr("matching_worker.app.get_redis_connection", lambda: redis_connection)

    response = TestClient(app).get("/health")

    assert response.status_code == 503
    assert response.json()["detail"] == "Redis no está disponible"


def test_worker_api_key_fails_closed_when_not_configured(monkeypatch) -> None:
    monkeypatch.delenv("MATCHING_WORKER_API_KEY", raising=False)

    try:
        verify_worker_api_key("any-key")
    except HTTPException as exception:
        assert exception.status_code == 503
    else:
        raise AssertionError("La ruta debe fallar si falta la clave de servicio")


def test_enqueue_requires_valid_service_api_key(monkeypatch) -> None:
    monkeypatch.setenv("MATCHING_WORKER_API_KEY", "expected-key")

    response = TestClient(app).post(
        "/jobs/scraping",
        json={"spiderName": "portal_spider"},
    )

    assert response.status_code == 401


def test_enqueue_returns_job_id_for_registered_spider(monkeypatch) -> None:
    monkeypatch.setenv("MATCHING_WORKER_API_KEY", "expected-key")
    monkeypatch.setattr(
        "matching_worker.app.enqueue_scraping_job",
        lambda spider_name, spider_arguments: Mock(id="job-123"),
    )

    response = TestClient(app).post(
        "/jobs/scraping",
        headers={"X-Worker-API-Key": "expected-key"},
        json={"spiderName": "portal_spider", "spiderArguments": {"region": "latam"}},
    )

    assert response.status_code == 202
    assert response.json() == {"jobId": "job-123", "status": "queued"}


def test_enqueue_rejects_spiders_without_a_configured_connector(monkeypatch) -> None:
    monkeypatch.setenv("MATCHING_WORKER_API_KEY", "expected-key")

    response = TestClient(app).post(
        "/jobs/scraping",
        headers={"X-Worker-API-Key": "expected-key"},
        json={"spiderName": "portal_spider"},
    )

    assert response.status_code == 422
    assert "No existe un spider configurado" in response.json()["detail"]


def test_embeddings_requires_valid_service_api_key(monkeypatch) -> None:
    monkeypatch.setenv("MATCHING_WORKER_API_KEY", "expected-key")

    response = TestClient(app).post(
        "/embeddings",
        json=[{"entity_type": "project", "id": 7, "title": "Project"}],
    )

    assert response.status_code == 401


def test_embeddings_generates_vectors_for_authenticated_batch(monkeypatch) -> None:
    monkeypatch.setenv("MATCHING_WORKER_API_KEY", "expected-key")
    result = EmbeddingOutput(
        entity_type="project",
        entity_id=7,
        model="local-test-model",
        model_dimensions=2,
        dimensions=1536,
        embedding=[1.0] + [0.0] * 1535,
    )
    generate_embedding = Mock(return_value=result)
    monkeypatch.setattr("matching_worker.app.generate_embedding", generate_embedding)

    response = TestClient(app).post(
        "/embeddings",
        headers={"X-Worker-API-Key": "expected-key"},
        json=[{"entityType": "project", "id": 7, "title": "Project"}],
    )

    assert response.status_code == 200
    assert response.json()[0]["entityId"] == 7
    assert response.json()[0]["dimensions"] == 1536
    generate_embedding.assert_called_once()


def test_embeddings_rejects_duplicate_records_in_a_batch(monkeypatch) -> None:
    monkeypatch.setenv("MATCHING_WORKER_API_KEY", "expected-key")

    response = TestClient(app).post(
        "/embeddings",
        headers={"X-Worker-API-Key": "expected-key"},
        json=[
            {"entity_type": "project", "id": 7, "title": "Project"},
            {"entity_type": "project", "id": 7, "title": "Project"},
        ],
    )

    assert response.status_code == 422
    assert response.json()["detail"] == "El lote no puede contener registros duplicados"


def test_embeddings_rejects_batches_larger_than_limit(monkeypatch) -> None:
    monkeypatch.setenv("MATCHING_WORKER_API_KEY", "expected-key")
    records = [
        {"entity_type": "project", "id": index, "title": "Project"}
        for index in range(17)
    ]

    response = TestClient(app).post(
        "/embeddings",
        headers={"X-Worker-API-Key": "expected-key"},
        json=records,
    )

    assert response.status_code == 422
