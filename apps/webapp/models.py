from webapp import db

class User(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    name=db.Column(db.String(15), index=True)
    surname=db.Column(db.String(40), index=True)
    email = db.Column(db.String(80), index=True, unique=True)
    password = db.Column(db.String(100), index=True)
    is_admin = db.Column(db.Boolean, index=True)
    
    def __init__(self, name, surname, email, password, is_admin):
        self.name = name
        self.surname = surname
        self.email = email
        self.password = password
        self.is_admin = is_admin

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

class Tokens(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    token=db.Column(db.String(100), index=True, unique=True)
    email=db.Column(db.String(80), index=True, unique=True)
    date_of_expire=db.Column(db.DateTime())
    is_used = db.Column(db.Boolean, index=True)

    def __init__(self, token, email, date):
        self.token=token
        self.email=email
        self.date_of_expire=date
        self.is_used=False