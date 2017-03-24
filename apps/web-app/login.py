from flask.ext.login import LoginManager, login_required
from run import app
from models import User

#flask-login
login_manager = LoginManager()
login_manager.init_app(app)

@login_manager.user_loader
def load_user(id):
    return User.query.get(int(id))

