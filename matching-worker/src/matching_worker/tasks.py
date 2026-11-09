import subprocess
import sys
from pathlib import Path

from matching_worker.scrapy_support import SPIDER_ARGUMENT_NAME_PATTERN
from matching_worker.scrapy_support import validate_spider

PROJECT_ROOT = Path(__file__).resolve().parents[2]
SCRAPY_PROCESS_TIMEOUT_SECONDS = 1740


def run_scrapy_spider(spider_name: str, spider_arguments: dict[str, str]) -> dict[str, str]:
    validate_spider(spider_name, spider_arguments)

    command = [sys.executable, "-m", "scrapy", "crawl", spider_name]
    for name, value in sorted(spider_arguments.items()):
        if not SPIDER_ARGUMENT_NAME_PATTERN.fullmatch(name):
            raise ValueError(f"El nombre del argumento '{name}' no es válido")
        command.extend(["-a", f"{name}={value}"])

    subprocess.run(
        command,
        cwd=PROJECT_ROOT,
        check=True,
        timeout=SCRAPY_PROCESS_TIMEOUT_SECONDS,
    )
    return {"spider": spider_name, "status": "completed"}
