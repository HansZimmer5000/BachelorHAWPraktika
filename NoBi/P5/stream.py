#!/usr/bin/env python

from __future__ import print_function
from satori.rtm.client import make_client, SubscriptionMode
from kafka import KafkaProducer

import socket
import json
import sys
import threading
import time
import requests
import spark

intervallInSec = spark.intervallInSec

####################################
#   Funktionen
####################################
def runSatoriStream():

    # Variablen für Kafka
    brokers = "localhost:9092"
    topic = "test"
    producer = KafkaProducer(bootstrap_servers=[brokers])

    # Variablen für Satori
    channel = "air-traffic"
    endpoint = "wss://open-data.api.satori.com"
    appkey = "AB26D7CF24aCd68C8dcBEB9deBe03d87"


    # Erstellen des Satori Streams
    with make_client(endpoint=endpoint, appkey=appkey) as client:
       
       # SubscriptionObserver Klasse
        class SubscriptionObserver(object):
            def on_subscription_data(self, data):
                for message in data['messages']:
                    mailbox.append(message)
                    got_message_event.set()

        print('Connected!')

        # Variablen für Satori Stream
        mailbox = []
        got_message_event = threading.Event()
        subscription_observer = SubscriptionObserver()

        # Client starten
        client.subscribe(
            channel,
            SubscriptionMode.SIMPLE,
            subscription_observer)

        # Abbruchbedingung
        if not got_message_event.wait(10):
            print("Timeout while waiting for a message")
            sys.exit(1)

        # Solang Nachrichten reinkommen, schick sie an Kafka
        while True:
            for messageJson in mailbox:
                # Verwandle Jsonnachricht zu String
                messageString = json.dumps(messageJson, ensure_ascii=False)
                # Nachricht nun in Kafka Stream
                producer.send(topic, messageString.encode())
                # Mit Zeitverzögerung, so dass Hardware nicht überfordert wird
                time.sleep(0.1)
#---------------------------------------
def runVirtualRadarStream():
    # Dokumentation der Rest API:
    #   http://www.virtualradarserver.co.uk/Documentation/Formats/AircraftList.aspx
    # Kleinere Testdaten: 
    #   https://public-api.adsbexchange.com/VirtualRadar/AircraftList.json?lat=33.433638&lng=-112.008113&fDstL=0&fDstU=100
    
    # Variablen für VirtualRadar
    baseUrl = 'https://public-api.adsbexchange.com/VirtualRadar/AircraftList.json'
    queryParams = {}
    headers = {}

    # Variablen für Kafka
    brokers = "localhost:9092"
    topic = "test"
    producer = KafkaProducer(bootstrap_servers=[brokers])

    print("Starting Virtual Radar Stream")

    while True:
        # Neue Anfrage
        response = requests.get(baseUrl, headers=headers, params=queryParams)

        acList = response.json()["acList"]
        cleanCount = 0
        for jsonObject in acList:
            jsonObjectClean = cleanVRSJsonObject(jsonObject)
            if jsonObjectClean != {}:
                cleanCount = cleanCount + 1
                messageString = json.dumps(jsonObjectClean, ensure_ascii=False)
                producer.send(topic, messageString.encode())
        print("---------")
        print("Inputlength: " + str(len(acList)))
        print("Outputlength: "+ str(cleanCount))
        print("Filtered Count: " + str(len(acList) - cleanCount))
        # Mit Zeitverzögerung, so dass Hardware nicht überfordert wird
        time.sleep(intervallInSec)
#---------------------------
def cleanVRSJsonObject(jsonObject):
    # Cleaned ein JsonObjekt im Sinne von, es werden nur die Keys übernommen die wir auch brauchen. 
    # Wenn min. einer von den Keys nicht vorhanden ist wird das Objekt verworfen.
    vrsConvertionKeys = {
        "GAlt" : "altitude",
        "Call" : "callsign",
        "Type" : "type",
        "Spd"  : "speed",
        "SpdTyp" : "speedType"
    }

    neededKeys = list(vrsConvertionKeys.keys())

    cleanJsonObject = {}
    for key in neededKeys:
        try:
            newValue = jsonObject[key]
        except KeyError:
            return {}
        newKey = vrsConvertionKeys[key]
        cleanJsonObject.update({newKey: newValue})

    return cleanJsonObject

#---------------------------
def prepareVirtualRadarStream():
    
    # Erste Anfrage (enthält noch alle Daten)
    lastId = 0
    headerIds = []

    baseUrl = "https://public-api.adsbexchange.com/VirtualRadar/AircraftList.json"

    response = requests.get(baseUrl)
    acList = response.json()["acList"]

    for jsonObject in acList:
        headerIds.append(jsonObject["Id"])


    lastId = headerIds[0]
    return (lastId, headerIds)

####################################
#   Skript start
####################################

# Wichtig für den Unterschied, ob diese File imported wird oder ausgeführt. Wenn ausgeführt ist der __name__ eben gleich __main__.
if __name__ == '__main__':
    #runSatoriStream()
    runVirtualRadarStream()