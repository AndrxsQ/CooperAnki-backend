import re
from typing import Any

from scrapy.settings import Settings
from scrapy.spiderloader import SpiderLoader

SPIDER_NAME_PATTERN = re.compile(r"^[A-Za-z0-9_-]{1,100}$")
SPIDER_ARGUMENT_NAME_PATTERN = re.compile(r"^[A-Za-z_][A-Za-z0-9_]{0,63}$")
MAX_SPIDER_ARGUMENTS = 50
MAX_SPIDER_ARGUMENT_VALUE_LENGTH = 2000


def get_scrapy_settings() -> Settings:
    settings = Settings()
    settings.setmodule("matching_worker.scrapy_settings")
    return settings


def validate_spider(spider_name: str, spider_arguments: dict[str, Any]) -> None:
    if not SPIDER_NAME_PATTERN.fullmatch(spider_name):
        raise ValueError("El nombre del spider no tiene un formato válido")
    if len(spider_arguments) > MAX_SPIDER_ARGUMENTS:
        raise ValueError("El trabajo supera el máximo de argumentos permitidos")
    for name, value in spider_arguments.items():
        if not SPIDER_ARGUMENT_NAME_PATTERN.fullmatch(name):
            raise ValueError(f"El nombre del argumento '{name}' no es válido")
        if not isinstance(value, str):
            raise ValueError(f"El argumento '{name}' debe ser texto")
        if len(value) > MAX_SPIDER_ARGUMENT_VALUE_LENGTH:
            raise ValueError(f"El argumento '{name}' supera la longitud permitida")

    spider_loader = SpiderLoader.from_settings(get_scrapy_settings())
    if spider_name not in spider_loader.list():
        raise ValueError(f"No existe un spider configurado con el nombre '{spider_name}'")
