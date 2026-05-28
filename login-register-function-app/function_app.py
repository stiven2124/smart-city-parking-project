import azure.functions as func
import logging
import pyodbc
import json
import os
from werkzeug.security import generate_password_hash, check_password_hash

app = func.FunctionApp(http_auth_level=func.AuthLevel.ANONYMOUS)

@app.route(route="register")
def register(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Python HTTP trigger function processed a request.')


    try:
        req_body = req.get_json()
        email = req_body.get('email')
        password = req_body.get('password')
        hashed_pass = generate_password_hash(password, method='pbkdf2:sha256')

    except ValueError:
        return func.HttpResponse("Invalid Json", status_code=400)
    
    conn_str = os.environ["SQL_CONNECTION_STRING"]

    try:
        conn = pyodbc.connect(conn_str)
        cursor = conn.cursor()

        cursor.execute("SELECT UserID FROM Users WHERE Email=?" , (email,))
        if cursor.fetchone():
            return func.HttpResponse('{"message":"Email already exists"}', status_code= 400, mimetype="application/json")
        
        cursor.execute("INSERT INTO Users (Email, PasswordHash) VALUES(?, ?)", (email, hashed_pass))
        conn.commit()
        return func.HttpResponse('{"message":"User Created"}', status_code=200, mimetype="application/json")
    
    except Exception as e:
        logging.error(f"Database error: {str(e)}")
        return func.HttpResponse('{"message":"Error"}', status_code=500, mimetype="application/json")
    finally:
        conn.close()

@app.route(route="login")
def login(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Processing Login Request.')

    try:
        req_body = req.get_json()
        email = req_body.get('email')
        password = req_body.get('password')
    except ValueError:
        return func.HttpResponse("Invalid Json", status_code=400)

    conn_str = os.environ["SQL_CONNECTION_STRING"]

    try:
        conn = pyodbc.connect(conn_str)
        cursor = conn.cursor()

        cursor.execute("SELECT PasswordHash FROM Users WHERE Email=?", (email,))
        row = cursor.fetchone()
        if row:
            if check_password_hash(row[0], password):
                return func.HttpResponse('{"message":"LogIn Successful"}', status_code=200, mimetype="application/json")
            
        return func.HttpResponse('{"message":"Invalid cradentials"}', status_code=401, mimetype="application/json")

    except Exception as e:
        logging.error(f"Database error: {str(e)}")
        return func.HttpResponse('{"message":"Error"}', status_code=500, mimetype="application/json")
    finally:
        conn.close()
