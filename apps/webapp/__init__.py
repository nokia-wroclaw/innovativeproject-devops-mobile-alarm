import os
import sys
from os import path
from flask import Flask
from flask.ext.login import LoginManager
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
sys.path.append(path.dirname(path.dirname(path.abspath(__file__))))

#config for Forms
app.config.update(DEBUG = True, SECRET_KEY = 'you-will-never-guess', SECURITY_PASSWORD_SALT = 'my_precious_two')

#conection to database
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ['DATABASE_URL']
db = SQLAlchemy(app)

#Configure Flask-Login
login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = 'login'

### move to other module and resolve problem with second import models (Table 'user' is already defined for this MetaData instance)
import config_celery
from celery.task.schedules import crontab
from celery.decorators import periodic_task
from celery.utils.log import get_task_logger
from celery.signals import worker_process_init
import datetime

import ping

#Configure Celery
app.config.update(CELERY_BROKER_URL=os.environ['REDIS_URL'], CELERY_RESULT_BACKEND=os.environ['REDIS_URL'])
celery = config_celery.make_celery(app)

@periodic_task(run_every=(crontab(minute="*/5")), ignore_result=True)
def ping_services():
    from models import Service
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
        elif response >= 400 and response < 600 and service.current_state != ServiceState.down:
            service.previous_state = service.current_state
            service.time_of_last_change_of_state = datetime.datetime.now()
            service.current_state = ServiceState.down
    db.session.commit()

###

#User types
#adm - admin, usr - regular user, oth - for later use
def enum(**enums):
    return type('Enum', (), enums)

UserType = enum(adm=1, usr=2, oth=3)

ServiceState = enum(up=1, down=2, unspecified=3)

from webapp import views