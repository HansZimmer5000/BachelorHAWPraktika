Autoren: Michael Müller & Arne Thiele

Hinweise zum Anpassen der steuernden Werte:
Der Server, die clients und die HBQ haben jeweils eine .cfg Datei in welcher die jeweiligen Werte angepasst werden können.

Hinweise zum starten der einzelnen Komponenten:
1) Die HBQ Komponente kann anhand der Methode hbq:start(hbq). gestartet werden. Hierzu ist es nötig, dass die Dateien "hbq.erl" und "dlq.erl" vorher
kompiliert wurden. Diese Komponente sollte als erstes gestartet werden.
2) Die Server Komponente lässt sich anhand der Methode server:start(). starten, hierzu muss nur die Datei "server.erl" vorher kompiliert worden sein.
Diese Komponente sollte als zweites gestartet werden.
3) Um n Clients zu starten (vorher in clients.cfg festzulegen), kann die Methode client:start(client) nach vorherigem Kompilieren der "client.erl" aufgerufen werden.
Dies sollte erst geschehen, nachdem die obigen zwei Schritte durchgeführt wurden.