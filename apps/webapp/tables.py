from flask_table import Table, Col, OptCol, ButtonCol

class UsersTable(Table):
    classes = ['table', 'table-striped']
    #id = Col('#')
    name = Col('Name')
    surname = Col('Surname')
    email = Col('Email')
    user_type = OptCol('Type of user', choices={1: 'Admin', 2: 'User', 3: 'Other'})
    remove = ButtonCol('Remove', 'remove_user', url_kwargs=dict(id='id'), button_attrs={'class': 'remove-btn'})

class ServicesTable(Table):
    classes = ['table', 'table-striped']
    states = {1: 'Up', 2: 'Down', 3: 'Unspecified'}
    #id = Col('#')
    address = Col('Address')
    name = Col('Name')
    time_of_last_change_of_state = Col('State changed in')
    previous_state = OptCol('Previous state', choices=states)
    current_state = OptCol('Current state', choices=states)
    remove = ButtonCol('Remove', 'remove_service', url_kwargs=dict(id='id'), button_attrs={'class': 'remove-btn'})

class HistoryTable(Table):
    classes = ['table', 'table-striped']
    states = {1: 'Up', 2: 'Down', 3: 'Unspecified'}
    address = Col('Address')
    name = Col('Name')
    state = OptCol('State', choices=states)
    state_set_time = Col('State changed in')

class StatisticsTable(Table):
    classes = ['table', 'table-striped']
    name = Col('Name')
    delta_uptime = Col('Uptime (delta)')
    percentage_uptime = Col('Uptime (%)')

class StatItem(object):
    def __init__(self, name, delta_uptime, percentage_uptime):
        self.name = name
        self.delta_uptime = delta_uptime
        self.percentage_uptime = percentage_uptime

