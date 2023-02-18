import cmd
import time
import sys
import re
import json
import socket


class TestShell(cmd.Cmd):
    intro = '' if "--no-prompt" in sys.argv else 'Welcome to the 3010 verifier shell.   Type help or ? to list commands.\n'
    prompt = '' if "--no-prompt" in sys.argv else '3010 > '
    coordinatorSock = None

    def preloop(self) -> None:
        '''
        Connect to the coordinator
        '''
        try:
            self.coordinatorSock = socket.socket(
                socket.AF_INET, socket.SOCK_STREAM)
            #parts = sys.argv[1].split(':')
            # there is a more pythonic way. Not today!
            #who = (parts[0], int(parts[1]))
            with open("port.txt", "r") as p:
              port = int(p.read())
            with open("host.txt", "r") as h:
              host = h.read()
            #print("Connecting to " + host)
            who = (host, port)
            self.coordinatorSock.connect(who)
        except Exception as e:
            print("Could not connnet. Quitting")
            print(e)
            sys.exit(1)

    def do_sleep(self, arg):
        time.sleep(float(arg))

    def do_set(self, arg):
        '''
        Set a value in the database: set key value
        '''
        matches = re.match("(\S+)\s+(\S+)", arg)
        if matches is None:
            print("set requires two arguments: the key and value")
        else:
            key = matches.group(1)
            value = matches.group(2)

            try:
                content = {"type": "SET", "key": key, "value": value}

                #print("Sending  request: " + json.dumps(content))
                self.coordinatorSock.sendall(json.dumps(content).encode())
                # wait for reply
                print(self.coordinatorSock.recv(1024))
            except Exception as e:
                print("Error sending/receiving")
                print(e)

    def do_verify(self, arg):
        try:
            content = {"type": "VERIFY"}

            self.coordinatorSock.sendall(json.dumps(content).encode())
            print(self.coordinatorSock.recv(1024))
        except Exception as e:
            print("Error sending/receiving")
            print(e)
    def do_get(self, arg):
        '''
        Set a value in the database: set key value
        '''

        if len(arg) == 0:
            print("set requires two arguments: the key and value")
        else:

            try:
                content = {"type": "GET", "key": arg}

                #print("Sending get request: " + json.dumps(content))
                self.coordinatorSock.sendall(json.dumps(content).encode())
                # wait for reply
                print(self.coordinatorSock.recv(1024))
            except Exception as e:
                print("Error sending/receiving")
                print(e)

    def do_exit(self, arg):
        #print('Later, gator.')
        return True

    def do_EOF(self, arg):
        #print("", end="\r")
        #time.sleep(0.5)
        return False

    def postloop(self) -> None:
        try:
            if self.coordinatorSock is not None:
                self.coordinatorSock.close()
        except:
            print("Failed to ")


if __name__ == '__main__':
    TestShell().cmdloop()
