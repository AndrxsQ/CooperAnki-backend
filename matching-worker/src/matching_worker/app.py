from fastapi import FastAPI

app = FastAPI(
    title="Coopera2075 Matching Worker",
    description="Scaffold for the independent matching and scraping worker.",
    version="0.1.0",
)


@app.get("/health", tags=["health"])
def health_check() -> dict[str, str]:
    return {"status": "ok", "service": "matching-worker"}
