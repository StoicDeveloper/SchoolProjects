import socket
import traceback
import time
import uuid
import json
import threading
import hashlib
import random

wellKnownHost = 'silicon.cs.umanitoba.ca'
wellKnownPort = 8999

def send(sock, msg, peer):
    if isinstance(peer, Peer):
        peer = peer.addr
    msg = json.dumps(msg)
    sock.sendto(msg.encode(), peer)

def getLocalIP():
    return socket.gethostbyname(socket.gethostname())



class PeerSet:
    def __init__(self, peers=None):
        if peers:
            self.peers = peers.peers
        else:
            self.peers = set()
        self.lock = threading.Lock()
    def add(self, peer, name=''):
        self.lock.acquire(blocking=True)
        if isinstance(peer, Peer):
            self.peers.add(peer)
        else:
            self.peers.add(Peer(addr, name))
        self.lock.release()
    def remove(self, peer):
        self.lock.acquire(blocking=True)
        self.peers.remove(peer)
        self.lock.release()
    def __contains__(self, peer):
        if isinstance(peer, Peer):
            return peer in self.peers
        else:
            return Peer(addr, '') in self.peers

    def __str__(self):
        return str(self.peers)
    def __len__(self):
        return len(self.peers)

    def getTimeoutDict(self):
        return {peer: [0, 0] for peer in self.peers}

class Peer:
    def __init__(self, addr, name):
        self.name = name
        self.host = addr[0]
        self.port = addr[1]
        self.addr = addr

    def __str__(self):
        return f'{self.host}:{self.port}'
    def __repr__(self):
        return str(self)
    def __hash__(self):
        return hash(str(self))
    def __eq__(self, peer):
        return str(self) == str(peer)
    

class BlockchainPeer:
    def __init__(self, name, port):
        self.peers = PeerSet()
        self.currConsensus = None
        self.blockchain = None

        self.awaitingBlocks = None
        self.awaitingBlocksLock = threading.Lock()

        self.name = name
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind(("", port))
        self.port = port
        self.host = getLocalIP()
        self.floods = set()
        threading.Thread(target=self.periodicFlood).start()
        threading.Thread(target=self.consensus).start()

        self.methods = {
            "FLOOD": self.floodReply,
            "FLOOD-REPLY": self.addPeer,
            "GET_BLOCK": self.sendBlock,
            "GET_BLOCK_REPLY": self.receiveBlock,
            "ANNOUNCE": self.addBlock,
            "STATS": self.statsReply,
            "STATS_REPLY": self.receiveStats,
            "CONSENSUS": self.consensus
        }

        if sys.argv[1] == '--accept-miners':
            threading.Thread(target=self.coordinateMiners).start()

	def accept_miner(self, server):
		conn, addr = server.accept()
		conn.setblocking(False)
		self.miners.append(conn)

    def read_response(socket):
        data = socket.recv(1024)
        res = None
        if data:
            cmds = [cmd + "}" for cmd in data.decode().split("}")][0:-1]
            res = [json.loads(cmd) for cmd in cmds]
        return res

    def mineCommand(self, prevHash, name, messages, height):
        return {
            "type": "MINE",
            "hash": prevHash,
            "minedBy": name,
            "messages": messages,
            "height": height
        }

    def coordinateMiners(self):
        words = open("/usr/share/dict/words").read().splitlines()
		with open("host.txt", "w") as f:
			f.write(self.host)
		with open("port.txt", "w") as p:
			p.write(self.port)

        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        while not self.blockchain:
            time.sleep(0.1)
        prevHash = self.blockchain.getTopHash()
		try:
			server.bind((self.HOST, self.PORT))
			server.listen()
			while True:
                if self.blockchain.getTopHash() != prevHash:
                    prevHash = self.blockchain.getTopHash()
                    for miner in self.miners:
                        miner.sendall(json.dumps(mineCommand(prevHash, self.name, random.sample(words, 5), len(self.blockchain))).encode())
				readable, writeable, error = select.select(
						[server, ] + self.clients, [], [], 0)
				for socket in readable:
					if socket is server:
						self.accept_miner(socket)
					else:
						cmds = read_response(socket)
                        if cmd["type"] == "ANNOUNCE":
                            self.addBlock(msg=cmd)
                            for peer in self.peers:
                                send(self.sock, cmd, peer)

    def isSelf(self, peer):
        return peer.host == self.host and peer.port == self.port

    def periodicFlood(self):
        while True:
            self.sendFlood()
            time.sleep(30)

    # def removePeer(self, addr):
        # self.peerLock.acquire(blocking=True)
        # self.peerSet.remove(self.peerString(addr))
        # self.peerLock.release()

    def addPeer(self, msg={}, addr=None):
        peer = Peer((socket.gethostbyname(msg['host']), msg['port']), '')
        if not peer in self.peers and not self.isSelf(peer):
            print(f"adding peer {str(peer)}")
            self.peers.add(peer, '')

    def run(self):
        try:
            while True:
                data, addr = self.sock.recvfrom(1024)
                # print("receiving message")
                # print(data)
                msg = json.loads(data.decode())
                if not "type" in msg:
                    continue
                
                if msg["type"] in self.methods:
                    while threading.active_count() > 100:
                        time.sleep(0.1)
                    threading.Thread(target=self.methods[msg["type"]], name=msg["type"], kwargs={'msg':msg, 'addr':addr}).start()
                    # self.methods[msg["type"]](msg, addr)
        except KeyboardInterrupt:
            self.sock.close()
        except Exception as e:
            self.sock.close()
            traceback.print_exc()
            print("exception")
            print(e)

    def getFloodBaseMsg(self):
        return {"host": self.host, "port": self.port, "name": self.name}

    def sendFlood(self):
        msg = self.getFloodBaseMsg()
        msg["type"] = "FLOOD"
        msg["id"] = uuid.uuid4().hex
        send(self.sock, msg, (wellKnownHost, wellKnownPort))

    def floodReply(self, msg={}, addr=()):
        reply = self.getFloodBaseMsg()
        reply["type"] = "FLOOD-REPLY"
        host = msg["host"]
        port = msg["port"]
        self.addPeer(msg=msg, addr=(host, port))
        send(self.sock, reply, (host, port))

    def sendBlock(self, msg={}, addr=()):
        #reimplement with blockchai class
        if msg["height"] >= len(self.blockchain) or msg["height"] < 0:
            reply = Block.null
        else:
            reply = self.blockchain[msg["height"]].get()
        send(self.sock, reply, addr)

    def addBlock(self, msg={}, addr=None):
        if self.blockchain:
            block = Block(msg["messages"], msg["minedBy"], msg["nonce"], msg["hash"], msg["height"])
            self.blockchain.add(block)

    def statsReply(self, msg=None, addr=()):
        # what if there are no blocks?
        # reimplement with blockchain class
        if self.blockchain and len(self.blockchain) > 0:
            reply = {"type": "STATS_REPLY", "height": len(self.blockchain), "hash": self.blockchain.getTopHash()}
            print(reply)
            send(self.sock, reply, addr)

    def receiveStats(self, msg={}, addr=()):
        print("received stats")
        if self.currConsensus:
            self.currConsensus.add(Peer(addr, ''), msg)
        else:
            print("no current consensus")

    def consensus(self, msg={}, addr=()):
        # todo: account for stat request timeouts, resend stat request (or ignore)
        while len(self.peers) < 4:
            time.sleep(0.1)
        self.currConsensus = Consensus(self.peers, self.sock)
        self.currConsensus.run()
        # this constructor will handle getting all  of the needed stats replies, including timeouts
        consensus = self.currConsensus.getConsensus()

        if not self.blockchain or consensus[0] != self.blockchain.getTopHash():
            self.getAllBlocks(*consensus)
        self.currConsensus = None

            
    def getAllBlocks(self, theHash, height, peers):
        # todo: what if a message is lost or its target times out?
        self.awaitingBlocks = set(range(height))
        self.blockchain = Blockchain(height)
        num = len(peers)
        for i in range(height):
            #print(f"requesting block {i} from peer {peers[i % num]}")
            send(self.sock, {"type": "GET_BLOCK", "height": i}, peers[i % num])
        i = 0
        while len(self.awaitingBlocks):
            block = random.sample(self.awaitingBlocks, 1)[0]
            peer = random.choice(peers)
            send(self.sock, {"type": "GET_BLOCK", "height": block}, peer )
            #print(f"requesting block {block} from peer {peer} and max height {height}, {len(self.awaitingBlocks)} left")
            i += 1
            time.sleep(0.1)
        print("all blocks received")

        if self.blockchain.verify():
            print("valid blockchain")
        else:
            print("invalid blockchain")



    def receiveBlock(self, msg={}, addr=None):
        self.awaitingBlocksLock.acquire(blocking=True)
        try:
            ##print(msg)
            if msg["height"] == None:
                print("null block received")
                self.awaitingBlocksLock.release()
                return None
            if self.awaitingBlocks and msg["height"] in self.awaitingBlocks:
                self.awaitingBlocks.remove(msg["height"])
                block = Block(msg["messages"], msg["minedBy"], msg["nonce"], msg["hash"], msg["height"])
                self.blockchain.update(block)
                #print(f"block {msg['height']} received, {len(self.awaitingBlocks)} left")
        except Exception as e:
            traceback.print_exc()
            print(e)
        self.awaitingBlocksLock.release()
        
        




class Consensus:
    maxTries = 5
    class ConsensusItem:
        def __init__(self, theHash, peer, height):
            self.count = 1
            self.height = height
            self.hash = theHash
            self.peers = [peer]
            
    def __init__(self, peers, sock):
        self.peers = peers.getTimeoutDict()
        print("peers")
        print(self.peers)
        self.sock = sock
        self.replies = {}
        self.lock = threading.Lock()

    def run(self):
        self.lock.acquire(blocking=True)
        for peer in self.peers:
            print("sending stats")
            send(self.sock, {"type": "STATS"}, peer.addr)
            self.peers[peer][0] = time.time()
            self.peers[peer][1] = 1
        self.lock.release()

        while self.peers:
            time.sleep(0.01)
            self.lock.acquire(blocking=True)
            for peer in list(self.peers.keys()):
                if self.peers[peer][1] > Consensus.maxTries:
                    del self.peers[peer]
                    print(f"{peer} lost contact")
                elif time.time() - self.peers[peer][0] > 0.1:
                    send(self.sock, {"type": "STATS"}, peer.addr)
                    self.peers[peer][0] = time.time()
                    self.peers[peer][1] += 1
                    print(f"{peer} timeout, resending")
            self.lock.release()
        
    def numReplies(self):
        count = 0
        for theHash in self.replies:
            count += self.replies[theHash].count
        return count

    def add(self, peer, reply):
        self.lock.acquire(blocking=True)
        theHash = reply["hash"]
        if peer in self.peers:
            del self.peers[peer]
        if theHash in self.replies:
            self.replies[theHash].count += 1
            self.replies[theHash].peers.append(peer)
        else:
            self.replies[theHash] = Consensus.ConsensusItem(theHash, peer, reply["height"])
        #print(f"received {self.numReplies()}, {len(self.peers)} remaining")
        #print(peer)
        #print(self.peers)
        self.lock.release()

    def getConsensus(self):
        self.lock.acquire(blocking=True)
        maxCount = 0
        maxHash = ""
        for theHash in self.replies:
            if self.replies[theHash].count > maxCount:
                maxHash = theHash
        height = self.replies[maxHash].height
        peers = self.replies[maxHash].peers
        return (maxHash, height, peers)



class Block:
    null = {
        "type": "GET_BLOCK_REPLY",
        "height": None,
        "minedBy": None,
        "nonce": None,
        "messages": None,
        "hash": None
    }


    def __init__(self, messages, miner, nonce, theHash, height):
        self.messages = messages
        self.miner = miner
        self.nonce = nonce
        self.hash = theHash
        self.height = height

    def get(self):
        return {
            "type": "GET_BLOCK_REPLY",
            "height": self.height,
            "minedBy": self.miner,
            "nonce": self.nonce,
            "messages": self.messages,
            "hash": self.hash 
        }

class Blockchain:
    def __init__(self, height):
        self.chain = [None] * height
        self.awaiting = set(range(height))
        self.lock = threading.Lock()
        self.valid = False

    def __getitem__(self, key):
        return self.chain[key]

    def __len__(self):
        return len(self.chain)

    def update(self, block):
        self.lock.acquire(blocking=True)
        self.chain[block.height] = block
        self.awaiting.remove(block.height)
        self.lock.release()

    def add(self, block):
        if block.height == len(self) and self.verifyBlock(block):
            self.chain.append(block)

    def __bool__(self):
        return self.valid

    def verifyBlock(self, block):
        hashBase = hashlib.sha256()
        if block.height != 0:
            hashBase.update(self.chain[block.height - 1].hash.encode())
        hashBase.update(block.miner.encode())
        for message in block.messages:
            hashBase.update(message.encode())
        hashBase.update(block.nonce.encode())
        if hashBase.hexdigest() == block.hash:
            valid = True
        else:
            print(f"invalid block of height {block.height}")
            valid = False
        return valid

       
    def getTopHash(self):
        return self.chain[len(self.chain) - 1].hash

    def verify(self):
        valid = True
        currHeight = 0
        for block in self.chain:
            valid = self.verifyBlock(block)
            if not valid:
                return valid
        self.valid = valid
        return valid




if __name__ == '__main__':
    BlockchainPeer("Xian's peer", 50000).run()
        




