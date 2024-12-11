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
process 1 is receiver, process 2 and 3 sender
"""
NUMBER_MESSAGES = 2


def main():

    print("Start build")

    # Build project
    subprocess.call(shlex.split('../template_java/build.sh'))


    print("start process")

    # Call teacher test
    #subprocess.call(shlex.split('../tools/stress.py fifo -r RUNSCRIPT -l LOGSDIR -p 2 -m ' + str(NUMBER_MESSAGES)))
    subprocess.call(shlex.split('../tools/stress.py agreement -r ../template_java/run.sh -l ../prof_test/ -p 3 -n 2 -v 22 -d 70'))
    #./stress.py agreement -r RUNSCRIPT -l LOGSDIR -p PROCESSES -n PROPOSALS -v PROPOSAL_MAX_VALUES -d PROPOSALS_DISTINCT_VALUES

    """RUNSCRIPT is the path to run.sh. Remember to build your project first!
    • LOGSDIR is the path to a directory where stdout/stderr and output of each process will be stored. It
    also stores generated HOSTS and CONFIG files. The directory must exist as it will not be created for you.
        • PROCESSES (for perfect, fifo and agreement) specifies the number of processes spawn during validation.
    • MESSAGES (for perfect and fifo) specifies the number of messages each process is broadcasting.
    • PROPOSALS (for agreement) specifies the number of proposals each process is proposing"""


    v = []
    v.append(verify('01'))
    v.append(verify('02'))
    v.append(verify('03'))

    assert len(v[0]) == len(v[1]) == len(v[2]) == 2
    proposal_number = len(v[0])

    for i in range(proposal_number):
        if not (v[0][i].issubset(v[1][i]) or (v[1][i].issubset(v[0][i]))):
            print(f"ERROR, set {i} not super or subset for process 0, 1")

    for i in range(proposal_number):
        if not (v[0][i].issubset(v[2][i]) or (v[2][i].issubset(v[0][i]))):
            print(f"ERROR, set {i} not super or subset for process 0, 2")

    for i in range(proposal_number):
        if not (v[1][i].issubset(v[2][i]) or (v[2][i].issubset(v[1][i]))):
            print(f"ERROR, set {i} not super or subset for process 1, 2")


    print("Finish")




def verify(number):

    propositions = []
    decisions = []


    # Open the file in read mode
    line_number = -1
    with open('../prof_test/proc' + number + '.config', 'r') as file:
        # Read each line in the file
        for line in file:
            line_number += 1
            #Don't check first line because config.
            if line_number == 0:
                continue

            set_proposition = set()
            line_words = line.split()
            for prop in line_words:
                set_proposition.add(prop)
            propositions.append(set_proposition)

    print(number, "proposition", propositions)

    # Open the file in read mode
    line_number = -1
    with open('../prof_test/proc' + number + '.output', 'r') as file:
        # Read each line in the file
        for line in file:
            line_number += 1
            line_words = line.split()

            set_decision = set()
            for decis in line_words:
                is_new = set_decision.add(decis)
                if not is_new:
                    print("ERROR not a set of decided value because 2 times same value", decis, number)


            if not set_proposition.issubset(set_decision):
                print("ERROR decision is not subset of proposition", set_proposition not in set_decision, number)


    return decisions

main()
