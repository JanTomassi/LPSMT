"""
https://graphql.anilist.co'
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
import time

### BEGIN OF GLOBAL VARIABLE

# The structure of the retrived data
QUERY: str = '''
query ($page: Int, $perPage: Int) {
    Page (page: $page, perPage: $perPage){
        pageInfo{
            total
            currentPage
            lastPage
            hasNextPage
            perPage
        }
        media (type: MANGA) {
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
}
'''

# The parameter of research the name is based on the structured
# https://anilist.github.io/ApiV2-GraphQL-Docs/
variables: dict = {
    'page': 0,
    'perPage': 49
}

### END OF GLOBAL VARIABLE

url_anilist: str = 'https://graphql.anilist.co'

if __name__=="__main__":

    # Connection and cursor to the Sqlite3 database file
    con: sqlite3.Connection = sqlite3.connect("./db/mangaCheck.db")
    cur: sqlite3.Cursor = con.cursor()

    # Defining table "manga", every time the table is dropped to be instantly rebuilt
    # cur.execute("DROP TABLE manga")
    cur.execute("CREATE TABLE IF NOT EXISTS manga(id INTEGER PRIMARY KEY, title VARCHAR UNIQUE, coverImageMedium VARCHAR UNIQUE, coverImageLarge VARCHAR UNIQUE, status VARCHAR , volumes INTEGER, chapters INTEGER, description VARCHAR)")


    for i in range(0,100):

        # List of tuples, each tuple is a row in the DB
        entries_list: list = []

        variables["page"] = i
        resp = requests.post(url_anilist, json={'query': QUERY, 'variables': variables})
        print(f"At {i}/100 request left {resp.headers['X-RateLimit-Remaining']}")
        resp = resp.json()

        for manga in resp['data']['Page']['media']:

            if manga['title']['english'] is not None:

                print(f"manga name == {manga['title']['english']}")

                entries_list.append((manga['id'],
                                         manga['title']['english'],
                                         manga['coverImage']['medium'],
                                         manga['coverImage']['large'],
                                         manga['status'],
                                         manga['volumes'],
                                         manga['chapters'],
                                         manga['description']
                                         ))
        # Massive insertion in the DB, entries_list is a list of tuples
        cur.executemany("INSERT OR IGNORE INTO manga VALUES(?, ?, ?, ?, ?, ?, ?, ?)", entries_list)

        # Commit changes on the DB
        con.commit()

        time.sleep(0.75) 

    # Close the connection to the db
    con.close()
