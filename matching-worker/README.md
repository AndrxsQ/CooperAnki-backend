# Matching Worker

Independent Python service scaffold for the matching and scraping worker.

This initial scaffold intentionally provides only a health endpoint and
conservative Scrapy defaults. It does not include portal connectors, spiders,
extraction endpoints, queues, persistence, or embedding generation. Those
behaviors remain pending definition.

## Run locally

Requires Python 3.11 or newer.

```powershell
py -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -e ".[dev]"
python -m uvicorn matching_worker.app:app --app-dir src --reload
```

Health check: `GET http://localhost:8000/health`

## Run with Docker

From this directory:

```powershell
docker build -t coopera-matching-worker .
docker run --rm -p 8000:8000 coopera-matching-worker
```

## Scrapy safety defaults

The Scrapy project observes `robots.txt`, enables AutoThrottle, limits
concurrency to one request per domain, and waits two seconds between requests.
Before adding a portal spider, configure an identifiable user agent with a
valid contact and confirm the portal's terms and access policy.
