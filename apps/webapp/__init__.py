import os
import sys
from os import path
from flask import Flask
from flask.ext.login import LoginManager
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
sys.path.append(path.dirname(path.dirname(path.abspath(__file__))))

#config for Forms
app.config.update(DEBUG = True, SECRET_KEY = 'you-will-never-guess')

#conection to database
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ['DATABASE_URL']
db = SQLAlchemy(app)

#Configure Flask-Login
login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = 'login'

from webapp import views