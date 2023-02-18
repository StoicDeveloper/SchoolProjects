import subprocess
from subprocess import PIPE
import sys
import time

test1 = """
set bla blo
sleep 0.2
get bla
"""
test2a  = """
set blib blop
get blib
exit
"""
test2b  = """
set blib blap
get blib
exit
"""
coordinator = subprocess.Popen(["python3", "coordinator.py", sys.argv[1]])
time.sleep(0.2)
for i in range(4):
	subprocess.Popen(["python3", "worker.py"])

time.sleep(0.2)
clients = []
res1 = subprocess.run(["python3", "test_cli.py", "--no-prompt", sys.argv[1]], stdout=PIPE, stderr=PIPE, input=test2a.encode()).stdout.decode()
res2 = subprocess.run(["python3", "test_cli.py", "--no-prompt", sys.argv[1]], stdout=PIPE, stderr=PIPE, input=test2b.encode()).stdout.decode()
print(res1)
print(res2)
time.sleep(0.2)
#clients[0].communicate(test1.encode())
#clients[1].communicate(test2a.encode())
#clients[2].communicate(test2b.encode())

#for client in clients:
	#client.communicate("exit\n".encode())
