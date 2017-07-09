#!/usr/bin/env python3

# Mit diesem Script kann die im Praktikum gestellte 'plz.data' Datei in eine MongoDB importiert werden
# Bevor das Script ausgefuehrt wird muss die MongoDB gestartet werden!
# Name der Datenbank soll 'test' lauten
# Die Datei 'plz.data' muss sich im gleichen Verzeichnis befinden wie dieses Script

import happybase
import json

FILE_PATH = ("plz.data")
TABLE_NAME = 'orte'



# Verbindung herstellen
con = happybase.Connection(autoconnect=True)
con.open()


table = con.table(TABLE_NAME)
batch = table.batch()

print(table.row('71646'))

for row in table.scan(columns=[b'daten:city']):
	plz = row[0]
	city = row[1]['daten:city']
	if(city == 'HAMBURG' or city == 'BREMEN'):
		batch.put(plz,{b'fussball:': b'ja'})


batch.send()

#print(table.row('71646',columns=[b'fussball:']))
print(table.row('71646'))
print(table.row('01001'))

