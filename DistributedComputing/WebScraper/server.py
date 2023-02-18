import json
import memos
import os
import re
import socket
import sqlite3
import threading

logging = {}
def log(string, level):
    if level in logging or "all" in logging:
        length = len(level)
        print(f"{level}{' '*(10 - length)}", end=':')
        print(string)

class Router():
    FILE_DIR = os.path.dirname(os.path.realpath(__file__))
    imageFormats = {'.png', '.jpg', '.jpeg'}

    def __init__(self, request, session):
        self.routes = {
            'GET': self.get,
            'POST': self.add,
            'PUT': self.update,
            'DELETE': self.delete
        }
        log("router", "router")
        self.session = session
        self.body = request.body
        self.fullpath = Router.FILE_DIR + request.path
        self.pathPieces = request.path.strip('/').split('/')
        self.method = request.method

    def route(self, response):
        log("routing", "router")
        log(self.pathPieces, "router")
        if self.pathPieces[0] == 'api':
            if self.session:
                if self.pathPieces[1] == 'code':
                    print("executing remote code")
                    response.setStatus(200)
                    try:
                        response.addBody(str(eval(path[10:])))
                    except Exception as e:
                        response.setStatus(500)
                        response.addBody(str(e))
                        print(e)
                elif self.pathPieces[1] == 'memo':
                    self.routes[self.method](response)
                else:
                    response.setStatus(501)
            else:
                response.setStatus(401)

        else:
            path = self.fullpath
            filename = path if os.path.isfile(path) else path + '/index.html'
            try:
                contentType = re.search('\.[^\.]+$', path)
                if contentType and contentType.group(0) in Router.imageFormats:
                    response.addBody(filename=filename, filetype='image')
                    response.addHeader(f"content-length: {os.path.getsize(path)}")
                    response.addHeader(f"content-type: image/{contentType.group(0)[1:]}")
                else:
                    response.addBody(filename=filename)
                response.setStatus(200)
            except FileNotFoundError:
                response.setStatus(404)

    def get(self, response):
        result = memos.get()
        if isinstance(result, list):
            if result:
                response.addBody(json.dumps(result))
                response.setStatus(200)
            else:
                response.setStatus(204)
        else:
            response.setStatus(500)

    def delete(self, response):
        if len(self.pathPieces) < 3 or not self.pathPieces[2].isnumeric():
            response.setStatus(400)
        else:
            result = memos.delete(self.pathPieces[2])
            if result:
                response.setStatus(200)
            else:
                response.setStatus(500)

    def add(self, response):
        body = self.body
        if 'name' not in body or 'note' not in body or len('name') == 0:
            response.setStatus(400)
        else:
            result = memos.add(body['name'], body['note'], self.session)
            if result:
                response.addBody(json.dumps(result))
                response.setStatus(201)
            else:
                response.setStatus(500)

    def update(self, response):
        body = self.body
        if 'note' not in body or len(self.pathPieces) < 3 \
           or not self.pathPieces[2].isnumeric():
            response.setStatus(400)
        else:
            result = memos.update(int(self.pathPieces[2]), body['note'], self.session)
            if result:
                response.addBody(json.dumps(result))
                response.setStatus(200)
            else:
                response.setStatus(500)

class Request():
    def __init__(self, data):
        log("received request", "request")
        self.text = data.decode()
        log(self.text, "request");
        header, *body = self.text.split("\r\n\r\n") 
        
        if body[0]: 
            self.body = json.loads(body[0])
        else:
            self.body = ''
        log('header', "request")
        log(header, "request")
        log('body', "request")
        log(body, "request")
        headerLines = [headerLine.strip('\r') for headerLine in header.split("\n")]
        self.cookies = self.parseCookies(headerLines)
        self.method, self.path, self.version = headerLines[0].split()

    def __str__(self):
        return self.text

    def parseCookies(self, headerLines):
        log("parsing cookies", "request")
        cookies = {}
        for line in headerLines:
            if re.match("^Cookie: ", line):
                pairs = line.split(" ")[1:]
                for pair in pairs:
                    pair = pair.strip(";").split("=")
                    cookies[pair[0]] = pair[1]
        return cookies

class Server():
    def __init__(self, port=50002):
        self.sessions = set()
        self.sessionCount = 0
        self.sessionLock = threading.Lock()

        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server.bind(("", port))
        server.listen()

        self.server = server
    
    def start(self):
        try:
            while True:
                client, clientAddr = self.server.accept()
                threading.Thread(target=self.serve, args=(client, clientAddr)).start()
        except KeyboardInterrupt:
            self.server.close()
        except Exception as e:
            self.server.close()
            print(e)

    def newSessionCookie(self):
        self.sessionLock.acquire(blocking=True)
        self.sessionCount += 1
        self.sessions.add(self.sessionCount)
        self.sessionLock.release()
        return f'Set-Cookie: session={self.sessionCount}'

    def serve(self, client, clientAddr):
        log("serving", "server")
        res = Response(client)
        try:
            req = Request(client.recv(1024))
            log(str(req), "server")
        except Exception as e:
            # bad request
            # get out of here
            print(e)
            res.setStatus(400)
            res.send()
            return True

        try:
            session = self.validateRequest(req, res)
            Router(req, session).route(res)
        except Exception as e:
            print(e)
            res.setStatus(500)
        res.send()

    def validateRequest(self, request, res):
        log("validating request", "server")
        log(request, "server")
        log(request.cookies.items(), "server")
        log("comparing cookies to session:", "server")
        log(self.sessions, "server")
        for key, value in request.cookies.items():
            if key == "session" and int(value) in self.sessions:
                log("cookie found", "server")
                return value

        res.addHeader(self.newSessionCookie())
        return False


class Response():
    statusCodes = {200: 'OK', 201: 'Created', 204: 'No Content', 400: 'Bad Request', 401: 'Unauthorized', 404: 'Not Found', 500: 'Internal Server Error', 501: 'Not Implemented'}
    def __init__(self, client):
        log("making response", "response")
        self.client = client
        self.status = ''
        self.headers = []
        self.body = ''.encode()

    def setStatus(self, num):
        self.status = f'HTTP/1.0 {num} {Response.statusCodes[num]}'

    def addHeader(self, header):
        self.headers.append(header)

    def addBody(self, string='', filename='', filetype=''):
        if filename:
            if filetype == 'image':
                f = open(filename, mode='rb')
                text = f.read()
                self.body = text
            else:
                f = open(filename, mode='r')
                text = f.read()
                self.body = text.encode()
            f.close()
        else:
            self.body = string.encode()

    def send(self):
        log("sending response", "response")
        response = (self.status + '\r\n' + '\r\n'.join(self.headers) + '\r\n\r\n').encode() + self.body
        log(response, "response")
        self.client.sendall(response)
        self.client.close()


if __name__ == '__main__':
    Server().start() 
