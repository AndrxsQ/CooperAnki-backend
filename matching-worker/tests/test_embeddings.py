import io
from unittest.mock import Mock

import numpy as np
import pytest

from matching_worker import embeddings
from matching_worker.generate_embeddings import process_jsonl


def test_generates_normalized_1536_dimension_project_embedding(monkeypatch) -> None:
    model = Mock()
    model.get_sentence_embedding_dimension.return_value = 3
    model.encode.return_value = np.array([[1.0, 0.0, 0.0]])
    monkeypatch.setattr(embeddings, "load_model", lambda model_name: model)

    record = embeddings.EmbeddingInput.model_validate({
        "entity_type": "project",
        "id": 41,
        "title": "Community project",
        "executive_summary": "Support local cooperatives.",
    })

    result = embeddings.generate_embedding(record, "local-test-model")

    assert result.entity_type == "project"
    assert result.entity_id == 41
    assert result.model == "local-test-model"
    assert result.model_dimensions == 3
    assert result.dimensions == 1536
    assert len(result.embedding) == 1536
    assert result.embedding[:3] == [1.0, 0.0, 0.0]
    assert all(value == 0.0 for value in result.embedding[3:])
    assert np.isclose(np.linalg.norm(result.embedding), 1.0)
    model.encode.assert_called_once_with(
        ["Project title: Community project Executive summary: Support local cooperatives."],
        convert_to_numpy=True,
        normalize_embeddings=True,
        show_progress_bar=False,
    )


def test_uses_funding_opportunity_fields_for_model_input(monkeypatch) -> None:
    model = Mock()
    model.get_sentence_embedding_dimension.return_value = 2
    model.encode.return_value = np.array([[0.0, 1.0]])
    monkeypatch.setattr(embeddings, "load_model", lambda model_name: model)

    record = embeddings.EmbeddingInput.model_validate({
        "entity_type": "funding_opportunity",
        "id": "grant-1",
        "title": "Regional program",
        "cooperating_entity": "Partner agency",
        "impact_area": "Education",
        "requirements_description": "Support rural schools.",
    })

    result = embeddings.generate_embedding(record, "local-test-model")

    assert result.entity_id == "grant-1"
    model.encode.assert_called_once_with(
        [
            "Funding opportunity: Regional program "
            "Cooperating entity: Partner agency "
            "Impact area: Education "
            "Requirements: Support rural schools."
        ],
        convert_to_numpy=True,
        normalize_embeddings=True,
        show_progress_bar=False,
    )


def test_chunks_long_records_before_encoding(monkeypatch) -> None:
    model = Mock()
    model.get_sentence_embedding_dimension.return_value = 2
    model.encode.return_value = np.array([[1.0, 0.0], [0.0, 1.0]])
    monkeypatch.setattr(embeddings, "load_model", lambda model_name: model)
    long_summary = " ".join(f"word{index}" for index in range(81))
    record = embeddings.EmbeddingInput.model_validate({
        "entity_type": "project",
        "id": 1,
        "title": "Long project",
        "executive_summary": long_summary,
    })

    embeddings.generate_embedding(record, "local-test-model")

    chunks = model.encode.call_args.args[0]
    assert len(chunks) == 2
    assert chunks[0].startswith("Project title: Long project Executive summary:")
    assert len(chunks[0].split()) == embeddings.WORDS_PER_CHUNK
    assert chunks[1].endswith("word80")


def test_rejects_models_larger_than_database_vector_dimension(monkeypatch) -> None:
    model = Mock()
    model.get_sentence_embedding_dimension.return_value = 1537
    monkeypatch.setattr(embeddings, "load_model", lambda model_name: model)
    record = embeddings.EmbeddingInput.model_validate({
        "entity_type": "project",
        "id": 1,
        "title": "Project",
    })

    with pytest.raises(ValueError, match="debe generar entre 1 y 1536"):
        embeddings.generate_embedding(record, "too-large-model")


def test_jsonl_processor_serializes_generated_embeddings(monkeypatch) -> None:
    record = embeddings.EmbeddingInput.model_validate({
        "entity_type": "project",
        "id": 41,
        "title": "Community project",
    })
    result = embeddings.EmbeddingOutput(
        entity_type="project",
        entity_id=41,
        model="local-test-model",
        model_dimensions=2,
        dimensions=1536,
        embedding=[1.0] + [0.0] * 1535,
    )
    generate_embedding = Mock(return_value=result)
    monkeypatch.setattr("matching_worker.generate_embeddings.generate_embedding", generate_embedding)
    input_stream = io.StringIO(record.model_dump_json() + "\n")
    output_stream = io.StringIO()

    process_jsonl(input_stream, output_stream, "local-test-model")

    assert output_stream.getvalue().count("\n") == 1
    assert '"entity_id":41' in output_stream.getvalue()
    assert '"dimensions":1536' in output_stream.getvalue()
    generate_embedding.assert_called_once_with(record, "local-test-model")


def test_jsonl_processor_reports_invalid_record_line() -> None:
    with pytest.raises(ValueError, match="línea 1"):
        process_jsonl(io.StringIO('{"entity_type":"unknown","id":1,"title":"Bad"}\n'), io.StringIO())
