from flask_wtf import Form
from wtforms import StringField, BooleanField
from wtforms.validators import DataRequired

class LoginForm(Form):
    inputEmail = StringField('inputEmail', validators=[DataRequired()])
    inputPassword = StringField('inputPassword', validators=[DataRequired()])