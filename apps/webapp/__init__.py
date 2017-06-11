import os
import sys
from os import path
from flask import Flask
from flask.ext.login import LoginManager
from flask_sqlalchemy import SQLAlchemy
from flask_recaptcha import ReCaptcha

app = Flask(__name__)
sys.path.append(path.dirname(path.dirname(path.abspath(__file__))))

#config for Forms, Register and FCM tokens
app.config.update(dict( DEBUG = True, SECRET_KEY = 'you-will-never-guess', SECURITY_PASSWORD_SALT = 'my_precious_two', FCM_APP_TOKEN = 'AAAAXUWoieY:APA91bGcVQ67M5mAEl7e2OSb5yKko8J17NH7GZtOspoq9NKjnHMyD9RiCePjLKUHfyBzn4II0aVJx_JnyyBHQijdbT6sYwxAoDrI15bZX_0FdBpHKgAVqMBpKMQAxIggXxakcZ3It54f', RECAPTCHA_ENABLED = True, RECAPTCHA_SITE_KEY = '6LetACUUAAAAAPckPB-tmBZdLo9eZDp5tacC1XA9', RECAPTCHA_SECRET_KEY = '6LetACUUAAAAAMUPZ3N1gjDO1AHxq8AVAXau9Fg-', RECAPTCHA_THEME = 'light'))

#recaptcha init
recaptcha = ReCaptcha()
recaptcha.init_app(app)

#conection to database
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ['DATABASE_URL']
db = SQLAlchemy(app)

#Configure Flask-Login
login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = 'login'

### move to other module and resolve problem with second import models (Table 'user' is already defined for this MetaData instance)
import config_celery

#Configure Celery
app.config.update(CELERY_BROKER_URL=os.environ['REDIS_URL'], CELERY_RESULT_BACKEND=os.environ['REDIS_URL'])
celery = config_celery.make_celery(app)

###

#User types
#adm - admin, usr - regular user, oth - for later use
def enum(**enums):
    return type('Enum', (), enums)

UserType = enum(adm=1, usr=2, oth=3)

ServiceState = enum(up=1, down=2, unspecified=3)

from webapp import tasks
from webapp import views
