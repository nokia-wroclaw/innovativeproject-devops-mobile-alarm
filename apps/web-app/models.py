from run import db

class User(db.Model):
    id=db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(120), index=True, unique=True)
    password = db.Column(db.String(50), index=True, unique=False)
    is_admin = db.Column(db.Boolean, index=True)
    
    def __init__(self, email, password):
        self.email = email
        self.password = password
        self.is_admin = False

    def __repr__(self):
        return '<User %r>' % (self.email)


