import json
import string
import hashlib

class Miner:
    def __init__(self):
		with open("port.txt", "r") as p:
			self.PORT = int(p.read())
		with open("host.txt", "r") as h:
			self.HOST = h.read()
        self.height = None
        self.hash = None
        self.name = None
        self.messages = []
        self.coordinator = None
        self.difficulty = 8


    def minerloop(self):
		global socket # not sure why this line is needed, script breaks without it
		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.setblocking(False)
			self.connect_coordinator(s)
			while True:
				time.sleep(0.1)
				data = s.recv(1024).decode()
				if data:
                    cmd = json.loads(data)
                    if cmd["type"] == "MINE":
                        self.height = cmd["height"]
                        self.name = cmd["minedBy"]
                        self.hash = cmd["hash"]
                        self.messages = cmd["messages"]
                else:
                    self.mine()

	def connect_coordinator(self, socket):
		socket.connect((self.HOST, self.PORT))
		addMsg = json.dumps({"type": "ADD"}).encode()
		#print(self.msgs + str(addMsg))
		socket.sendall(addMsg)
        self.coordinator = socket

    def mine(self):
        letters = string.printable
        nonce = "".join(random.choice(letters) for i in range(40))
        newHash = self.gethash(rnd)
        if newHash[-1 * self.difficulty:] == "0" * self.difficulty:
            msg = {
                "type": "ANNOUNCE"
                "height": self.height,
                "minedBy": self.name,
                "nonce": nonce,
                "messages": messages,
                "hash": newHash
            }
            self.coordinator.sendall(json.dumps(msg).encode())

    def gethash(self, rnd):
        hashBase = hashlib.sha256()
        hashBase.update(self.hash.encode())
        hashBase.update(self.name.encode())
        for message in self.messages:
            hashBase.update(message.encode())
        hashBase.update(nonce.encode())
        return hashBase.hexDigest()
    


if __name__ == '__main__':
Miner().minerloop()

