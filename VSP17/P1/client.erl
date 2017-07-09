%%%-------------------------------------------------------------------
%%% @author Arne Thiele & Michael Müller
%%% @copyright (C) 2017, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 13. Apr 2017 13:31
%%%-------------------------------------------------------------------
-module(client).
-author("Arne Thiele & Michael Müller").

%% API
-export([start/1]).

%% CONSTANTS
-define(CLIENTAMOUNT, extractValueFromConfig(clients)).
-define(LIFETIME, timer:seconds(extractValueFromConfig(lifetime))).
-define(SERVERNAME, extractValueFromConfig(servername)).
-define(SERVERNODE, extractValueFromConfig(servernode)).
-define(SENDEINTERVALL, timer:seconds(extractValueFromConfig(sendeintervall))).
-define(LOGFILENAME, "MainClient.log").

% Einheitliche Nachricht siehe Vorgaben -> Im labor anpassen, weil dann erst PC bekannt.
%------------------------------------------------------------------------------------------------------
%																					>>INIT UND LOOPS<<
%------------------------------------------------------------------------------------------------------

% Startet eine in der client.cfg Datei festgelegte Anzahl von Clients
start(Name) ->
	log_status(start,"\n\n\nAlle Clients werden gestartet"),
	log_status(start,io_lib:format("Ping Server: ~p",[net_adm:ping(?SERVERNODE)])),
	ClientPidList = start_n_clients(Name,?CLIENTAMOUNT,[]),
	timer:sleep(?LIFETIME),
	killAllClients(ClientPidList).

% Startet den Clientprozess an sich.
% Gibt eine Liste an ClientPids zurück.
start_n_clients(Name,RestAnzahl, PIDList) ->
	if 
		RestAnzahl == 0 ->
			log_status(start_n_clients,io_lib:format("Alle Clients sind gestartet: ~p",[PIDList])),
			PIDList;
		RestAnzahl > 0 ->
			LogFile = io_lib:format("Client~p.log",[RestAnzahl]),
			TmpClientPid = spawn(fun() -> loop_redakteur(0,[],?SENDEINTERVALL, LogFile) end),
			register(list_to_atom(lists:concat([Name,RestAnzahl])),TmpClientPid),
			log_status(start_n_clients,io_lib:format("Client ~p~p startet mit: ~p", [Name,RestAnzahl,TmpClientPid])),
			start_n_clients(Name,RestAnzahl-1,[TmpClientPid|PIDList])
	end.

% Code für den Redakteurs "Loop", mit Indikator wie viele Nachrichten schon geschrieben wurden.
% Hat der Redakteur 5 Nachrichten geschrieben, wie die letzte angeforderte ID schlich nicht genutzt und der Leser_loop kommt dran.
loop_redakteur(MsgCounter,WrittenMessages,SendeintervallInSec, LogFile) ->
	log_status_file(loop_redakteur,"start",LogFile),
		getmsgid(LogFile),
		receive
			{nid, Number} -> 
				log_status_file(loop_redakteur,"Redakteur hat neue ID erhalten und schlaeft ein",LogFile),
				timer:sleep(SendeintervallInSec),
				log_status_file(loop_redakteur,"Redakteur wacht wieder auf",LogFile),
				if
					MsgCounter < 5 ->
						handle_new_id(Number,LogFile),
						loop_redakteur(MsgCounter + 1, [Number|WrittenMessages],SendeintervallInSec, LogFile);
					true ->
						log_status_file(loop_redakteur,io_lib:format("Wechsel zu Leser, Nachrichtnummer: ~p 'vergessen' zu senden",[Number]), LogFile),
						NewSendeintervallInSec = calcNewIntervall(SendeintervallInSec,LogFile),
						loop_leser(WrittenMessages, NewSendeintervallInSec, LogFile)
				end;
			{kill} -> 
				killHandler(LogFile);
			Any ->
				log_status_file(loop_redakteur, io_lib:format("Recieved Somehting I don't understand: ~p",[Any]),LogFile),
				loop_redakteur(MsgCounter, WrittenMessages,SendeintervallInSec, LogFile)
		end.

% Code für den Lesers "loop"
% Gibt es keine Nachrichten mehr, wechsel zu Redakteur.
loop_leser(WrittenMessages,SendeintervallInSec, LogFile) ->
	log_status_file(loop_leser,"start",LogFile),
	getmessage(LogFile),
	receive 
		{reply, [NNr,Message,_TS1,_TS2,_TS3,_TS4],Termi} -> 
			log_status(loop_leser,io_lib:format("Empfagene Nummer: ~p, Msg: ~s",[NNr, Message])),
			IsMember = lists:member(NNr,WrittenMessages),
			readMessageHandler([NNr,Message,_TS1,_TS2,_TS3,_TS4],Termi,IsMember,LogFile),
			if
				Termi ->
          log_status_file(loop_leser,"Wechsel zu Redakteur",LogFile),
          loop_redakteur(0,WrittenMessages,SendeintervallInSec, LogFile);
				true ->
          loop_leser(WrittenMessages,SendeintervallInSec, LogFile)
			end;
		{kill} -> 
			killHandler(LogFile);
		Any ->
			log_status_file(loop_leser, io_lib:format("Recieved Somehting I don't understand: ~p",[Any]),LogFile),
			loop_leser(WrittenMessages,SendeintervallInSec, LogFile)
	end.

%------------------------------------------------------------------------------------------------------
%																	    >>SCHNITTSTELLEN UND HANDLER<<
%------------------------------------------------------------------------------------------------------

%Fragt beim Server eine Nachricht ab.
getmessage(LogFile) ->
	log_status_file(getmessage,"started",LogFile),
	{?SERVERNAME, ?SERVERNODE} ! {self(),getmessages}.

%Fragt beim Server eine neue Nachrichten ID ab.
getmsgid(LogFile) ->
	log_status_file(getmsgid,"started",LogFile),
	{?SERVERNAME, ?SERVERNODE} ! {self(),getmsgid}.

%Behandelt die eingehende (zu lesende) Nachricht
readMessageHandler([NNr,_Message,_TS1,_TS2,_TS3,TSDLQout],Termi,IsMember,LogFile) ->
	TSNow = erlang:timestamp(),
	IsFuture = werkzeug:lessTS(TSNow,TSDLQout),
	if
		IsFuture ->
			FutureTxt = io_lib:format("Differenz: ~p", [werkzeug:diffTS(TSNow,TSDLQout)]);
		true ->
			FutureTxt = ""
	end,

	if
		IsMember ->
			log_status_file(loop_leser, io_lib:format("Future: ~p ~p // Received own MessageNummer: ~p, Termi: ~p",[IsFuture,FutureTxt,NNr,Termi]),LogFile);
		true ->
			log_status_file(loop_leser, io_lib:format("Future: ~p ~p// Received MessageNummer: ~p, Termi: ~p",[IsFuture,FutureTxt,NNr,Termi]),LogFile)
	end.

killHandler(LogFile) ->
	log_status_file(killHandler,"Client wurde vom Hauptclientprozess gekillt",LogFile).

%------------------------------------------------------------------------------------------------------
%																					>>HILFSMETHODEN<<
%------------------------------------------------------------------------------------------------------

%Kalkuliert ein neues Senderintervall anhand des altens. 
calcNewIntervall(OldSendeintervallInSec,LogFile) ->
	log_status_file(calcNewIntervall,"started",LogFile),
	TmpSenderIntervallInSec = rand:normal() * OldSendeintervallInSec,
	NewSendeintervallInSec = round((OldSendeintervallInSec + TmpSenderIntervallInSec) / timer:seconds(2)),
	log_status_file(calcNewIntervall,io_lib:format("New: ~p",[NewSendeintervallInSec]),LogFile),
	ResultSendeIntervallInSec = max(NewSendeintervallInSec,2),
	log_status_file(calcNewIntervall,io_lib:format("Result: ~p",[ResultSendeIntervallInSec]),LogFile),
	ResultSendeIntervallInSec.

%Behandle eingekommene neue NachrichtenId.
handle_new_id(Number,LogFile) ->
	if 	
		Number > 0 ->
			log_status_file(handle_new_id,io_lib:format("~pte Nachricht erstellt",[Number]),LogFile),
			drop_message(write_new_message(Number,LogFile),LogFile);
		true -> log_status_file(handle_new_id,"Number is <= 0",LogFile)
	end.

%Erstellt eine neue Nachricht anhand der Nummer.
%Beinhaltet: Rechnername, Praktikumsgruppe, Teamnummer, aktuelle Systemzeit (TS Clientout?)
write_new_message(Nachrichtnummer,LogFile) ->
	log_status_file(write_new_message,"started",LogFile),
	Rechnername = labXY, 
	Praktikumsgruppe = 02, 
	Teamnummer = 03, 
	[Nachrichtnummer,
		[io_lib:format("~pte Nachricht von PC/PraGr/Team ~p/~p/~p erstellt",
			[
				Nachrichtnummer, Rechnername, Praktikumsgruppe, Teamnummer
			])
		],
		erlang:timestamp()
	].

%Schickt eine Nachricht an den Server
drop_message(Message, LogFile) ->
	log_status_file(drop_message,"started",LogFile),
	{?SERVERNAME, ?SERVERNODE} ! {dropmessage,Message}.

killAllClients([]) -> 
	log_status(killAllClients, "Alle Clients wurden getötet");
killAllClients([HeadClient|RestClients]) ->
	%exit(HeadClient,kill),
	HeadClient ! {kill},
	log_status(killAllClients, io_lib:format("Der Client ~p wurde zur Selbstzerstörung überredet", [HeadClient])),
	killAllClients(RestClients).

%------------------------------------------------------------------------------------------------------
%																				>>LOGGING UND CONFIG<<
%------------------------------------------------------------------------------------------------------

% Holt aus der Configdatei (client.cfg) den benötigten Wert für den eingegebenen Key.
% Erwartet Configdatei mit "{key1,value1}. {key2,value2}. ....."
extractValueFromConfig(Key) ->
	{ok,ConfigListe} = file:consult("client.cfg"),
	{ok, Value} = werkzeug:get_config_value(Key,ConfigListe),
	Value.

% Logt den aktuellen Status / Wert / ... einer Funktion.
log_status_file(Funktion,Status,Datei) -> werkzeug:logging(Datei, io_lib:format("~p ~p hat status: ~s. ~n",[werkzeug:timeMilliSecond(), Funktion, Status])).
log_status(Funktion,Status) -> werkzeug:logging(?LOGFILENAME, io_lib:format("~p ~p hat status: ~s. ~n",[werkzeug:timeMilliSecond(), Funktion, Status])).