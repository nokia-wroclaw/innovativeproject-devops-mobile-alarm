from flask import Flask, session, request, flash, url_for, redirect, render_template, abort, g
from flask_login import login_user, logout_user, current_user, login_required
from webapp import db, app, login_manager
from .models import User

@login_manager.user_loader
def load_user(id):
    return User.query.get(int(id))

@app.route('/')
@app.route('/index')
@login_required
def index():
    user = g.user
    return render_template('index.html', title="DevOps Nokia Project", user=user)

@app.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('index'))

@app.before_request
def before_request():
    g.user = current_user

@app.route('/login',methods=['GET','POST'])
def login():
    if request.method == 'GET':
        return render_template('login.html')
    email = request.form['email']
    password = request.form['password']
    registered_user = User.query.filter_by(email=email,password=password).first()
    if registered_user is None or registered_user.is_admin == False:
        flash('Email or Password is invalid' , 'error')
        return redirect(url_for('login'))
    login_user(registered_user)
    flash('Logged in successfully')
    return redirect(request.args.get('next') or url_for('index'))

@app.errorhandler(404)
def page_not_found(error):
    return 'fail'

@app.route("/test")
def testJSON():
    return "Kapibara"