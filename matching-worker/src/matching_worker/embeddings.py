import os
import re
from functools import lru_cache
from typing import Any

import numpy as np
from pydantic import BaseModel
from pydantic import ConfigDict
from pydantic import Field
from pydantic import field_validator
from pydantic.alias_generators import to_camel
from sentence_transformers import SentenceTransformer

TARGET_DIMENSIONS = 1536
DEFAULT_MODEL_NAME = "sentence-transformers/paraphrase-multilingual-mpnet-base-v2"
MAX_INPUT_CHARACTERS = 100_000
WORDS_PER_CHUNK = 80
WHITESPACE_PATTERN = re.compile(r"\s+")


class EmbeddingInput(BaseModel):
    model_config = ConfigDict(
        extra="forbid",
        populate_by_name=True,
        alias_generator=to_camel,
    )

    entity_type: str = Field(pattern=r"^(project|funding_opportunity)$")
    entity_id: str | int = Field(alias="id")
    title: str = Field(min_length=1, max_length=255)
    executive_summary: str | None = None
    cooperating_entity: str | None = None
    impact_area: str | None = None
    requirements_description: str | None = None

    @field_validator("title")
    @classmethod
    def title_must_not_be_blank(cls, value: str) -> str:
        normalized = value.strip()
        if not normalized:
            raise ValueError("El título no puede estar vacío")
        return normalized


class EmbeddingOutput(BaseModel):
    entity_type: str
    entity_id: str | int
    model: str
    model_dimensions: int
    dimensions: int
    embedding: list[float]


@lru_cache(maxsize=2)
def load_model(model_name: str) -> SentenceTransformer:
    return SentenceTransformer(model_name)


def build_embedding_text(record: EmbeddingInput) -> str:
    fields: list[tuple[str, str | None]]
    if record.entity_type == "project":
        fields = [
            ("Project title", record.title),
            ("Executive summary", record.executive_summary),
        ]
    else:
        fields = [
            ("Funding opportunity", record.title),
            ("Cooperating entity", record.cooperating_entity),
            ("Impact area", record.impact_area),
            ("Requirements", record.requirements_description),
        ]

    text = "\n".join(
        f"{label}: {value.strip()}"
        for label, value in fields
        if value is not None and value.strip()
    )
    if len(text) > MAX_INPUT_CHARACTERS:
        raise ValueError(f"El texto supera el límite de {MAX_INPUT_CHARACTERS} caracteres")
    return text


def split_text_into_chunks(text: str) -> list[str]:
    words = WHITESPACE_PATTERN.split(text.strip())
    return [
        " ".join(words[index:index + WORDS_PER_CHUNK])
        for index in range(0, len(words), WORDS_PER_CHUNK)
    ]


def generate_embedding(
    record: EmbeddingInput,
    model_name: str | None = None,
) -> EmbeddingOutput:
    selected_model = model_name or os.getenv("MATCHING_EMBEDDING_MODEL", DEFAULT_MODEL_NAME)
    model = load_model(selected_model)
    model_dimensions = model.get_sentence_embedding_dimension()
    if model_dimensions is None or model_dimensions < 1 or model_dimensions > TARGET_DIMENSIONS:
        raise ValueError(
            f"El modelo '{selected_model}' debe generar entre 1 y {TARGET_DIMENSIONS} dimensiones"
        )

    text = build_embedding_text(record)
    chunks = split_text_into_chunks(text)
    if not chunks:
        raise ValueError("El texto del registro no contiene contenido para vectorizar")

    encoded_chunks: Any = model.encode(
        chunks,
        convert_to_numpy=True,
        normalize_embeddings=True,
        show_progress_bar=False,
    )
    chunk_vectors = np.asarray(encoded_chunks, dtype=np.float64)
    if chunk_vectors.shape != (len(chunks), model_dimensions):
        raise ValueError("El modelo devolvió vectores con dimensiones inesperadas")
    if not np.isfinite(chunk_vectors).all():
        raise ValueError("El modelo devolvió valores no finitos")

    vector = chunk_vectors.mean(axis=0)
    vector_norm = float(np.linalg.norm(vector))
    if not np.isfinite(vector_norm) or vector_norm == 0:
        raise ValueError("No se pudo normalizar el embedding generado")
    normalized_vector = vector / vector_norm
    padded_vector = np.pad(
        normalized_vector,
        (0, TARGET_DIMENSIONS - model_dimensions),
        mode="constant",
    )

    return EmbeddingOutput(
        entity_type=record.entity_type,
        entity_id=record.entity_id,
        model=selected_model,
        model_dimensions=model_dimensions,
        dimensions=TARGET_DIMENSIONS,
        embedding=padded_vector.tolist(),
    )
