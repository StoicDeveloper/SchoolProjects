import subprocess
import sys
import time

test1 = """
set bla blo
sleep 0.2
get bla
"""
test2a  = "set blib blop"
test2b  = "set blib blap"
coordinator = subprocess.Popen(["python3", "coordinator.py", sys.argv[1]])
time.sleep(0.2)
for i in range(10):
	subprocess.Popen(["python3", "worker.py"])

time.sleep(0.2)
clients = []
for i in range(10):
	clients.append(subprocess.Popen(["python3", "test_cli.py", "--no-prompt", sys.argv[1]], stdin=subprocess.PIPE))
time.sleep(0.2)
client[0].communicate(test1.encode())
client[1].communicate(test2a.encode())
client[2].communicate(test2b.encode())
