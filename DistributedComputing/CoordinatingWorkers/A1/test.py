import subprocess
import sys
import time

test1 = """
set bla blo
sleep 0.2
get bla
"""
coordinator = subprocess.Popen(["python3", "coordinator.py", sys.argv[1]])
time.sleep(0.2)
worker = subprocess.Popen(["python3", "worker.py"])

time.sleep(0.2)
client = subprocess.Popen(["python3", "test_cli.py", "--no-prompt", sys.argv[1]], stdin=subprocess.PIPE)
time.sleep(0.2)
client.communicate(test1.encode())
