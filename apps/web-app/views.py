from flask import Blueprint, render_template, request, redirect, url_for

devops = Blueprint('devops', __name__)

@devops.route('/')
@devops.route('/index')
def index():
    user = {'nickname': 'World'}
    return render_template('index.html', title='Osdev Nokia', user=user)

@devops.errorhandler(404)
def page_not_found(error):
    return 'fail'