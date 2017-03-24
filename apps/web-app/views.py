from flask import Blueprint, render_template, request, redirect, url_for, flash
from forms import LoginForm

devops = Blueprint('devops', __name__)

@devops.route('/')
@devops.route('/index', methods=['GET', 'POST'])
def index():
    form = LoginForm()
    return render_template('index.html', title='DevOps Nokia', form=form)

@devops.errorhandler(404)
def page_not_found(error):
    return 'fail'