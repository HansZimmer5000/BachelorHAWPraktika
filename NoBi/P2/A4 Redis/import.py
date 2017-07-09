#!/usr/bin/env python3

# Bevor das Script ausgefuehrt wird muss der Redis server laufen!
# Dies geht mit: redis server

import redis
import re as regEx

CONNECTION = redis.StrictRedis(host='localhost', port=6379, db=0)
FILE_PATH = "plz.data"
REGEXPKEY = '"([_a-zAA-Z]+)"'
REGEXPVALUE = '"?([\- 0-9a-zA-Z]+)"?'


# Hilfsfunktion um Zeile einzulesen
# Erwartet eine Zeile mit Key / Value Paar(en)
def store_in_dictionary(input):
		result = {}
		# Erkennt, dass Zeile (input) schon mit Key Value versehen, und wendet auf jedes Paar den match an.
		# Matcht aktuell '_id' 'city' 'pop' und 'state'
		for match in regEx.findall(REGEXPKEY + ' : ' + REGEXPVALUE,input):
			result[match[0]] = match[1]
		return result

# Hauptarbeit des Skriptes
file = open(FILE_PATH, 'r')
# Gelesene Datei nun Zeile fuer Zeile matchen (regExp) und in die DB einfuegen.
for line in file:
		result = store_in_dictionary(line)
		plz = result.get('_id')
		city = result.get('city')
		state = result.get('state')

		id_str = 'id:0'.replace('0',plz)
		dict = {"city": city, "state": state}

		CONNECTION.hmset(id_str, dict)
		CONNECTION.sadd(city, plz)


# Test ob Id 01010 vorhanden ist.
print(CONNECTION.get('01010'))


