#!/usr/bin/python3
import random
import time
import select
import socket
import sys
import json

logging = 0
def log(string):
	if logging:
		print(string)

def get_db_request(rId):
	return {"type": "GET-DB", "id": rId}

def abort_request(rId, key):
	return {"type": "SET-ABORT", "id": rId, "key": key}

def commit_request(rId, key):
	return {"type": "SET-COMMIT", "id": rId, "key": key}

def set_lock_request(rId, key, value):
	return {"type": "SET-LOCK", "id": rId, "key": key, "value": value}

def send(socket, request):
	log("COORDINATOR SEND: " + str(request))
	socket.sendall(json.dumps(request).encode())

def readablesockets(socketlist):
	readable, writeable, error = select(socketlist, [], [])
	return readable

def get_response(res):
	return {"type": "GET-RESPONSE", "key": res["key"], "value": res["value"]}

def read_response(socket):
	data = socket.recv(1024)
	res = None
	if data:
		cmds = [cmd + "}" for cmd in data.decode().split("}")][0:-1]
		res = [json.loads(cmd) for cmd in cmds]
	return res

class RequestItem():
	def __init__(self, opId, worker):
		self.opId = opId
		self.worker = worker
		self.sentTime = time.time()
	
	def __str__(self):
		return f"{self.opId}: {self.sentTime} {self.timeToTimeout()}"
	
	def timeToTimeout(self):
		return max(self.sentTime + 1 - time.time(), 0.001)

class Requests():
	# may be better to use a set
	# get soonest timeout, but then process all of the responses received in that time, accessing by requestID
	count = 0
	def __init__(self):
		self.requests = {}

	def __str__(self):
		string = "REQUESTS:\n"
		for req in self.requests.values():
			string += "    " + str(req) + "\n"
		return string
			

	def earliest(self):
		if len(self.requests) == 0:
			return None
		requests = list(self.requests.items())
		earliest = requests[0][1]
		for request in requests:
			if request[1].sentTime < earliest.sentTime:
				earliest = request
		return earliest

	def delete(self, rId):
		req = self.requests[rId]
		del self.requests[rId]
		return req

	def new(self, opId, worker):
		Requests.count += 1
		self.requests[Requests.count] = RequestItem(opId, worker)
		return Requests.count


class Operation():
# bad idea
# should instead stream all inputs and relate them to an operation id
# client requests create a new id which worker requests then refer to
# coordinator keeps a queue of request ids
	opCount = 0


	def __init__(self, coordinator, cmd, client):
		Operation.opCount += 1
		self.opId = Operation.opCount
		self.client = client
		self.cmd = cmd
		self.coordinator = coordinator

	def complete(self):
		self.coordinator.removeOperation(self.opId)

class GetOperation(Operation):
	def __init__(self, coordinator, cmd, client):
		super().__init__(coordinator, cmd, client)
		self.timeoutCount = None
		self.pickAndRetrieve()

	def pickAndRetrieve(self):
		if len(self.coordinator.workers) == 0:
			self.client.sendall(Coordinator.NETWORK_FAILURE)
			self.complete()
		else:
			self.timeoutCount = 0
			self.workers = random.sample(self.coordinator.workers, min(3, len(self.coordinator.workers)))
			for worker in self.workers:
				rId = self.coordinator.requests.new(self.opId, worker)
				self.cmd["id"] = rId
				send(worker, self.cmd)

	def receiveResponse(self, res, worker):
		send(self.client, get_response(res))
		self.complete()

	def timeout(self):
		self.timeoutCount += 1
		if self.timeoutCount == len(self.workers):
			self.pickAndRetrieve()

class VerifyOperation(Operation):
	def __init__(self, coordinator, cmd, client, workers):
		super().__init__(coordinator, cmd, client)
		self.db = None
		self.valid = True
		self.workers = workers
		self.resCount = 0
		for worker in self.workers:
			rId = coordinator.requests.new(self.opId, worker)
			send(worker, get_db_request(rId))

	def receiveResponse(self, res, worker):
		del res["id"]
		self.resCount += 1
		if not self.db:
			self.db = res
		elif self.resCount == len(self.workers):
			send(self.client, {"type": "VERIFY-RESPONSE", "valid": self.valid})
			self.complete()
		else:
			self.valid = res == self.db
			if not self.valid:
				send(self.client, {"type": "VERIFY-RESPONSE", "valid": self.valid})
				self.complete()

	def timeout():
		pass


class SetOperation(Operation):
	SET_SUCCESS = json.dumps({"type": "SET-RESPONSE", "success": True}).encode()
	SET_FAILURE = json.dumps({"type": "SET-RESPONSE", "success": False}).encode()
	NETWORK_FAILURE = json.dumps({"type": "NETWORK_FAILURE"}).encode()
	def __init__(self, coordinator, cmd, client, workers):
		super().__init__(coordinator, cmd, client)
		self.phase = 1
		self.aborted = False
		self.yesWorkers = []
		self.workers = workers
		self.workerCount = len(coordinator.workers)
		self.responseCount = 0

		for worker in self.workers:
			rId = coordinator.requests.new(self.opId, worker)
			send(worker, set_lock_request(rId, self.cmd["key"], self.cmd["value"]))

	def receiveResponse(self, res, worker):
		# don't need to check for responses received in a successful or aborted op, since the op won't exist
		if self.phase == 1:
			if res["response"] == "YES":
				self.responseCount += 1
				self.yesWorkers.append(worker)
			elif res["response"] == "NO":
				self.aborted = True
				self.responseCount += 1
			if self.responseCount == len(self.coordinator.workers):
				self.phase = 2
				if self.aborted:
					self.abort()
				else:
					self.commit()
		elif self.phase == 2 and res["type"] == "ACK":
			# one ack is enough to determine success
			self.client.sendall(SetOperation.SET_SUCCESS)
			self.complete()


		if self.phase == 1 and res["response"] == "YES":
			self.responseCount = self.responseCount + 1
			if self.responseCount == self.workerCount:
				self.commit()
		elif self.phase == 1 and res["response"] == "NO":
			self.phase = 2
			self.abort()

	def timeout(self):
		if self.phase == 1:
			self.aborted = True
			if self.responseCount == len(self.coordinator.workers):
				self.abort()
		elif self.phase == 2 and len(self.coordinator.workers) == 0:
			# sent commits, but they all timed out
			self.client.sendall(SetOperation.SET_FAILURE)
			self.complete()

	def commit(self):
		self.phase = 2
		for worker in self.workers:
			rId = self.coordinator.requests.new(self.opId, worker)
			send(worker, commit_request(rId, self.cmd["key"]))

	def abort(self):
		self.phase = 2
		for worker in self.yesWorkers:
			rId = self.coordinator.requests.new(self.opId, worker)
			send(worker, abort_request(rId, self.cmd["key"]))
		self.client.sendall(SetOperation.SET_FAILURE)
		self.complete()

class Coordinator():
	INTERFACE = {"ADD", "GET", "SET", "ACK", "VERIFY"}
	UNRECOGNIZED = json.dumps("Unrecognized command").encode()
	def __init__(self):
		self.msgr = "COORDINATOR RECV: "
		self.msgs = "COORDINATOR SEND: "
		self.HOST = ''
		port = sys.argv[1]
		with open("port.txt", "w") as p:
			p.write(port)
		self.PORT = int(port)
		self.clients = []
		self.workers = []
		self.requests = Requests()
		self.operations = {}
        
		with open("host.txt", "w") as f:
			f.write(socket.gethostname())


	def getworkers(self):
		readable, writeable, error = select.select(
				[], self.workers, [])
		return writeable

	def accept_client(self, server):
		conn, addr = server.accept()
		conn.setblocking(False)
		self.clients.append(conn)
		#log(self.msgr + 'Connected by' + addr)

	def removeOperation(self, opId):
		del self.operations[opId]

	def removeWorker(self, worker):
		self.workers.remove(worker)

	def read_workers(self, timeout):
		for worker in self.workers:
			if worker.fileno() == -1:
				# this probably only obscures what is causing the worker to fail
				self.workers.remove(worker)
		readable, writeable, error = select.select(
						self.workers, [], [], timeout)
		for worker in readable:
			res = read_response(worker)
			if res:
				for obj in res:
					log(self.msgr + str(obj))
					req = self.requests.delete(obj["id"])
					# will probably need to check for multiple responses
					if req.opId in self.operations:
						self.operations[req.opId].receiveResponse(obj, worker)
			else:
				log("failed worker: " + str(worker))
				self.removeWorker(worker)
		return readable

	def listenloop(self):
		global socket
		server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		try:
			server.bind((self.HOST, self.PORT))
			server.listen()
			while True:
				for worker in self.workers:
					if worker.fileno() == -1:
						# this probably only obscures what is causing the worker to fail
						self.workers.remove(worker)
				readable, writeable, error = select.select(
						[server, ] + self.clients, [], [], 0)
				for socket in readable:
					if socket is server:
						self.accept_client(socket)
					else:
						cmds = read_response(socket)
						if not cmds:
							self.clients.remove(socket)
							socket.close()
						else:
							for cmd in cmds:
								log(self.msgr + str(cmd))
								if isinstance(cmd, dict) and "type" in cmd and cmd["type"] in Coordinator.INTERFACE:
									cmdType = cmd["type"]

									if cmdType == "ADD":
										self.clients.remove(socket)
										self.workers.append(socket)
									elif cmdType == "SET":
										op = SetOperation(self, cmd, socket, self.workers)
										self.operations[op.opId] = op
									elif cmdType == "GET":
										op = GetOperation(self, cmd, socket)
										self.operations[op.opId] = op
									elif cmdType == "VERIFY":
										op = VerifyOperation(self, cmd, socket, self.workers)
										self.operations[op.opId] = op
								else:
									socket.sendall(Coordinator.UNRECOGNIZED)    

				earliest = self.requests.earliest()
				if earliest:
					readable = self.read_workers(earliest.timeToTimeout())
					if not readable and earliest.opId in self.operations:
						self.operations[earliest.opId].timeout()
		except KeyboardInterrupt:
			server.close()
		except BaseException as e:
			print(e)
			server.close()
			print("server closed")
			raise



if __name__ == '__main__':
	Coordinator().listenloop()
