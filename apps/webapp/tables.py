from flask_table import Table, Col, OptCol

class UsersTable(Table):
    classes = ['table', 'table-striped']
    id = Col('#')
    name = Col('Name')
    surname = Col('Surname')
    email = Col('Email')
    user_type = OptCol('Type of user', choices={1: 'Admin', 2: 'User', 3: 'Other'})