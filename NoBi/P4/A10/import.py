#!/usr/bin/env python3

# Mit diesem Script kann die im Praktikum gestellte 'plz.data' Datei in eine MongoDB importiert werden
# Bevor das Script ausgefuehrt wird muss die MongoDB gestartet werden!
# Name der Datenbank soll 'test' lauten
# Die Datei 'plz.data' muss sich im gleichen Verzeichnis befinden wie dieses Script

import happybase
import json
import re as regEx

FILE_PATH = ("plz.data")
TABLE_NAME = 'orte'

# Verbindung herstellen
con = happybase.Connection(autoconnect=True)
con.open()

# Neue Tabelle erstellen
con.delete_table(TABLE_NAME, True)
families = {'daten': dict()}
con.create_table(TABLE_NAME,families)

table = con.table(TABLE_NAME)
batch = table.batch()

#table.put('01001',{	b'daten:name': 'Hamburg'.encode(),
#					b'daten:pop': b'5'})

# File oeffnen
file = open(FILE_PATH, 'r')

for line in file:
		#V1: result = store_in_dictionary(line)
		jsondict = json.loads(line)
		plz = jsondict.get('_id')
		city = jsondict.get('city')
		state = jsondict.get('state')
		loc = str(jsondict.get('loc'))
		pop = str(jsondict.get('pop'))
		print(plz)
		batch.put(	plz.encode(),
					{	
						b'daten:city': city.encode(), 
						b'daten:state': state.encode(),
						b'daten:loc': loc.encode(),
						b'daten:pop': pop.encode()
					}
				)
batch.send()


print(table.row(b'01001'))
print(table.row(b'01054'))

# Tabelle loeschen
#con.delete_table(TABLE_NAME, True)