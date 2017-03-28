from flask import Flask, session, request, flash, url_for, redirect, render_template, abort, g, jsonify
from flask_login import login_user, logout_user, current_user, login_required
from webapp import db, app, login_manager, UserType
from .models import User, Tokens
import sendgrid
import os
from sendgrid.helpers.mail import *
import bcrypt
import datetime
from functions import generate_registration_token, confirm_token

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
    # display login template
    if request.method == 'GET':
        return render_template('login.html')
    # get input values from template
    email = request.form['email']
    password = request.form['password']
    # get user from db by email
    registered_user = User.query.filter_by(email=email).first()
    # check if user with given email exists in db
    if registered_user is None:
        flash('The user does not exist!' , 'error')
        return redirect(url_for('login'))
    # checking encrypted password
    check_pass = bcrypt.checkpw(password.encode(), registered_user.password.encode())
    # check if password is correct and user is admin
    if check_pass == False or registered_user.user_type != UserType.adm:
        flash('Email or Password is invalid' , 'error')
        return redirect(url_for('login'))
    # login user
    login_user(registered_user)
    flash('Logged in successfully')
    return redirect(request.args.get('next') or url_for('index'))

@app.route('/register/<token>' , methods=['GET','POST'])
def register(token):
    pushed_token = Tokens.query.filter_by(token=token).first()
    
    # check if token expired
    expiration_date = pushed_token.date_of_expire + datetime.timedelta(days=7)
    if expiration_date <= datetime.datetime.now():
        if request.method == 'GET':
            return "Brak dostepu! Token wygasl!"
    
    if request.method == 'GET':
        if pushed_token is None or pushed_token.is_used == True:
            return "Brak dostepu!"
        else:
            return render_template('register.html')
    email = confirm_token(token)
    name = request.form['name']
    surname = request.form['surname']
    password = request.form['password']
    password_bytes = password.encode('utf-8')
    hashed = bcrypt.hashpw(password_bytes, bcrypt.gensalt())
    user=User(name=name, surname=surname, email=email, user_type=UserType.usr, password=hashed)
    db.session.add(user)
    db.session.commit()
    # set token as USED
    pushed_token.is_used = True
    db.session.commit()
    flash('User successfully registered')
    return "Your account is ready for login. Go to your Android app and try this on!"

@app.route('/invite',methods=['GET','POST'])
@login_required
def invite():
    # display invite template
    if request.method == 'GET':
        return render_template('invite.html')
    # get email from template
    email = request.form['email']

    # checking if email exists in database
    email_check = User.query.filter_by(email=email).first()
    if email_check:
        return render_template('invite.html', exist="Ten e-mail juz istnieje w bazie danych!!!")

    # create a client for sending emails
    sg = sendgrid.SendGridAPIClient(apikey=os.environ.get('SENDGRID_API_KEY'))
    # email's data
    from_email = Email("devops-nokia@heroku.com")
    subject = "You got invited to awesome app!"
    to_email = Email(email)
    content = Content("text/plain", "Hello, World!")

    # generates token for an email
    token=Tokens(token=generate_registration_token(email), email=email, date=datetime.datetime.now()+datetime.timedelta(days=7))
    
    content = Content("text/plain", "Hello! You've got invited to DevOps project. To continue "+
                      "the registration click this link: "+
                      "https://devops-nokia.herokuapp.com/register/"+token.token +
                      " You have 7 days for sign up, after that your token will be deactivated.")

    # creatin the mail
    mail = Mail(from_email, subject, to_email, content)
    # sending the email
    response = sg.client.mail.send.post(request_body=mail.get())
    db.session.add(token)
    db.session.commit()

    flash('E-mail sent successfully')
    return redirect(request.args.get('next') or url_for('index'))

@app.errorhandler(404)
def page_not_found(error):
    return 'fail'

@app.route("/test")
def testJSON():
    return "Kapibara", 200

@app.route('/loginandroid',methods=['GET','POST'])
def loginandroid():
    if request.method == 'GET':
        return "Brak dostepu!", 403
    email = request.form['email']
    password = request.form['password']
    #tu dodac jeszcze or registered_user.is_admin == True:
    registered_user = User.query.filter_by(email=email).first()
    if registered_user is None:
        flash('The user does not exist!' , 'error')
        return "Uzytkownik nie istnieje", 403
    check_pass = bcrypt.checkpw(password.encode(), registered_user.password.encode())
    if check_pass == False:
        flash('Email or Password is invalid' , 'error')
        return "Bledne haslo", 403
    login_user(registered_user)
    flash('Logged in successfully')
    return jsonify({
               "error": False,
               "uid": registered_user.id,
               "user": {
               "name": registered_user.name,
               "surname": registered_user.surname,
               "email": registered_user.email
               }
               }), 200
