import os
import sys
from os import path
from flask import Flask
from flask.ext.login import LoginManager
from flask_sqlalchemy import SQLAlchemy
import config_celery

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

#Configure Celery
app.config.update(CELERY_BROKER_URL=os.environ['REDIS_URL'], CELERY_RESULT_BACKEND=os.environ['REDIS_URL'])
celery = config_celery.make_celery(app)

#User types
#adm - admin, usr - regular user, oth - for later use
def enum(**enums):
    return type('Enum', (), enums)

UserType = enum(adm=1, usr=2, oth=3)

ServiceState = enum(up=1, down=2, unspecified=3)

from webapp import views