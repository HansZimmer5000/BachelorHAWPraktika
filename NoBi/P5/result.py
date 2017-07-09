#!/usr/bin/env python

import os

from datetime import datetime

####################################
#   Funktionen
####################################
def collectResults():
	mypath = os.getcwd()
	resultFile = open(os.getcwd() + "\\" + "result.txt",'a')

	directories = []
	for (dirpath, dirnames, filenames) in os.walk(mypath):
		directories = dirnames
		break

	headString = "Results from " + str(datetime.now()) + "\n"
	resultFile.write(headString)

	for directory in directories:
		directoryPath = mypath + "\\" + directory
		for file in os.listdir(directoryPath):
			# Nur Files mit "part-" im Namen und "crc" nicht im Namen
			if("part-" in file and not "crc" in file):
				tmpfile = open(directoryPath + "\\" + file,"r")
				resultFile.write(tmpfile.read()[:-1]+"\n")
				tmpfile.close()

	resultFile.close()

	resultFile = open(os.getcwd() + "\\" + "result.txt",'r')
	for index, line in enumerate(resultFile):
		pass
	print("Total Lines: " + str(index))

	resultFile.close()

####################################
#   Skript start
####################################

# Wichtig für den Unterschied, ob diese File imported wird oder ausgeführt. Wenn ausgeführt ist der __name__ eben gleich __main__.
if __name__ == '__main__':
    collectResults()