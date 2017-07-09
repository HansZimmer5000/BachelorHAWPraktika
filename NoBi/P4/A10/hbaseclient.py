#!/usr/bin/env python3

import happybase
import json
import time

FILE_PATH = ("plz.data")
TABLE_NAME = 'orte'



# Verbindung herstellen
con = happybase.Connection(autoconnect=True)
con.open()

table = con.table(TABLE_NAME)

while True: 
	key = raw_input("PLZ oder Stadt eingeben:\n")

	if(key == "stop"):
		break
	elif(type(key) == str):
		try:
			int(key)
			id_str = str(key)
			time1 = time.time()
			res = table.row(id_str,columns=[b'daten:city'])['daten:city']
			time2 = time.time()
			print("Zeit fuer DB Anfrage:" + str(time2-time1))
			print(res)
		except ValueError:
			time1 = time.time()
			res = []
			#V1for plz,citydict in table.scan(columns=[b'daten:city']):
			for plz, citydict in table.scan(filter="SingleColumnValueFilter ('daten', 'city', =, 'binary:"+key.encode()+"')"): 
				city = citydict['daten:city']
				#v1 if(city == key):
				res.append(plz)
			time2 = time.time()
			print("Zeit fuer DB Anfrage:" + str(time2-time1))
			for plz in res:
				print(plz)
	else:
		print("User Input nicht verstaendlich!\n")












