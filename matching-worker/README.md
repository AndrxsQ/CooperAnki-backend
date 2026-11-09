# Matching Worker

Independent Python service scaffold for the matching and scraping worker.

The worker exposes a health endpoint and an API-key-protected endpoint for
queueing registered Scrapy spiders through Redis/RQ. The RQ worker runs each
Scrapy crawl in a separate process to avoid reusing Twisted's reactor across
jobs. No portal connectors or spiders are included until source portals and
their access policies are selected. Persistence and embedding generation are
also out of scope.

## Run locally

Requires Python 3.11 or newer.

```powershell
py -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -e ".[dev]"
python -m uvicorn matching_worker.app:app --app-dir src --reload
```

Set `REDIS_URL` to a reachable Redis instance and `MATCHING_WORKER_API_KEY` to
a secret shared only with trusted backend callers. Start the queue worker in a
second terminal:

```powershell
python -m matching_worker.worker
```

Health check: `GET http://localhost:8000/health`. Job submission:
`POST /jobs/scraping`, with `X-Worker-API-Key` and JSON containing a registered
`spiderName` and optional string-valued `spiderArguments`. Until a spider
connector is added, submissions are rejected with `422`.

## Run with Docker

From the repository root, configure `MATCHING_WORKER_API_KEY`, then run:

```powershell
docker compose up --build redis matching-worker-api matching-worker-queue
```

The API is bound to `127.0.0.1:8000` and Redis is not exposed outside the host.

## Scrapy safety defaults

The Scrapy project observes `robots.txt`, enables AutoThrottle, limits
concurrency to one request per domain, and waits two seconds between requests.
Before adding a portal spider, configure an identifiable user agent with a
valid contact and confirm the portal's terms and access policy.
