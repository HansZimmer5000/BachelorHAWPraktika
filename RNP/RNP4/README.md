"# RNP4" 


Benutzung der Jars:

	java -jar client.jar TYPE STRING ZIELIP LOCALPORT NEXTIP NEXTPORT INT
		:: TYPE 	data/control
		:: STRING 	payload: String (Data) oder type (control)
		:: ZIELIP 	IPV6 Adresse des echo Servers
		:: LOCALPORT 	Port auf den gehorcht wird
		:: NEXTIP 	IPV6 Adresse des ersten Routers
		:: NEXTPORT	Port des ersten Routers
		:: INT 		HopLimit

	java -jar server.jar INT 
		:: INT = Port auf den gehorcht wird

	Hinweis: Localhost fuer ipv6 ist "::1".
	



	Bsp: Bei laufendem Router mit Port 8000(gestartet aus IDE mit entsprechender RoutingTable)	
	Starten des Servers: 		java -jar server.jar 6000
	Starten des Routers: 	 	Eintrag in RoutingTable (0:0:0:0:0:0:0:1/48;0:0:0:0:0:0:0:1;6000)
	Starten des Clients: 		java -jar client.jar Data "ha" ::1 5000 ::1 8000 5

	fuehrt dazu, dass Paket von Client an Router auf Port 8000 geschickt, von diesem an Echo-Server auf Port 6000 weitergeleitet wird. 
	In diesem Fall schickt der Echo-Server das Paket zurueck, und weil bei Localhost die Ziel- und Startadresse gleich sind, wird das echo-Paket vom Router wie das Anfangspaket behandelt und wieder an Server geschickt (Ping-Pong bis Hoplimit == 0).



Einrichten der Routing-Table:

	EA (EndzielAdresse): Adresse, an der das Paket schlussendlich ankommen soll in der Form IP6Adresse/Adressraum)
	NR (NextRouter): Adresse des naechsten Routers auf dem Pfad
	TP (TargetPort): Port, ueber den NR erreicht wird.

	Die Values sind mit Semikolon zu trennen!

	Bsp: 	EA             ;NR            ;TP
			2001:db8:1::/48;2001:db8:0:1::;5000


	Fuer jeden Router/Server, der erreicht werden soll, muss ein Eintrag vorhanden sein.






Testrun.sh:

	Beispiel fuer die Verbindung von Client direkt mit echo-Server. Deprecated

