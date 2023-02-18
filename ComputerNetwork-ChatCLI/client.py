# Assignment 1
# Author: Xian Mardiros
# Class: COMP 4300
# Date: Thu. Oct. 20, 2022
# Purpose:
#   Client-side of application
import socket
import select
import threading

instructions = """Valid Commands
All commands must start with '#', everything else will be interpreted as a message to a chatroom.
Valid Commands:
#CreateRoom <name>
#ListRooms
#RoomDetails <number>
#JoinRoom <number>
#LeaveRoom <number>
#Disconnect
"""

PORT=50000
# Its really simple: ask the clients name, and ask for more input while printing output and new messages in a forever-loop
# It actually took a bit of work to ensure that new messages and other output doesn't overwrite the prompt.
class Client():
    def __init__(self):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect(('', PORT))
            self.socket = s
            threading.Thread(target=self.readServer, args=()).start()
            print('What is your name?\n>>>', end='')
            try:
                name = input()
                s.sendall(name.encode())
                print(instructions)
                while True:
                    # print('>>>', end='')
                    # print('\r' + s.recv(1024).decode())
                    # print('waiting')
                    cmd = input()
                    sending = ''
                    if cmd[0] == '#':
                        sending = '|'.join(cmd[1:].split())
                    else:
                        sending = 'SendMessage|' + cmd[0:]
                    print('\r>>>', flush=True, end='')
                    s.sendall(sending.encode())
            except Exception as e:
                s.close()
                print(e)
            except KeyboardInterrupt:
                s.close()

    # read for new output and new messages. When found, print and print a new prompt
    def readServer(self):
        while True:
            res = self.socket.recv(1024).decode()
            if len(res) == 0:
                break
            print('\r            \r' + res)
            print('>>>', flush=True, end='')
            

if __name__ == '__main__':
    Client()
