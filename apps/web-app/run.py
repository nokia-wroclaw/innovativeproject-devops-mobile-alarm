import os
from flask import Flask
import views

app = Flask(__name__, template_folder='templates')
app.register_blueprint(views.devops)

def run():
    app.run(debug=True)
