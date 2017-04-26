from itsdangerous import URLSafeTimedSerializer
from webapp import app
import json
import requests

def generate_registration_token(email):
    serializer = URLSafeTimedSerializer(app.config['SECRET_KEY'])
    return serializer.dumps(email, salt=app.config['SECURITY_PASSWORD_SALT'])

def confirm_token(token, expiration=3600):
    serializer = URLSafeTimedSerializer(app.config['SECRET_KEY'])
    try:
        email = serializer.loads(
                                 token,
                                 salt=app.config['SECURITY_PASSWORD_SALT'],
                                 max_age=expiration
                                 )
    except:
        return False
    return email

# function sending notification when state of service has change
def send_notification( icon, service, state ):
    json_data = { "registration_ids" : [ o.fcm_token for o in service.users ] , "notification" : { 'icon' : icon, 'sound' : 'default', 'tag' : service.id, 'title_loc_key' : 'fcm_message_service_state_title', 'title_loc_args' : [ service.name ], 'body_loc_key' : 'fcm_message_service_state_body', 'body_loc_args' : [ state ] } }
    json_string = json.dumps(json_data)
    headers = {'Content-Type': 'application/json', 'Authorization': 'key='+app.config['FCM_APP_TOKEN']}
    #print json_string
    res = requests.post('https://fcm.googleapis.com/fcm/send', headers=headers, data=json_string)
    #print 'response from server:', json.dumps(res.text)
    
    return res.text