import os
from flask import Flask, render_template, request, redirect, url_for

app = Flask(__name__)

@app.route('/')
def home():
    return 'Hello World!!!'


@app.errorhandler(404)
def page_not_found(error):
    return 'fail'


if __name__ == '__main__':
    app.run(debug=True)
