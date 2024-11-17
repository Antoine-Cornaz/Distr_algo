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
NUMBER_MESSAGES = 1000


def main():

    print("Start build")

    # Build project
    subprocess.call(shlex.split('../template_java/build.sh'))


    print("start process")

    # Call teacher test
    #subprocess.call(shlex.split('../tools/stress.py fifo -r RUNSCRIPT -l LOGSDIR -p 2 -m ' + str(NUMBER_MESSAGES)))
    subprocess.call(shlex.split('../tools/stress.py fifo -r ../template_java/run.sh -l ../prof_test/ -p 3 -m ' + str(NUMBER_MESSAGES)))

    v = []
    v.append(verify('01'))
    v.append(verify('02'))
    v.append(verify('03'))

    # Verify message delivred is message broadcasted
    for i in range(3):
        for j in range(3):
            if not v[i][j+1].issubset(v[j][0]):
                diff = v[i][j+1].difference(v[j][0])
                if len(diff) < 10:
                    print(diff)

    # Verify if one get it all get it
    for i in range(3):
        if v[0][i] != v[i][1] or v[0][i] != v[i][2] or v[0][i] != v[i][3]:
            print(i, ", error not everyone get message", len(v[0][i]), len(v[i][1]), len(v[i][2]), len(v[i][3]))

    # Verify no 0 or > number message
    for i in range(3):
        for j in range(4):
            for m in v[i][j]:
                m = int(m)

                if not (0 < m and m <= NUMBER_MESSAGES):
                    #i=receiver, j=sender 0 if broadcast, 1,2,3 if receive
                    print("subset contain message not in list", i, j, m, "i=receiver, j=sender 0 if broadcast, 1,2,3 if receive")

    for i in range(3):
        print(i, "broadcast", len(v[i][0]) / NUMBER_MESSAGES * 100, "%")

    for i in range(3):
        value = 0
        for j in range(3):
            value += len(v[j][i+1])
        print(i, "deliver", value / NUMBER_MESSAGES/3 * 100, "%")

    with open('../prof_test/proc01.stderr', 'r') as file:
        m = file.read()
        if m != "":
            print(m)

    with open('../prof_test/proc02.stderr', 'r') as file:
        m = file.read()
        if m != "":
            print(m)

    with open('../prof_test/proc02.stderr', 'r') as file:
        m = file.read()
        if m != "":
            print(m)

    print("Finish")




def verify(number):

    broadcast = set()
    value_p1 = set()
    value_p2 = set()
    value_p3 = set()

    # Open the file in read mode
    with open('../prof_test/proc' + number + '.output', 'r') as file:
        # Read each line in the file
        for line in file:
            line_words = line.split()
            if len(line_words) == 2:
                if not line_words[0] == "b":
                    print("Error 2 args but first char is not b $" + str(line_words[0]) + "$", )
                    print("$"+line+"$")

                if line_words[1] != '1' and str(int(line_words[1])-1) not in broadcast:
                    print("Error previous one not there broadcast", line_words[1])
                broadcast.add(line_words[1])
            elif len(line_words) == 3:
                if not line_words[0] == "d":
                    print("Error 3 args but first char is not d $" + str(line_words[0]) + "$", )
                    print("$"+line+"$")

                message_number = int(line_words[1])
                if message_number == 1:

                    if message_number in value_p1:
                        print("Error value received 2 times p1:" + str(message_number))

                    else:
                        if line_words[2] != '1' and str(int(line_words[2])-1) not in value_p1:
                            print("Error previous one not there p1", line)
                        value_p1.add(line_words[2])

                elif message_number == 2:
                    if message_number in value_p2:
                        print("Error value received 2 times p2:" + str(message_number))
                    else:
                        if line_words[2] != '1' and str(int(line_words[2])-1) not in value_p2:
                            print("Error previous one not there p2", line)
                        value_p2.add(line_words[2])
                elif message_number == 3:
                    if message_number in value_p3:
                        print("Error value received 2 times p2:" + str(message_number))
                    else:
                        if line_words[2] != '1' and str(int(line_words[2])-1) not in value_p3:
                            print("Error previous one not there p3", line)
                        value_p3.add(line_words[2])
                else:
                    print("error value sender number " + message_number)

            else:
                print("ERROR, wrong number of argument, should be 2 or 3 but not " + len(line_words))
                print("$"+line+"$")

    return broadcast, value_p1, value_p2, value_p3

main()
