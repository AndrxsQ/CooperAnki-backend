from fastapi.testclient import TestClient

from matching_worker.app import app


def test_health_check_reports_worker_status() -> None:
    response = TestClient(app).get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok", "service": "matching-worker"}
