#!/bin/bash

#starte server und Client als Background-Prozesse
java -jar server.jar 6001 &
java -jar client.jar Data "ha" ::1 5001 ::1 6001 0 &

echo \n
#warte auf Client...
wait %2
#wenn Client durchgelaufen ist, toete Server (Prozess 1) 
kill %1
#fahre fort, wenn Server wirklich tot ist
wait %1
#wenn alles laeuft, gibt es an dieser Stelle keine Jobs mehr, die angezeigt werden.
jobs

printf "\nTestrun done\n"