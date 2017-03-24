import os
from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy
import views

app = Flask(__name__, template_folder='templates')

#config for Forms
app.config.update(DEBUG = True, SECRET_KEY = 'csefdfdf')

#import aplication views
app.register_blueprint(views.devops)

#conection to database
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ['DATABASE_URL']
db = SQLAlchemy(app)


def run():
    app.run(debug=True)
