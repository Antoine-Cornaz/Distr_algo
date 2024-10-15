#!/usr/bin/env python3

from multiprocessing import Process
import subprocess
import shlex
import time
import threading
import signal

from enum import Enum

"""
This is a basic test file
3 processes started
with 1000 messages
process 2 is receiver
"""
NUMBER_MESSAGES = 1000  #Change both in CONFIG
WAITING_TIME = 10 # time to wait in ms


# Process State Enum for managing process states
class ProcessState(Enum):
    RUNNING = 1
    STOPPED = 2
    TERMINATED = 3

# ProcessInfo class to hold information about a subprocess and manage state
class ProcessInfo:
    def __init__(self, handle):
        self.lock = threading.Lock()  # Thread safety when managing the process
        self.handle = handle          # Subprocess handle
        self.state = ProcessState.RUNNING  # Initial state is RUNNING

    @staticmethod
    def stateToSignal(state):
        # Convert ProcessState to the appropriate signal
        if state == ProcessState.RUNNING:
            return signal.SIGCONT
        if state == ProcessState.STOPPED:
            return signal.SIGSTOP
        if state == ProcessState.TERMINATED:
            return signal.SIGTERM

    @staticmethod
    def stateToSignalStr(state):
        # Get the signal name as a string (for logging purposes)
        if state == ProcessState.RUNNING:
            return "SIGCONT"
        if state == ProcessState.STOPPED:
            return "SIGSTOP"
        if state == ProcessState.TERMINATED:
            return "SIGTERM"

    @staticmethod
    def validStateTransition(current, desired):
        # Validate if the transition between states is allowed
        if current == ProcessState.TERMINATED:
            return False
        if current == ProcessState.RUNNING:
            return desired == ProcessState.STOPPED or desired == ProcessState.TERMINATED
        if current == ProcessState.STOPPED:
            return desired == ProcessState.RUNNING
        return False



def start_subprocess(number):
    cmd = '../template_java/run.sh --id ' + number + ' --hosts ../example/hosts --output ../example/output/' + number + '.output ../example/configs/perfect-links2.config'
    return subprocess.Popen(cmd, shell=True)




def main():
    print("Start build")

    # Build project
    subprocess.call(shlex.split('../template_java/build.sh'))

    print("start process")

    # Start three Java subprocesses
    p1 = ProcessInfo(start_subprocess('1'))
    p2 = ProcessInfo(start_subprocess('2'))
    p3 = ProcessInfo(start_subprocess('3'))

    processes = [p1, p2, p3]

    # Wait a maximum of 1 second for processes to initialize
    time.sleep(WAITING_TIME)
    print("Processes initialized")

    # Attempt to gracefully terminate all processes
    for proc in processes:
        with proc.lock:
            print("terminate gracefully", proc)
            proc.handle.send_signal(signal.SIGTERM)  # Send SIGTERM for graceful termination
            proc.state = ProcessState.TERMINATED  # Update state to TERMINATED
            print(f"Sent SIGTERM to process {proc.handle.pid}")

    # Wait for another second to allow processes to terminate gracefully
    time.sleep(WAITING_TIME)

    # Ensure the processes are killed if they haven't terminated
    print("Killing remaining processes")
    for proc in processes:
        with proc.lock:
            proc.handle.kill()  # Forcefully kill the process
            print(f"Killed process {proc.handle.pid}")

    subprocess.call(shlex.split('sudo fuser -k 11002/udp'))

    r1, r3 = verify_receiver('2')
    s_1 = verify_sender('1')
    s_3 = verify_sender('3')

    if not s_1.issubset(r1):
        print("Some messages has been delivered as sent but not received from process 1")
        print(s_1.difference(r1))

    if not s_3.issubset(r3):
        print("Some messages has been delivered as sent but not received from process 3")
        print(s_3.difference(r3))

    print("s1 ", len(s_1), " r1 " , len(r1))
    print("s3 ", len(s_3), " r3 " , len(r3))

    send_ratio = (len(s_1) + len(s_3))/(2*NUMBER_MESSAGES)
    received_ratio = (len(r1) + len(r3))/(2*NUMBER_MESSAGES)

    print("sent " + str(send_ratio))
    print("recived " + str(received_ratio))

    print("Finish")




def verify_receiver(number):
    value_p1 = set()
    #p2 receive
    value_p3 = set()
    # Open the file in read mode
    #try:
    if True:
        with open('../example/output/' + number + '.output', 'r') as file:
            # Read each line in the file
            for line in file:
                line_words = line.split()
                if len(line_words) != 3:
                    print("Error receiver, line does not have 3 arguments ", str(len(line_words)))
                if not line_words[0] == "d":
                    print("Error first char is not d $" + str(line_words[0]) + "$", )
                    print("$"+line+"$")
                    return value_p1, value_p3

                message_number = int(line_words[1])
                if message_number == 1:

                    if message_number in value_p1:
                        print("Error value received 2 times p1:" + str(message_number))
                        return value_p1, value_p3
                    else:
                        value_p1.add(line_words[2])
                else:
                    value_p3.add(line_words[2])

    return value_p1, value_p3


def verify_sender(number):
    value_p = set()

    # Open the file in read mode
    try:
        with open('../example/output/' + number + '.output', 'r') as file:
            # Read each line in the file
            for line in file:
                line_words = line.split()
                if len(line_words) != 2:
                    print("Error sender, line does not have 2 arguments ", str(len(line_words)))
                    return value_p
                if not line_words[0] == "b":
                    print("Error first char is not b $"  + str(line_words[0]) + "$")
                    print("$"+line+"$")
                    return value_p
                if line_words[1] in value_p:
                    print("Error sender send id " + number + " value send twice " + str(line_words[1]))
                value_p.add(line_words[1])

    except:
        print("ERROR in reading file " + number + " as a sender")
        return value_p

    """if len(value_p) != NUMBER_MESSAGES:
        print("ERROR not all messages sent from p:" + number + " " + str(len(value_p)) + " should be " + str(NUMBER_MESSAGES))
        return False"""

    return value_p


main()
