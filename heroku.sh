#!/bin/bash
celery worker -A apps.webapp.celery &
celery beat --app apps.webapp.celery &
gunicorn -w 4 -b 0.0.0.0:$PORT -k gevent apps.webapp.run:app
