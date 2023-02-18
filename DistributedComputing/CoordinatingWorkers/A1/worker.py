#!/usr/bin/python3
import json
import random
import select
import socket
import sys
import time

logging = 0
def log(string):
	if logging:
		print(string)

def stall():
	time.sleep(random.random()/8 + 0.01)

class Worker():
	def __init__(self):
		self.msgr = "WORKER RECV:      "
		self.msgs = "WORKER SEND:      "
		self.locks = {}
		self.currId = None
		with open("port.txt", "r") as p:
			self.PORT = int(p.read())
		with open("host.txt", "r") as h:
			self.HOST = h.read()
		self.database = {}

	def lock_response(self, key, accept):
		obj = {"type": "SET-RESPONSE", "id": self.getCurrId(), "key": key, "response": "YES" if accept else "NO"}
		return json.dumps(obj).encode()

	def getCurrId(self):
		currId = self.currId
		self.currId = None
		return currId

	def workerloop(self):
		global socket # not sure why this line is needed, script breaks without it
		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
			self.connect_coordinator(s)

			while True:
				time.sleep(0.1)
				data = s.recv(1024).decode()
				if data:
					cmds = [cmd + "}" for cmd in data.split("}")][0:-1]
					for cmd in cmds:
						req = json.loads(cmd)
						if isinstance(req, dict) and "type" in req:
							stall()
							log(self.msgr + data)
							requestType = req["type"]

							if "key" in req:
								key = req["key"]
							if "value" in req:
								value = req["value"]
							if "id" in req:
								self.currId = req["id"]

							if requestType == "KILL":
								break

							elif requestType == "GET-DB":
								db = self.database
								db["id"] = self.getCurrId()
								log(self.msgs + str(db))
								s.sendall(json.dumps(db).encode())

							elif requestType == "SET-LOCK":
								accept = key not in self.locks
								if accept:
									self.locks[key] = value
								res = self.lock_response(key, accept)
								log(self.msgs + str(res))
								s.sendall(res)

							elif requestType == "SET-COMMIT":
								self.database[key] = self.locks[key]
								del self.locks[key]
								self.acknowledge(s)

							elif requestType == "SET-ABORT":
								del self.locks[key]
								self.acknowledge(s)

							elif requestType == "GET":
								res = self.get_response(req["key"])
								log(self.msgs + str(res))
								s.sendall(res)
	
	def connect_coordinator(self, socket):
		socket.connect((self.HOST, self.PORT))
		addMsg = json.dumps({"type": "ADD"}).encode()
		#print(self.msgs + str(addMsg))
		socket.sendall(addMsg)

	def get_response(self, key):
		if key in self.database:
			res = {"type": "GET-RESPONSE", "id": self.getCurrId(), "key": key, "value": self.database[key]}
		else:
			res = {"type": "GET-RESPONSE", "id": self.getCurrId(), "key": key, "value": None}
		return json.dumps(res).encode()
	
	def acknowledge(self, socket):
		res = {"type": "ACK", "id": self.getCurrId()}
		socket.sendall(json.dumps(res).encode())

if __name__ == '__main__':
	Worker().workerloop()

						
