# Assignment 1
# Author: Xian Mardiros
# Class: COMP 4300
# Date: Thu. Oct. 20, 2022
# Purpose:
#   Demonstrate knowledge of TCP sockets by creating a basic chatroom application

import socket
import select
import traceback

HOST = ''                 # Symbolic name meaning all available interfaces
PORT = 50007              # Arbitrary non-privileged port
MAX_CAPACITY = 5

# The server keeps a list of logged in users and the existing rooms
# Each room keeps a list of users in the room

class Room():
    def __init__(self, name):
        self.name = name
        self.users = []

    def postMessage(self, sender, msg):
        print(f"Sending {msg} to room {self.name}")
        for user in self.users:
            if user is not sender:
                user.send(msg)

    def removeUser(self, user):
        self.users.remove(user)

class User():
    def __init__(self, name, socket):
        self.name = name
        self.socket = socket
        self.room = None
        pass
    
    def send(self, msg):
        self.socket.sendall(msg.encode())

    def leaveRoom(self):
        if self.room:
            self.room.removeUser(self)
            self.send(f"Left room {self.room.name}")
            self.room = None
        else:
            self.send("You are not in a room")

# The server class, which keeps the overall app state.
# Keeps a list of rooms, clients(sockets), and {socket: user} dictionaries.
# When a message is received, the socket addr is mapped to the user, and what to do is determined by the user's state and message content
# The error handling should ensure that unrecognized or unacceptable input is ignored.
# 
class Server():
    def __init__(self, port=50000):
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server.bind(("", port))
        server.listen()
        self.server = server
        self.currUsers = {}
        self.clients = []
        self.rooms = []

    # accept a new client
    def acceptClient(self):
        print('connecting')
        conn, addr = self.server.accept()
        conn.setblocking(False)
        self.clients.append(conn)

    # remove all user data while ensuring consistent state
    def disconnectUser(self, user, socket):
        if user.room:
            user.leaveRoom()
        del self.currUsers[socket]
        self.clients.remove(socket)
        socket.close()
        print(user.name + ' disconnected')

    # parses the message into a command, and executes the correct functionality
    # returned value will be sent to the client
    def processRequest(self, socket):
        string = socket.recv(1024).decode()
        print('command received: ' + string)
        user = self.currUsers[socket]
        if not string:
            self.disconnectUser(user, socket)
            return
        toks = string.split('|')
        if toks[0] == 'ListRooms':
            print("Listing Rooms")
            header = 'Room List:'
            return "\n".join([header] + [f"{num}: {room.name}" for (num, room) in enumerate(self.rooms)])
        elif toks[0] == 'RoomDetails':
            # is this a number? is there even that many rooms?
            number = int(toks[1])
            header = "RoomDetails"
            room = self.rooms[number]
            name = room.name
            userNum = f"{len(room.users)} Users:"
            return "\n".join([header, userNum] + [user.name for user in room.users])
        elif toks[0] == 'JoinRoom':
            number = int(toks[1])
            room = self.rooms[number]
            name = room.name
            if user in room.users:
                return f"You are already in room {name}"
            if len(room.users) < MAX_CAPACITY:
                room.users.append(user)
                user.room = room
                return f"Joined room {name}"
            else:
                return f"Room {name} is full"
        elif toks[0] == 'CreateRoom':
            self.rooms.append(Room(toks[1]))
            return "Room created"
        elif toks[0] == 'SendMessage':
            room = user.room
            if room:
                room.postMessage(user, toks[1])
            else:
                return "You must join a room before you can send messages"
            pass
        elif toks[0] == 'LeaveRoom':
            user.leaveRoom()
        elif toks[0] == 'Disconnect':
            if user.room:
                user.leaveRoom()
            # del self.currUsers[socket]
            user.send('Disconnecting')
            self.disconnectUser(user, socket)

    # the first message received is always the username
    def login(self, socket):
        name = socket.recv(1024).decode()
        print(name + ' logged in')
        if name:
            self.currUsers[socket] = User(name, socket)
            socket.sendall("Connected".encode())
        else:
            self.clients.remove(socket)
            socket.close()

    # Zhu Li, do the thing!
    def start(self):
        print('starting')
        try:
            while True:
                try:
                    readable, _, _ = select.select( [self.server, ] + self.clients, [], [], 0)
                    for socket in readable:
                        if socket is self.server:
                            self.acceptClient()
                            continue
                        if socket not in self.currUsers:
                            self.login(socket)
                        else:
                            response = self.processRequest(socket)
                            if response:
                                socket.sendall(response.encode())
                except ValueError as e:
                    print(self.server)
                    print(e)
                    print(traceback.format_exc())
                    print(self.clients)
                    for sock in self.clients:
                        print(sock)
                        try:
                            sock.getpeername()
                        except Exception:
                            print(sock.fileno())
                            self.clients.remove(sock)
                            break
        except Exception as e:
            self.server.close()
            print(e)
            print(traceback.format_exc())
        except KeyboardInterrupt:
            self.server.close()


if __name__ == '__main__':
    Server().start()
