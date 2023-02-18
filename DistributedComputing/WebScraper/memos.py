import sqlite3
import threading

DB_NAME = "memos.db"

connectionLock = threading.Lock()
connection = sqlite3.connect(DB_NAME, check_same_thread=False)
cursor = connection.cursor()
cursor.execute("select name from sqlite_master where type='table';")
if not 'memos' in [tablename[0] for tablename in cursor.fetchall()]:
    cursor.execute("CREATE TABLE memos(name TEXT, note TEXT, modified TEXT)")
    connection.commit()
    

def add(name, note, session):
    try:
        connectionLock.acquire()
        cur = connection.cursor()
        cur.execute(f'INSERT INTO memos VALUES ("{name}", "{note}", "{session}");')
        result = {'id': cur.lastrowid, 'name': name, 'note': note, 'modified': session}
        connection.commit()
    except Exception as e:
        print('sqlite insert error')
        print(e)
        result = False
    connectionLock.release()
    return result

def update(mid, note, session):
    # make sure the memo still exists
    try:
        connectionLock.acquire()
        cur = connection.cursor()
        cur.execute(f'''UPDATE memos SET note = "{note}", modified = "{session}" WHERE _rowid_ = {mid};''')
        connection.commit()
        result = {'note': note, 'modified': session}
    except Exception as e:
        print('sqlite update error')
        print(e)
        result = False
    connectionLock.release()
    return result

def has(name):
    cursor = connection.cursor()
    cursor.execute(f'''
        SELECT *
        FROM memos
        WHERE name = "{name}";''')
    connection.commit()
    return len(cursor.fetchall()) > 0

def delete(mid):
    # what if it's already been deleted?
    # then show success => desired result is achieved
    try:
        connectionLock.acquire()
        cur = connection.cursor()
        cur.execute(f'''
                DELETE FROM memos
                WHERE _rowid_ = "{mid}";''')
        result = True
        connection.commit()
    except Exception as e:
        print('sqlite delete error')
        print(e)
        result = False
    connectionLock.release()
    return result

def get():
    try:
        cursor = connection.cursor()
        cursor.execute(f'''
            SELECT _rowid_, * FROM memos;''')
        result = [{'id': row[0], 'name': row[1], 'note': row[2], 'modified': row[3]} for row in cursor.fetchall()]
        connection.commit()
    except Exception as e:
        print('sqlite get error')
        print(e)
        result = False
    return result

