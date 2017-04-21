from flask import Flask, session, request, flash, url_for, redirect, render_template, abort, g, jsonify
from flask_login import login_user, logout_user, current_user, login_required
from webapp import db, app, login_manager, UserType
from .models import *
import sendgrid
import os
from sendgrid.helpers.mail import *
import bcrypt
import datetime
from functions import generate_registration_token, confirm_token
from tables import UsersTable, ServicesTable
import json
from bson import json_util

@login_manager.user_loader
def load_user(id):
    return User.query.get(int(id))

@app.route('/')
@app.route('/index')
@login_required
def index():
    return redirect(url_for('dashboard'))

@app.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('login'))

@app.before_request
def before_request():
    g.user = current_user

@app.route('/login',methods=['GET','POST'])
def login():
    # display login template
    if request.method == 'GET':
        return render_template('login.html', title="DevOps")
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
    org_usr_mapp = User_Organization_mapping.query.filter_by(id_user=registered_user.id).first()
    if check_pass == False or org_usr_mapp.user_type != UserType.adm:
        flash('Email or Password is invalid' , 'error')
        return redirect(url_for('login'))
    # login user
    login_user(registered_user)
    flash('Logged in successfully')
    return redirect(request.args.get('next') or url_for('dashboard'))

@app.route('/register/<token>/<organization_id>' , methods=['GET','POST'])
def register(token, organization_id):
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
            return render_template('register.html', title="DevOps", registerAdmin="false")

    email = confirm_token(token)
    name = request.form['name']
    surname = request.form['surname']
    password = request.form['password']
    password_bytes = password.encode('utf-8')
    hashed = bcrypt.hashpw(password_bytes, bcrypt.gensalt())
    user=User(name=name, surname=surname, email=email, password=hashed)
    db.session.add(user)
    db.session.commit()
    org_usr_mapp=User_Organization_mapping(id_user=user.id, id_organization=organization_id, user_type=2)
    db.session.add(org_usr_mapp)
    pushed_token.is_used = True
    db.session.commit()
    flash('User successfully registered')
    return "Your account is ready for login. Go to your Android app and try this on!"

#admin registration
@app.route('/register' , methods=['GET','POST'])
def register_admin():
    if request.method == 'GET':
        return render_template('register.html', title="DevOps", registerAdmin="true")

    email = request.form['email']
    name = request.form['name']
    organization = request.form['organization']
    surname = request.form['surname']
    password = request.form['password']
    password_bytes = password.encode('utf-8')
    hashed = bcrypt.hashpw(password_bytes, bcrypt.gensalt())
    user=User(name=name, surname=surname, email=email, password=hashed)
    org_usr_mapp=User_Organization_mapping(user_type=1)
    org_usr_mapp.organization=Organization(name=organization)
    user.organizations.append(org_usr_mapp)
    db.session.add(user)
    db.session.add(org_usr_mapp)
    db.session.commit()

    # set token as USED
    flash('User successfully registered')
    return redirect(request.args.get('next') or url_for('login'))


@app.route('/invite',methods=['GET','POST'])
@login_required
def invite():
    # get id_organization of specific admin
    org_id = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()
    
    # display invite template
    if request.method == 'GET':
        # creating a table of users
        items = db.session.query(User_Organization_mapping).filter_by(id_organization=org_id.id_organization).all()
        users_table = UsersTable(items)
        return render_template('users.html', users_table=users_table, panel="users", org_name=org_id.organization.name)
    
    # get email from template
    email = request.form['email']

    # checking if email exists in database
    email_check = User.query.filter_by(email=email).first()
    if email_check:
        return render_template('users.html', exist="Ten e-mail juz istnieje w bazie danych!!!")

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
                      "https://devops-nokia.herokuapp.com/register/"+token.token+"/"+str(org_id.id_organization)+
                      " You have 7 days for sign up, after that your token will be deactivated.")

    # creatin the mail
    mail = Mail(from_email, subject, to_email, content)
    # sending the email
    response = sg.client.mail.send.post(request_body=mail.get())
    db.session.add(token)
    db.session.commit()

    flash('E-mail sent successfully')
    return redirect(request.args.get('next') or url_for('invite'))

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

@app.route('/logoutandroid')
def logoutandroid():
    logout_user()
    return "Success", 200

@app.route('/servicesandroid')
def servicesandroid():
    if current_user.is_authenticated():
        org_id = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()
        items=db.session.query(Service).filter_by(organization_id=org_id.id_organization).all()

        return json.dumps([o.dump() for o in items], default=json_util.default)
    else:
        return "Blad", 400

@app.route('/dashboard', methods=['GET','POST'])
@login_required
def dashboard():
    user = g.user
    return render_template('dashboard.html', title="DevOps Nokia Project", user=user, panel="dashboard")

@app.route('/services', methods=['GET','POST'])
@login_required
def services():
    org_id = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()
    if request.method == 'GET':
        # creating a table of services
        items = db.session.query(Service).filter_by(organization_id=org_id.id_organization).all()
        services_table = ServicesTable(items)

        return render_template('services.html', services_table=services_table, panel="services", org_name=org_id.organization.name)
    
    # get the values from the template
    address = request.form['service_address']
    name = request.form['service_name']

    # checking if a service with a given address or name does not exist
    address_check = Service.query.filter_by(address=address, organization_id=org_id.id_organization).first()
    name_check = Service.query.filter_by(name=name, organization_id=org_id.id_organization).first()

    # if exists redirect to the error page
    if address_check or name_check:
        return redirect(url_for('services'))

    # creating a new user
    new_service = Service(address=address, name=name, organization_id=org_id.id_organization)
    db.session.add(new_service)
    db.session.commit()

    return redirect(request.args.get('next') or url_for('services'))

