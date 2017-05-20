from celery.task.schedules import crontab
from celery.decorators import periodic_task
from celery.utils.log import get_task_logger
from celery.signals import worker_process_init
import datetime
import ping
from models import Service, Tokens, History
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
            history = History(service.address, service.name, ServiceState.unspecified, service.time_of_last_change_of_state, service.organization_id)
            db.session.add(history)
        elif response >= 200 and response < 300 and service.current_state != ServiceState.up:
            service.previous_state = service.current_state
            service.time_of_last_change_of_state = datetime.datetime.now()
            service.current_state = ServiceState.up
            history = History(service.address, service.name, ServiceState.up, service.time_of_last_change_of_state, service.organization_id)
            db.session.add(history)
            if service.users:
                send_notification( "icon_blue", service, "UP" )
        elif response >= 400 and response < 600 and service.current_state != ServiceState.down:
            service.previous_state = service.current_state
            service.time_of_last_change_of_state = datetime.datetime.now()
            service.current_state = ServiceState.down
            history = History(service.address, service.name, ServiceState.down, service.time_of_last_change_of_state, service.organization_id)
            db.session.add(history)
            if service.users:
                send_notification( "icon_red", service, "DOWN" )
    db.session.commit()

#task run every Monday at 7:30
@periodic_task(run_every=(crontab(hour=7, minute=30, day_of_week=1)), ignore_result=True)
def cleaning_tokens():
    for token in Tokens.query.all():
        #checking if token is older then 7 days
        expiration_date = token.date_of_expire + datetime.timedelta(days=7)
        if expiration_date <= datetime.datetime.now() or token.is_used == True:
            db.session.delete(token)
    db.session.commit()