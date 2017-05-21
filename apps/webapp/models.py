from webapp import db, ServiceState

class User(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    name=db.Column(db.String(15), index=True)
    surname=db.Column(db.String(40), index=True)
    email = db.Column(db.String(80), index=True)
    password = db.Column(db.String(100), index=True)
    fcm_token = db.Column(db.String(200), index=True)
    organizations = db.relationship("User_Organization_mapping", back_populates="user")
    services=db.relationship("Subscription", back_populates="user")
    in_repairing = db.relationship("Service")
    
    def __init__(self, name, surname, email, password):
        self.name = name
        self.surname = surname
        self.email = email
        self.password = password

    def __repr__(self):
        return 'Name: {0} \nSurname: {1} \nE-mail: {2}'.format(self.name, self.surname, self.email)

    def is_authenticated(self):
        return True

    def is_active(self):
        return True
 
    def is_anonymous(self):
        return False
 
    def get_id(self):
        return unicode(self.id)

class Organization(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    name=db.Column(db.String(80), index=True)
    users=db.relationship("User_Organization_mapping", back_populates="organization")
    services=db.relationship("Service", backref='organization')

    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return 'Name: {0}'.format(self.name)

class User_Organization_mapping(db.Model):
    id_user=db.Column(db.Integer, db.ForeignKey('user.id'), primary_key=True)
    id_organization=db.Column(db.Integer, db.ForeignKey('organization.id'), primary_key=True)
    user_type=db.Column(db.Integer)
    user=db.relationship("User", back_populates="organizations")
    organization=db.relationship("Organization", back_populates="users")

    @property
    def id(self):
        return self.user.id

    @property
    def name(self):
        return self.user.name
    
    @property
    def surname(self):
        return self.user.surname
    
    @property
    def email(self):
        return self.user.email

class Service(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    address=db.Column(db.String(100), index=True)
    name=db.Column(db.String(80), index=True)
    time_of_last_change_of_state=db.Column(db.DateTime())
    previous_state = db.Column(db.Integer, index=True)
    current_state = db.Column(db.Integer, index=True)
    #fcm_token_group=db.Column(db.String(100), index=True)
    organization_id=db.Column(db.Integer, db.ForeignKey('organization.id'))
    service_repairer_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=True)
    users=db.relationship("Subscription", back_populates="service")
    
    def __init__(self, address, name, organization_id):
        self.address=address
        self.name=name
        self.organization_id=organization_id
        self.previous_state = ServiceState.unspecified
        self.current_state = ServiceState.unspecified


    def dump(self):
        return {"service": {'id': self.id,
                'address': self.address,
                'name': self.name,
                'organization_id': self.organization_id,
                'service_repairer_id': self.service_repairer_id,
                'repairer_email': self.email}}

    @property
    def email(self):
        if self.service_repairer_id != None:
            return User.query.filter_by(id=self.service_repairer_id).first().email
        elif self.service_repairer_id == None:
            return 0

class Subscription(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    id_user=db.Column(db.Integer, db.ForeignKey('user.id'), primary_key=True)
    id_service=db.Column(db.Integer, db.ForeignKey('service.id'), primary_key=True)
    status=db.Column(db.Integer, index=True)
    user=db.relationship("User", back_populates="services")
    service=db.relationship("Service", back_populates="users")

    def dump(self):
        return {"subscritpion": {
                'id_service': self.id_service,
                'status': self.service_state}}

    @property
    def fcm_token(self):
        return self.user.fcm_token

    @property
    def service_state(self):
        return self.service.current_state

class Tokens(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    token=db.Column(db.String(100), index=True, unique=True)
    email=db.Column(db.String(80), index=True)
    date_of_expire=db.Column(db.DateTime())
    is_used = db.Column(db.Boolean, index=True)

    def __init__(self, token, email, date):
        self.token=token
        self.email=email
        self.date_of_expire=date
        self.is_used=False

class History(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    address=db.Column(db.String(100), index=True)
    name=db.Column(db.String(80), index=True)
    state = db.Column(db.Integer)
    state_set_time=db.Column(db.DateTime())
    organization_id=db.Column(db.Integer, db.ForeignKey('organization.id'))

    def __init__(self, address, name, state, state_set_time, organization_id):
        self.address=address
        self.name=name
        self.state=state
        self.state_set_time=state_set_time
        self.organization_id=organization_id

class Stats(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    name=db.Column(db.String(80), index=True)
    hour_counter=db.Column(db.Integer)
    day_counter=db.Column(db.Integer)
    week_counter=db.Column(db.Integer)
    month_counter=db.Column(db.Integer)
    service_id=db.Column(db.Integer, db.ForeignKey('service.id'))

    def __init__(self, name, service_id):
        self.name = name
        self.hour_counter=0
        self.day_counter=0
        self.week_counter=0
        self.month_counter=0
        self.service_id=service_id