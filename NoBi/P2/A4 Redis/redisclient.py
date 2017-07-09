#!/usr/bin/env python3

# Bevor das Script ausgefuehrt werden darf muss der redis server laufen und schon Daten importiert haben!
# dann starten des Scripts mit: python redisclient

import redis
import time
import re as regEx

CONNECTION = redis.StrictRedis(host='localhost', port=6379, db=0)
FILE_PATH = "plz.data"

while True: 
	key = raw_input("PLZ oder Stadt eingeben:\n")

	if(key == "stop"):
		break
	elif(type(key) == str):
		try:
			int(key)
			id_str = 'id:0'.replace('0',key)
			time1 = time.time()
			print(CONNECTION.hgetall(id_str))
			time2 = time.time()
			print(time2-time1)
		except ValueError:
			time1 = time.time()
			print(CONNECTION.smembers(key.upper()))
			time2 = time.time()
			print(time2-time1)
	else:
		print("User Input nicht verstaendlich!\n")
