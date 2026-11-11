import argparse
import json
import sys
from collections.abc import Iterable
from typing import TextIO

from pydantic import ValidationError

from matching_worker.embeddings import EmbeddingInput
from matching_worker.embeddings import generate_embedding


def process_jsonl(
    input_stream: TextIO,
    output_stream: TextIO,
    model_name: str | None = None,
) -> None:
    for line_number, line in enumerate(input_stream, start=1):
        if not line.strip():
            continue
        try:
            record = EmbeddingInput.model_validate_json(line)
            result = generate_embedding(record, model_name)
        except (ValidationError, ValueError) as exception:
            raise ValueError(f"Registro JSONL inválido en la línea {line_number}: {exception}") from exception
        output_stream.write(result.model_dump_json() + "\n")


def parse_arguments(arguments: Iterable[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Genera embeddings locales para proyectos y oportunidades de financiación en JSONL."
    )
    parser.add_argument("--input", required=True, help="Archivo JSONL de entrada.")
    parser.add_argument(
        "--model",
        default=None,
        help="Modelo Sentence Transformers local. Por defecto usa MATCHING_EMBEDDING_MODEL "
        "o el modelo multilingüe configurado por defecto.",
    )
    return parser.parse_args(arguments)


def main(arguments: Iterable[str] | None = None) -> int:
    options = parse_arguments(arguments)
    try:
        with open(options.input, encoding="utf-8") as input_stream:
            process_jsonl(input_stream, sys.stdout, options.model)
    except (OSError, ValueError) as exception:
        print(str(exception), file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
