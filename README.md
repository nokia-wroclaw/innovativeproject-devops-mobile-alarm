# Mobile phone DevOps alarms delivery - innovative project

## Description

### Project goals

Develop a tool that will notify person via android app that some system or web application has crashed and/or behaves weirdly.

### Application that has following components

* Backend app
  * monitors web services and generates and closes alarms when web applications are not responsive or has failing healthchecks
  * serves the mobile phone app

* Android/mobile phone application
  * Allows for subscription to alarms from particular services
  * Receives and displays alarms and alarm cancellations
  * Allows the technical stuff to “claim“ the alarm (“I‘m working on it“ notification)

Extra: application can be notified about events/alarms from Sensu system.
Extra: backend can be notified about application events (“App x is rebooting for upgrade. Est downtime 30 minutes“)

## Technologies

* Java
* Android
* Python with Flask
* HTML
* CSS
* Bootstrap
* SQLAlchemy
* Celery
* Cloud Messaging - Firebase
* Retrofit

## Team

* Jakub Batogowski - jakub.batogowski@gmail.com
* Marcin Buciora - buciora.marcin@gmail.com
* Marcin Okroy - m.okroy@hotmail.com
* Grzegorz Oliwa - gymnopedia10@gmail.com
* Patryk Witkowski - patrykwitkovski@gmail.com

## Frontend app demo 

Our application is avalible on heroku cloud platform (administration panel):

* http://devops-nokia.herokuapp.com/