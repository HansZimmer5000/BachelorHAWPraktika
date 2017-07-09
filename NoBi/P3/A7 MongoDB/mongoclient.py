#!/usr/bin/env python3

# Bevor das Script ausgefuehrt werden darf muss der redis server laufen und schon Daten importiert haben!
# dann starten des Scripts mit: python redisclient

import pymongo
import time
import json
from pymongo import MongoClient

CLIENT = MongoClient()
DB = CLIENT.test
COLLECTION = DB.plz

while True: 
	key = raw_input("PLZ oder Stadt eingeben:\n")

	if(key == "stop"):
		break
	elif(type(key) == str):
		try:
			int(key)
			id_str = str(key)
			time1 = time.time()
			cursor = COLLECTION.find({"_id":id_str}, {"city": 1})
			time2 = time.time()
			print(time2-time1)
			for res in cursor:
				print(res)
		except ValueError:
			time1 = time.time()
			cursor = COLLECTION.find({"city":key}, {"_id": 1})
			time2 = time.time()
			print(time2-time1)
			for res in cursor:
				print(res)
	else:
		print("User Input nicht verstaendlich!\n")