from matching_worker import scrapy_settings


def test_scrapy_uses_conservative_and_robots_aware_defaults() -> None:
    assert scrapy_settings.ROBOTSTXT_OBEY is True
    assert scrapy_settings.AUTOTHROTTLE_ENABLED is True
    assert scrapy_settings.CONCURRENT_REQUESTS_PER_DOMAIN == 1
    assert scrapy_settings.DOWNLOAD_DELAY >= 2.0
    assert scrapy_settings.TELNETCONSOLE_ENABLED is False
