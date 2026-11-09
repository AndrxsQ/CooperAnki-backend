import os

BOT_NAME = "coopera_matching_worker"

SPIDER_MODULES = ["matching_worker.spiders"]
NEWSPIDER_MODULE = "matching_worker.spiders"

ROBOTSTXT_OBEY = True
AUTOTHROTTLE_ENABLED = True
AUTOTHROTTLE_START_DELAY = 2.0
AUTOTHROTTLE_MAX_DELAY = 30.0
CONCURRENT_REQUESTS_PER_DOMAIN = 1
DOWNLOAD_DELAY = 2.0
USER_AGENT = os.getenv(
    "MATCHING_WORKER_USER_AGENT",
    "Coopera2075MatchingWorker/0.1 (configure contact before enabling spiders)",
)
TELNETCONSOLE_ENABLED = False
LOG_LEVEL = "INFO"
FEED_EXPORT_ENCODING = "utf-8"
