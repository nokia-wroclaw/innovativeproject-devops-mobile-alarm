from flask import Flask,session, request, flash, url_for, redirect, render_template, abort, Blueprint
from flask.ext.login import login_user , logout_user , current_user , login_required
from forms import LoginForm
from models import User
import login

devops = Blueprint('devops', __name__)

@devops.route('/')
@devops.route('/index', methods=['GET', 'POST'])
def index():
    form = LoginForm()
    if request.method == 'GET':
        return render_template('index.html', title='DevOps Nokia', form=form)
    email = request.form['inputEmail']
    password = request.form['inputPassword']
    admin = User.query.fillter_by(email=email, password=password).first()
    if admin is None or admin.is_admin == False:
        flash('Error, no user!')
        return redirect(url_for('index'))
    login_user(registered_user)
    flash('Logged in successfully')
    return redirect(request.args.get('next') or url_for('test'))

@devops.errorhandler(404)
def page_not_found(error):
    return 'fail'

@devops.route("/test")
def testJSON():
    return "Kapibara"
