import hmac
import os

from fastapi import Depends
from fastapi import Body
from fastapi import FastAPI
from fastapi import Header
from fastapi import HTTPException
from fastapi import status
from pydantic import BaseModel
from pydantic import Field
from redis.exceptions import RedisError

from matching_worker.embeddings import EmbeddingInput
from matching_worker.embeddings import generate_embedding
from matching_worker.queue import enqueue_scraping_job
from matching_worker.queue import get_redis_connection

app = FastAPI(
    title="Coopera2075 Matching Worker",
    description="API interna para embeddings y trabajos de scraping.",
    version="0.3.0",
)


@app.get("/health", tags=["health"])
def health_check() -> dict[str, str]:
    try:
        get_redis_connection().ping()
    except RedisError as exception:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Redis no está disponible",
        ) from exception
    return {"status": "ok", "service": "matching-worker", "queue": "ok"}


class ScrapingJobRequest(BaseModel):
    spider_name: str = Field(alias="spiderName", min_length=1, max_length=100)
    spider_arguments: dict[str, str] = Field(default_factory=dict, alias="spiderArguments")

    model_config = {"populate_by_name": True}


class ScrapingJobResponse(BaseModel):
    job_id: str = Field(serialization_alias="jobId")
    status: str


class EmbeddingApiResponse(BaseModel):
    entityType: str
    entityId: str | int
    model: str
    modelDimensions: int
    dimensions: int
    embedding: list[float]


def verify_worker_api_key(
    x_worker_api_key: str | None = Header(default=None, alias="X-Worker-API-Key"),
) -> None:
    expected_api_key = os.getenv("MATCHING_WORKER_API_KEY", "")
    if not expected_api_key:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="El servicio de encolado no está configurado",
        )
    if x_worker_api_key is None or not hmac.compare_digest(x_worker_api_key, expected_api_key):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Credenciales de servicio no válidas",
        )


@app.post(
    "/jobs/scraping",
    response_model=ScrapingJobResponse,
    status_code=status.HTTP_202_ACCEPTED,
    tags=["jobs"],
    dependencies=[Depends(verify_worker_api_key)],
)
def enqueue_scraping(
    request: ScrapingJobRequest,
) -> ScrapingJobResponse:
    try:
        job = enqueue_scraping_job(request.spider_name, request.spider_arguments)
    except ValueError as exception:
        raise HTTPException(
            status_code=422,
            detail=str(exception),
        ) from exception
    return ScrapingJobResponse(job_id=job.id, status="queued")


@app.post(
    "/embeddings",
    response_model=list[EmbeddingApiResponse],
    tags=["embeddings"],
    dependencies=[Depends(verify_worker_api_key)],
)
def generate_embeddings(
    records: list[EmbeddingInput] = Body(min_length=1, max_length=16),
) -> list[EmbeddingApiResponse]:
    identifiers = [(record.entity_type, str(record.entity_id)) for record in records]
    if len(identifiers) != len(set(identifiers)):
        raise HTTPException(
            status_code=422,
            detail="El lote no puede contener registros duplicados",
        )
    return [
        EmbeddingApiResponse(
            entityType=result.entity_type,
            entityId=result.entity_id,
            model=result.model,
            modelDimensions=result.model_dimensions,
            dimensions=result.dimensions,
            embedding=result.embedding,
        )
        for result in (generate_embedding(record) for record in records)
    ]
