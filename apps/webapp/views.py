from flask import Flask, session, request, flash, url_for, redirect, render_template, abort, g, jsonify, Response
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

@app.route('/test_service')
def test_service():
    #return "Internal Server Error", 500
    return "OK!", 200

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
    
    # register user which already exist in db
    email = confirm_token(token)
    user = User.query.filter_by(email=email).first()
    if user is not None:
        org_usr_mapp=User_Organization_mapping(id_user=user.id, id_organization=organization_id, user_type=2)
        db.session.add(org_usr_mapp)
        pushed_token.is_used = True
        db.session.commit()
        flash('User successfully registered')
        return "You are register to other organization! Now you joined to this organization too."
    
    if request.method == 'GET':
        return render_template('register.html', title="DevOps", registerAdmin="false")

    name = request.form['name']
    surname = request.form['surname']
    password = request.form['password']
    password_bytes = password.encode('utf-8')
    hashed = bcrypt.hashpw(password_bytes, bcrypt.gensalt())
    
    # register new user
    user=User(name=name, surname=surname, email=email, password=hashed)
    db.session.add(user)
    db.session.commit()
    org_usr_mapp=User_Organization_mapping(id_user=user.id, id_organization=organization_id, user_type=2)
    db.session.add(org_usr_mapp)
    pushed_token.is_used = True
    db.session.commit()
    flash('User successfully registered')
    return "Your account is ready for login. Go to your Android app and try this on!"

@app.route('/remove_service', methods=['GET', 'POST'])
@login_required
def remove_service():
    if request.method == 'POST':
        id = request.args.get('id')
        subscriptions=Subscription.query.filter_by(id_service=id).all()
        serv=Service.query.filter_by(id=id).first()
       
        for sub in subscriptions:
            db.session.delete(sub)
        
        db.session.delete(serv)
        db.session.commit()
    return redirect(request.args.get('next') or url_for('services'))

@app.route('/remove_user', methods=['GET', 'POST'])
@login_required
def remove_user():
    if request.method == 'POST':
        id = request.args.get('id')
        org = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()

        services_of_organization = Service.query.filter_by(organization_id=org.id_organization).all()
        subscriptions = Subscription.query.filter_by(id_user=id).all()
        membership_to_organization = User_Organization_mapping.query.filter_by(id_user=id, id_organization=org.id_organization).first()
       
       # we have to delete just a subscriptions of services which are belong to organization from which we remove user
        for sub in subscriptions:
            for serv in services_of_organization:
                if sub.id_service == serv.id:
                    db.session.delete(sub)
        
        # we remove a user just from admin's organization and just if that user is not an admin 
        if membership_to_organization.user_type != UserType.adm:
            db.session.delete(membership_to_organization)
            user = User.query.filter_by(id=id).first()
            # if user do not belong to any organization we remove him
            if user.organizations == []:
                db.session.delete(user)
        
        db.session.commit()
    return redirect(request.args.get('next') or url_for('users'))

#admin registration
@app.route('/register' , methods=['GET','POST'])
def register_admin():
    if request.method == 'GET':
        return render_template('register.html', title="DevOps", registerAdmin="true")

    email = request.form['email']
    name = request.form['name']
    organization = request.form['organization']

    if Organization.query.filter_by(name=organization).first() is not None:
        return redirect(request.args.get('next') or url_for('register_admin'))

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


@app.route('/users',methods=['GET','POST'])
@login_required
def users():
    # get id_organization of specific admin
    org_id = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()
    
    # display invite template
    if request.method == 'GET':
        # creating a table of users
        items = db.session.query(User_Organization_mapping).filter_by(id_organization=org_id.id_organization).all()
        users_table = UsersTable(items)
        return render_template('users.html', users_table=users_table, panel="users", org_name=org_id.organization.name)

@app.errorhandler(404)
def page_not_found(error):
    return 'fail'

@app.route('/loginandroid',methods=['GET','POST'])
def loginandroid():
    if request.method == 'GET':
        return "Brak dostepu!", 403
    email = request.form['email']
    password = request.form['password']
    fcm_token = request.form['fcm_token']
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

    registered_user.fcm_token = fcm_token
    db.session.commit()

    return jsonify({
               "error": False,
               "uid": registered_user.id,
               "user": {
               "name": registered_user.name,
               "surname": registered_user.surname,
               "email": registered_user.email,
               "fcm_token": registered_user.fcm_token
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

        return Response((json.dumps([o.dump() for o in items], default=json_util.default)), mimetype='application/json')
    else:
        return "Blad", 400

@app.route('/subscriptionandroid', methods=['GET', 'POST'])
def subscriptionandroid():
    if current_user.is_authenticated():
        if request.method == 'GET':
            items=db.session.query(Subscription).filter_by(id_user=g.user.id).all()

            return Response((json.dumps([o.dump() for o in items], default=json_util.default)), mimetype='application/json')

        json_dict = request.get_json()
        id = json_dict['id']
        status = json_dict['status']

        if status == "remove":
            get_sub = Subscription.query.filter_by(id_user=g.user.id, id_service=id).first()
            if get_sub != None:
                db.session.delete(get_sub)
                db.session.commit()
            
                return "Success", 200
        elif status == "add":
            if Subscription.query.filter_by(id_user=g.user.id, id_service=id).first() is None:
                sub=Subscription(id_user=g.user.id, id_service=id, status=1)
                db.session.add(sub)
                db.session.commit()
        
                return "Success", 200
            else:
                return "You've already subscribed this service", 400
        else:
            return "Bad request", 400
    else:
        return "Blad", 400


@app.route('/dashboard', methods=['GET','POST'])
@login_required
def dashboard():
    user = g.user
    org_id = User_Organization_mapping.query.filter_by(id_user=user.id).first()

    up_services = Service.query.filter_by(organization_id=org_id.id_organization, current_state=ServiceState.up).count()
    down_services = Service.query.filter_by(organization_id=org_id.id_organization, current_state=ServiceState.down).count()
    unspecified_services = Service.query.filter_by(organization_id=org_id.id_organization, current_state=ServiceState.unspecified).count()
    all_services = Service.query.filter_by(organization_id=org_id.id_organization).count()

    if all_services != 0:
        percent_up_services = int(float(up_services)/float(all_services)*100)
        percent_down_services = int(float(down_services)/float(all_services)*100)
        percent_unspecified_services = int(float(unspecified_services)/float(all_services)*100)
    else:
        percent_up_services = 0
        percent_down_services = 0
        percent_unspecified_services = 0

    if percent_up_services + percent_down_services + percent_unspecified_services < 100:
        if percent_up_services != 0:
            percent_up_services = percent_up_services + 1
        elif percent_down_services != 0:
            percent_down_services = percent_down_services + 1
        elif percent_unspecified_services != 0:
            percent_unspecified_services = percent_unspecified_services + 1

    return render_template('dashboard.html', title="DevOps Nokia Project", user=user, panel="dashboard", org_name=org_id.organization.name, percent_up_services=percent_up_services, percent_down_services=percent_down_services, percent_unspecified_services=percent_unspecified_services)

@app.route('/services', methods=['GET','POST'])
@login_required
def services():
    org_id = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()
    if request.method == 'GET':
        # creating a table of services
        items = db.session.query(Service).filter_by(organization_id=org_id.id_organization).all()
        services_table = ServicesTable(items)

        return render_template('services.html', services_table=services_table, panel="services", org_name=org_id.organization.name)

@app.route('/invite',methods=['GET','POST'])
@login_required
def invite():
    admin_org = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()

    if request.method == 'GET':
        return render_template('invite.html', panel="invite")

    # get email from template
    email = request.form['email']

    # checking if user exists in current organization
    user = User.query.filter_by(email=email).first()
    if user is not None:
        for org in user.organizations:
            if org.id_organization == admin_org.id_organization:
                return render_template('invite.html', panel="invite", exist="Ten e-mail juz istnieje w biezacej organizacji!!!")

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
                      "https://devops-nokia.herokuapp.com/register/"+token.token+"/"+str(admin_org.id_organization)+
                      " You have 7 days for sign up, after that your token will be deactivated.")

    # creatin the mail
    mail = Mail(from_email, subject, to_email, content)
    # sending the email
    response = sg.client.mail.send.post(request_body=mail.get())
    db.session.add(token)
    db.session.commit()

    flash('E-mail sent successfully')
    return redirect(request.args.get('next') or url_for('invite'))

@app.route('/add_service',methods=['GET','POST'])
@login_required
def add_service():
    org_id = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()

    if request.method == 'GET':
        return render_template('add_service.html', panel="add_service")

    # get the values from the template
    address = request.form['service_address']
    name = request.form['service_name']

    # adding required prefix 'http://' if do not exist
    if address[0:7] != 'http://' and address[0:8] != 'https://':
        address = 'http://' + address

    # checking if a service with a given address or name does not exist
    address_check = Service.query.filter_by(address=address, organization_id=org_id.id_organization).first()
    name_check = Service.query.filter_by(name=name, organization_id=org_id.id_organization).first()

    # if exists redirect to the error page
    if address_check or name_check:
        return redirect(url_for('add_service'))

    # creating a new user
    new_service = Service(address=address, name=name, organization_id=org_id.id_organization)
    db.session.add(new_service)
    db.session.commit()

    return redirect(request.args.get('next') or url_for('add_service'))

@app.route('/settings',methods=['GET','POST'])
@login_required
def settings():
    org = User_Organization_mapping.query.filter_by(id_user=g.user.id).first()

    if request.method == 'GET':
        return render_template('settings.html', org_name=org.organization.name)

    org_name = request.form['organization_name']

    if Organization.query.filter_by(name=org_name).first() is None:
        org.organization.name = org_name
    
    old_password = request.form['old_password']
    check_pass = bcrypt.checkpw(old_password.encode(), g.user.password.encode())

    if check_pass:
        password = request.form['password']
        password_bytes = password.encode('utf-8')
        hashed = bcrypt.hashpw(password_bytes, bcrypt.gensalt())
        g.user.password = hashed

    db.session.commit()

    return redirect(request.args.get('next') or url_for('settings'))
