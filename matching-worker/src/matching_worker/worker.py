from rq import Queue
from rq import Worker

from matching_worker.queue import QUEUE_NAME
from matching_worker.queue import get_redis_connection


def main() -> None:
    connection = get_redis_connection()
    worker = Worker([Queue(QUEUE_NAME, connection=connection)], connection=connection)
    worker.work(with_scheduler=True)


if __name__ == "__main__":
    main()
