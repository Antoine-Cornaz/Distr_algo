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
NUMBER_MESSAGES = 100_000
NUMBER_PROCESS = 9


def main():

    print("Start build")

    # Build project
    subprocess.call(shlex.split('../template_java/build.sh'))


    print("start process")

    # Call teacher test
    subprocess.call(shlex.split('../tools/stress.py agreement -r ../template_java/run.sh -l ../prof_test/ -p '+str(NUMBER_PROCESS)+' -n' + str(NUMBER_MESSAGES) + ' -v 3 -d 7'))
    #./stress.py agreement -r RUNSCRIPT -l LOGSDIR -p PROCESSES -n PROPOSALS -v PROPOSAL_MAX_VALUES -d PROPOSALS_DISTINCT_VALUES

    """RUNSCRIPT is the path to run.sh. Remember to build your project first!
    • LOGSDIR is the path to a directory where stdout/stderr and output of each process will be stored. It
    also stores generated HOSTS and CONFIG files. The directory must exist as it will not be created for you.
        • PROCESSES (for perfect, fifo and agreement) specifies the number of processes spawn during validation.
    • MESSAGES (for perfect and fifo) specifies the number of messages each process is broadcasting.
    • PROPOSALS (for agreement) specifies the number of proposals each process is proposing"""


    v = []
    for i in range(1, NUMBER_PROCESS+1):
        v.append(verify('0'+str(i)))

    for i in range(1, NUMBER_PROCESS+1):
        with open('../prof_test/proc0'+str(i)+'.stderr', 'r') as file:
            text = file.read()
            if text != "":
                print(text)

    print(str(NUMBER_MESSAGES) + " Size:")
    total = 0
    for i in range(NUMBER_PROCESS):
        print(len(v[i]), end=', ')
        total += len(v[i])
    print()
    print("total", total)

    for i in range(NUMBER_PROCESS):
        if not (len(v[i]) == NUMBER_MESSAGES):
            print("The file outputs doesn't have the same length as input ")
            break


    for i in range(NUMBER_PROCESS):
        for j in range(i+1, NUMBER_PROCESS):
            for k in range(min(len(v[i]), len(v[j]))):
                if not (v[i][k].issubset(v[j][k]) or (v[j][k].issubset(v[i][k]))):
                    print(f"ERROR, set {k} not super or subset for process {i}, {j}")


    print("Finish")




def verify(number):

    list_set_propositions = []
    list_set_decision = []

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
            for proposition in line_words:
                set_proposition.add(proposition)
            list_set_propositions.append(set_proposition)

    #print(number, "proposition", propositions)

    # Open the file in read mode
    line_number = -1
    with open('../prof_test/proc' + number + '.output', 'r') as file:
        # Read each line in the file
        for line in file:
            line_number += 1
            line_words = line.split()

            set_decision = set()
            #print("line", line_words)
            for decision in line_words:
                if decision in set_decision:
                    print("ERROR not a set of decided value because 2 times same value", decision, number)
                else:
                    set_decision.add(decision)
            list_set_decision.append(set_decision)


    for i in range(len(list_set_decision)):
        if not list_set_propositions[i].issubset(list_set_decision[i]):
            print()
            print(f"ERROR file:{number} line {i}")
            print("proposition " + str(list_set_propositions[i]))
            print("decided " + str(list_set_decision[i]))
            print("ERROR decision is not subset of proposition", str(list_set_propositions[i].difference(list_set_decision[i])))

    return list_set_decision

main()
