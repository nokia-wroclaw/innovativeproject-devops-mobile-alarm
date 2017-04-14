from flask_table import Table, Col, OptCol
#from models import User_Organization_mapping

class UsersTable(Table):
    classes = ['table', 'table-striped']
    id = Col('#')
    name = Col('Name')
    surname = Col('Surname')
    email = Col('Email')
    #user_type = OptCol('Type of user', choices={1: 'Admin', 2: 'User', 3: 'Other'})

class ServicesTable(Table):
    classes = ['table', 'table-striped']
    states = {1: 'Up', 2: 'Down', 3: 'Unspecified'}
    id = Col('#')
    address = Col('Address')
    name = Col('Name')
    time_of_last_change_of_state = Col('State changed in')
    previous_state = OptCol('Previous state', choices=states)
    current_state = OptCol('Current state', choices=states)