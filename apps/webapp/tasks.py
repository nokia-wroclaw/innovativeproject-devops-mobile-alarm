from celery.task.schedules import crontab
from celery.decorators import periodic_task
from celery.utils.log import get_task_logger
from celery.signals import worker_process_init
import datetime
import ping
from models import Service
from webapp import celery, ServiceState, db
from functions import send_notification

@periodic_task(run_every=(crontab(minute="*")), ignore_result=True)
def ping_services():
    for service in Service.query.all():
        response = ping.ping(service.address)
        if response == 0 and service.current_state != ServiceState.unspecified:
            service.previous_state = service.current_state
            service.time_of_last_change_of_state = datetime.datetime.now()
            service.current_state = ServiceState.unspecified
        elif response >= 200 and response < 300 and service.current_state != ServiceState.up:
            service.previous_state = service.current_state
            service.time_of_last_change_of_state = datetime.datetime.now()
            service.current_state = ServiceState.up
            if service.users:
                send_notification( "icon_blue", service, "UP" )
        elif response >= 400 and response < 600 and service.current_state != ServiceState.down:
            service.previous_state = service.current_state
            service.time_of_last_change_of_state = datetime.datetime.now()
            service.current_state = ServiceState.down
            if service.users:
                send_notification( "icon_red", service, "DOWN" )
    db.session.commit()