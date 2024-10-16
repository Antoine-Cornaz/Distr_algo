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
NUMBER_MESSAGES = 100000  #Change both here and in call


def main():
    print("Start build")

    # Build project
    subprocess.call(shlex.split('../template_java/build.sh'))

    # delete folder prof_test
    subprocess.call(shlex.split('rm ../prof_test/*'))

    print("start process")

    # Call teacher test
    subprocess.call(shlex.split('../tools/stress.py perfect -r ../template_java/run.sh -l ../prof_test/ -p 3 -m ' + str(NUMBER_MESSAGES)))

    r2, r3 = verify_receiver('01')
    s2 = verify_sender('02')
    s3 = verify_sender('03')

    if not r2.issubset(s2):
        print("Some messages has been delivered for the receiver but never sent p2")
        diff = r2.difference(s2)
        if len(diff) < 10:
            print(diff)

    if not r3.issubset(s3):
        print("Some messages has been delivered for the receiver but never sent p3")
        diff = r3.difference(s3)
        if len(diff) < 10:
            print(diff)

    print("s2 ", len(s2), " r2 " , len(r2))
    print("s3 ", len(s3), " r3 " , len(r3))

    send_ratio = 100*(len(s2) + len(s3))/(2*NUMBER_MESSAGES)
    received_ratio = 100*(len(r2) + len(r3))/(2*NUMBER_MESSAGES)

    print("sent " + str(send_ratio) + "%")
    print("recived " + str(received_ratio) + "%")

    print("Finish")




def verify_receiver(number):
    value_p2 = set()
    #p2 receive
    value_p3 = set()
    # Open the file in read mode
    #try:
    if True:
        with open('../prof_test/proc' + number + '.output', 'r') as file:
            # Read each line in the file
            for line in file:
                line_words = line.split()
                if len(line_words) != 3:
                    print("Error receiver, line does not have 3 arguments ", str(len(line_words)))
                if not line_words[0] == "d":
                    print("Error first char is not d $" + str(line_words[0]) + "$", )
                    print("$"+line+"$")
                    return value_p2, value_p3

                message_number = int(line_words[1])
                if message_number == 2:

                    if message_number in value_p2:
                        print("Error value received 2 times p1:" + str(message_number))
                        return value_p2, value_p3
                    else:
                        value_p2.add(line_words[2])
                else:
                    value_p3.add(line_words[2])

    return value_p2, value_p3


def verify_sender(number):
    value_p = set()

    # Open the file in read mode
    try:
        with open('../prof_test/proc' + number + '.output', 'r') as file:
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

    return value_p


main()
