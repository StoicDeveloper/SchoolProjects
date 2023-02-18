import subprocess
import threading
import random
from subprocess import PIPE
import sys
import time

def getWorkers(num):
	procs = []
	for i in range(num):
		procs.append(subprocess.Popen(["python3", "worker.py"]))
	return procs

def getClients(strings):
	procs = []
	for s in strings:
		procs.append(subprocess.run(["python3", "test_cli.py", "--no-prompt", sys.argv[1]], stdout=PIPE, stderr=PIPE, input=s.encode()))
	return procs

def getClientThread(string, proc=[]):
	proc.append(subprocess.run(["python3", "test_cli.py", "--no-prompt", sys.argv[1]], stdout=PIPE, stderr=PIPE, input=string.encode()))

def handleThreads(num, strings):
	threadOutput = []
	threads = []
	for i in range(num):
		threadOutput.append([])
		threads.append(threading.Thread(target=getClients, args=(random.choice(strings), threadOutput[i])))
	for i in range(num):
		threads[i].start()
	for i in range(num):
		threads[i].join()
	clientOutput = None



def clientOutput(clients):
	for client in clients:
		print(client.stdout.decode())
	

def getCoordinator():
	return subprocess.Popen(["python3", "coordinator.py", sys.argv[1]])

def killall(procs):
	time.sleep(5)
	for proc in procs:
		if getattr(proc, "kill", None):
			proc.kill()

def test1():
	procs = []
	print("Testing the values can be input and then retreived")
	procs.append(getCoordinator())
	time.sleep(0.2)
	procs.extend(getWorkers(4))
	time.sleep(0.2)

	test1 = """
	set bla blo
	sleep 0.2
	get bla
	verify
	exit
	"""
	test2 = """
	set blim blam
	sleep 0.2
	get blim
	verify
	exit
	"""
	clients = getClients([test1, test2])
	procs.extend(clients)
	clientOutput(clients)
	killall(procs)
	
	
def test2():
	procs = []
	print("Testing collision between attempts to set a single value")
	procs.append(getCoordinator())
	time.sleep(0.2)
	procs.extend(getWorkers(3))
	time.sleep(0.2)

	plants = ["fern", "grass", "tree", "flower", "pine", "potato", "bush", "algae", "evergreen"]
	test1 = ""
	test2 = ""
	test3 = ""
	for i in range(3):
		test1 += "set plant " + random.choice(plants) + "\n"
		test2 += "set plant " + random.choice(plants) + "\n"
		test3 += "set plant " + random.choice(plants) + "\n"
	test1 += "verify\nexit\n"
	test2 += "verify\nexit\n"
	test3 += "verify\nexit\n"

	threads = [[],[],[]]
	thread1 = threading.Thread(target=getClientThread, args=(test1, threads[0]))
	thread2 = threading.Thread(target=getClientThread, args=(test2, threads[1]))
	thread3 = threading.Thread(target=getClientThread, args=(test3, threads[2]))
	thread1.start()
	thread2.start()
	thread3.start()
	thread1.join()
	thread2.join()
	thread3.join()
	clientOutput([threads[0][0], threads[1][0], threads[2][0]])
	killall(procs)


	

if __name__ == '__main__':
	test1()
	test2()
