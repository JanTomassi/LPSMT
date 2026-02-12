from flask import Flask
import sqlite3
import re
import sys
import requests
import base64

app = Flask(__name__)

@app.route('/search/<title>')
def query_for_comics(title: str):
    def regexp(y, x, search=re.search):
        return 1 if search(y, x) else 0
    
    con = sqlite3.connect("../db/mangaCheck.db")

    con.create_function('regexp', 2, regexp)

    cur = con.cursor()

    res = cur.execute("SELECT id,title from manga WHERE lower(title) REGEXP ?", [r'(?i)^{}.*'.format(title)]).fetchall()

    return res

@app.route('/image/<id>')
def query_for_image(id: str):
    def regexp(y, x, search=re.search):
        return 1 if search(y, x) else 0
    
    con = sqlite3.connect("../db/mangaCheck.db")

    con.create_function('regexp', 2, regexp)

    cur = con.cursor()

    res = cur.execute("SELECT coverImageMedium from manga WHERE id = ?", [id]).fetchall()

    if (len(res) == 0):
        return('',404)

    image_url = res[0][0]

    request_bitmap = requests.get(
        image_url
    )

    if(request_bitmap.status_code not in range(200,300)):
        return('',404)
    else:
        return base64.b64encode(request_bitmap.content)
