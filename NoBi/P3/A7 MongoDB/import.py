#!/usr/bin/env python3

# Mit diesem Script kann die im Praktikum gestellte 'plz.data' Datei in eine MongoDB importiert werden
# Bevor das Script ausgefuehrt wird muss die MongoDB gestartet werden!
# Name der Datenbank soll 'test' lauten
# Die Datei 'plz.data' muss sich im gleichen Verzeichnis befinden wie dieses Script

import pymongo
import json
import re as regEx
from pymongo import MongoClient

FILE_PATH = ("plz.data")

# Erstelle Mongoclient mit Default port und ip
client = MongoClient()
# Verbindung auswaehlen
#con = pymongo.Connection
# DB auswaehlen
db = client.test
# Collection auswaehlen
collection = db.plz
# Hauptarbeit des Skriptes
file = open(FILE_PATH, 'r')
# Gelesene Datei nun Zeile fuer Zeile matchen (regExp) und in die DB einfuegen.
for line in file:
	collection.insert(json.loads(line))
	#print(collection.insert(line).inserted_id)
# Test ob Id 01010 vorhanden ist.
print(collection.count({'_id': 1}))


