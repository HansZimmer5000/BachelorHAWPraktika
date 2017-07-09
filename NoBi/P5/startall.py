#!/usr/bin/env python

import sys
import os
import result

from time import sleep

####################################
#   Funktionen
####################################
def startAll(startSparkBool):
    startZookeeper()
    sleep(5)
    startKafka()
    sleep(5)
    startStream()
    if(startSparkBool):
        sleep(5)
        startSpark()

#-----------------------------------
def startZookeeper():
    tag = "zookeeper"
    printStart(tag)
    command = r"C:\Users\Michael\Desktop\kafka_2.11-0.10.2.0\bin\windows/zookeeper-server-start.bat C:\Users\Michael\Desktop\kafka_2.11-0.10.2.0\config/zookeeper.properties"
    openCMD(command)

#-----------------------------------
def startKafka():
    tag = "kafka"
    printStart(tag)
    command = r"C:\Users\Michael\Desktop\kafka_2.11-0.10.2.0\bin\windows/kafka-server-start.bat C:\Users\Michael\Desktop\kafka_2.11-0.10.2.0\config/server.properties"
    openCMD(command)

#-----------------------------------
def startStream():
    tag = "stream"
    printStart(tag)
    command = r"python C:\Users\Michael\IdeaProjects\Nobi17\P5/stream.py"
    openCMD(command)

#-----------------------------------
def startSpark():
    tag = "spark"
    printStart(tag)
    command = r"C:\Users\Michael\Sparktest\spark-2.1.1-bin-hadoop2.4\bin/spark-submit --jars C:\Users\Michael\Desktop\kafka_2.11-0.10.2.0/spark-streaming-kafka-0-8-assembly_2.11-2.1.1.jar C:\Users\Michael\IdeaProjects\Nobi17\P5/spark.py"
    openCMD(command)

#-----------------------------------
def startResults():
    tag = "results"
    printStart(tag)
    result.collectResults()

#-----------------------------------
def openCMD(command):
    os.system("start cmd /c " + command)

#-----------------------------------
def printStart(command):
    print("--> Starting: " + command)

####################################
#   Skript start
####################################

# Wichtig für den Unterschied, ob diese File imported wird oder ausgeführt. Wenn ausgeführt ist der __name__ eben gleich __main__.
if __name__ == '__main__':
    while True: 
        command = input("\nBefehle oder Befehlsnummer eingeben:\n[zoo,kafka,stream,spark,results]\n")

        if(command == "end"):
            break
        elif(command in ["1","zoo"]):
            startZookeeper()
        elif(command in ["2","kafka"]):
            startKafka()
        elif(command in ["3","stream"]):
            startStream()
        elif(command in ["4","spark"]):
            startSpark()
        elif(command in ["5","result"]):
            startResults()



