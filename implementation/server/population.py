"""
Firts time building a virtual env for python sorry for mistake.
I think that I respected every good practise.
I tried to insert all type in the variable declaration.
The script rebuild the table each time.
To add new mangas just add new title in titles_lit.

To import packages in pip use:
    ./bin/pip install -r requirements.txt
"""

import sqlite3
import requests

### BEGIN OF GLOBAL VARIABLE

# The structure of the retrived data
QUERY: str = '''
query ($search: String) {
    Media (search: $search, type: MANGA) {
        id
        title {
            english
        }
        coverImage{
            large
            medium
        }
        status
        volumes
        chapters
        description
    }
}
'''

# The parameter of research the name is based on the structured
# https://anilist.github.io/ApiV2-GraphQL-Docs/
variables: dict = {
    'search': '',
}

### END OF GLOBAL VARIABLE

url_anilist: str = 'https://graphql.anilist.co'

if __name__=="__main__":

    # Connection and cursor to the Sqlite3 database file
    con: sqlite3.Connection = sqlite3.connect("./db/mangaCheck.db")
    cur: sqlite3.Cursor = con.cursor()

    # Defining table "manga", every time the table is dropped to be instantly rebuilt
    cur.execute("DROP TABLE manga")
    cur.execute("CREATE TABLE manga(id INTEGER PRIMARY KEY, title VARCHAR UNIQUE, coverImageMedium VARCHAR UNIQUE, coverImageLarge VARCHAR UNIQUE, status VARCHAR , volumes INTEGER, chapters INTEGER, description VARCHAR)")

    # The list of manga titles
    titles_list: list = ['Attack on Titan',
                         'Berserk',
                         'Devilman',
                         'Heads',
                         'Fist of the North Star',
                         'Afro Samurai']

    # List of tuples, each tuple is a row in the DB
    entries_list: list = []

    for title in titles_list:
        variables["search"] = title
        resp: dict = requests.post(url_anilist, json={'query': QUERY, 'variables': variables}).json()
        # Sorry for the syntax of the lines below
        entries_list.append((resp['data']['Media']['id'],
                             resp['data']['Media']['title']['english'],
                             resp['data']['Media']['coverImage']['medium'],
                             resp['data']['Media']['coverImage']['large'],
                             resp['data']['Media']['status'],
                             resp['data']['Media']['volumes'],
                             resp['data']['Media']['chapters'],
                             resp['data']['Media']['description']
                             ))

    # Massive insertion in the DB, entries_list is a list of tuples
    cur.executemany("INSERT INTO manga VALUES(?, ?, ?, ?, ?, ?, ?, ?)", entries_list)

    # Commit changes on the DB
    con.commit()

    # Close the connection to the db
    con.close()
