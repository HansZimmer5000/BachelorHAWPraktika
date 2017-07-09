%%%-------------------------------------------------------------------
%%% @author Arne Thiele & Michael Müller
%%% @copyright (C) 2017, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 13. Apr 2017 13:31
%%%-------------------------------------------------------------------
-module(server).
-author("Arne Thiele & Michael Müller").

%% API
-export([start/0]).

-define(LATENCY, timer:seconds(extractValueFromConfig(latency))).
-define(CLIENTLIFETIME, timer:seconds(extractValueFromConfig(clientlifetime))).
-define(SERVERNAME, extractValueFromConfig(servername)).
-define(HBQNAME, extractValueFromConfig(hbqname)).
-define(HBQNODE, extractValueFromConfig(hbqnode)).
-define(DLQLIMIT, extractValueFromConfig(dlqlimit)).
-define(SERVERLOG, 'server.log').
-define(CMEMLOG, 'cmem.log').
%------------------------------------------------------------------------------------------------------
%																					>>INIT UND LOOPS<<
%------------------------------------------------------------------------------------------------------

% Startet den Serverprozess an sich.
start() ->
  log_status(start,"\n\n\nServer wird gestartet"),
  log_status(start,io_lib:format("Ping HBQ: ~p",[net_adm:ping(?HBQNODE)])),
  CMEM = cmem:initCMEM(?CLIENTLIFETIME, ?CMEMLOG),
  {?HBQNAME,?HBQNODE} ! {self(), {request, initHBQ}},
  receive
    {reply, ok} ->
      log_status(loop,"HBQ ist initialisiert");
    _Any -> 
      log_status(loop,"HBQ ist NICHT initialisiert")
  end,
  ServerPid = spawn(fun() -> loop(1, CMEM) end),
  register(?SERVERNAME, ServerPid),
  ServerPid.

% Der Serverloop / Der Receiveblock um Nachrichten entgegenzunehmen.
% Hält unter anderem auch die "Nummernverwaltung".
% Heißt ActualMessageID startet bei 1 und wirt mit jedem loop hochgezählt.
loop(ActualMessageID, CMEM) ->
  {ok,ServerTimer} = timer:send_after(?LATENCY,self(),{request,killAll}),
  receive
    {reply, ok} ->
      log_status(loop,"dropMessage OK"),
      timer:cancel(ServerTimer),
      log_status(loop, "Timer cancled"),
      loop(ActualMessageID,CMEM);
    {PID, getmessages} ->
      timer:cancel(ServerTimer),
      log_status(loop, "Timer cancled"),
      NewCMEM = getmessageHandler(PID, CMEM),
      loop(ActualMessageID, NewCMEM);
    {PID, getmsgid} ->
      timer:cancel(ServerTimer),
      log_status(loop, "Timer cancled"),
      getMessageIDHandler(PID, ActualMessageID),
      loop(ActualMessageID + 1, CMEM);
    {dropmessage, MsgLst} ->
      timer:cancel(ServerTimer),
      log_status(loop, "Timer cancled"),
      dropMessageHandler(MsgLst),
      loop(ActualMessageID, CMEM);
    {request,killAll} ->
      log_status(loop, "Obacht der Server fährt herunter!"),
      killHandler(CMEM);
    Any ->
      log_status(loop, io_lib:format("ERR: Etwas erhalten, was nicht zugeordnet werden konnte ~p",[Any])),
      loop(ActualMessageID, CMEM)
  end.

%------------------------------------------------------------------------------------------------------
%																	>>SCHNITTSTELLEN UND HANDLER<<
%------------------------------------------------------------------------------------------------------

% Schnittstelle an welche sich der Client wenden kann um neue Nachrichten zu erhalten.
getmessageHandler(PID, CMEM) ->
  log_start(getmessageHandler),
  ClientNNr = cmem:getClientNNr(CMEM, PID),
  {?HBQNAME,?HBQNODE} ! {self(),{request,deliverMSG,ClientNNr,PID}},
  %An dieser Stelle keine Any-Catch Block, damit nicht alle Messages aus der ERLANG-MessageQueue genommen werden!
  receive
    {reply, SentNNr} when is_number(SentNNr) ->
      NewCMEM = cmem:updateClient(CMEM,PID,SentNNr,?CMEMLOG),
      log_status(getmessageHandler, io_lib:format("Nachricht ~p an ~p versendet",[SentNNr,PID])),
      NewCMEM;
    {request,killAll} ->
      log_status(loop, "Obacht der Server fährt aus dem getMessageHandler herunter!"),
      killHandler(CMEM)
  end.

%Liefert die aktuelle Nachrichtennummer an den Redakteur, welcher diese angefragt hat
getMessageIDHandler(PID, ActualMessageID) ->
  log_status(getMessageIDHandler, io_lib:format("input PID: ~p, ActualMessageID: ~p",[PID, ActualMessageID])),
  PID ! {nid, ActualMessageID},
  log_status(getMessageIDHandler, io_lib:format("erhalten: getmsgid ~n gesendet: ~p",[ActualMessageID])).

%Delegiert die Verwaltung von Nachrichten an die HBQ-Komponente weiter.
dropMessageHandler([NNr,MessageTxt,TSClientOut]) ->
  log_start(dropMessage),
  {?HBQNAME,?HBQNODE} ! {self(),{request,pushHBQ,[NNr,MessageTxt,TSClientOut]}},
  TSNow = erlang:timestamp(),
  IsFuture = werkzeug:lessTS(TSNow,TSClientOut),
  if
    IsFuture ->
      TSDiff = werkzeug:diffTS(TSNow,TSClientOut),
      log_status(dropMessage, io_lib:format("Aus der Zukunft (Diff: ~p) erhalten: dropmessage mit Inhalt ~p",[TSDiff, [NNr,MessageTxt,TSClientOut]]));
    true ->
      log_status(dropMessage, io_lib:format("erhalten: dropmessage mit Inhalt ~p",[[NNr,MessageTxt,TSClientOut]]))
  end.

%Die kill-Methoden geschehen erst zum Ende des Durchlaufs, deswegen werden keine neuen, leeren ADTs an die loops übergeben
killHandler(CMEM) ->
  {?HBQNAME,?HBQNODE} ! {self(), {request, dellHBQ}},
  receive
    {reply,ok} -> log_status(killHandler, io:format("HBQ wurde erfolgreich gelöscht"))
  end,
  AtomAnswer = cmem:delCMEM(CMEM),
  if(AtomAnswer == ok) ->
    log_status(killHandler, io:format("CMEM wurde erfolgreich gelöscht"));
    true ->  log_status(killHandler, io:format("ERR: CMEM wurde NICHT gelöscht"))
  end,
  exit(self(),kill).


%------------------------------------------------------------------------------------------------------
%																					>>HILFSMETHODEN<<
%------------------------------------------------------------------------------------------------------


%------------------------------------------------------------------------------------------------------
%																					>>LOGGING UND CONFIG<<
%------------------------------------------------------------------------------------------------------

% Holt aus der Configdatei (server.cfg) den benötigten Wert für den eingegebenen Key.
% Erwartet Configdatei mit "{key1,value1}. {key2,value2}. ....."
extractValueFromConfig(Key) ->
  %log_status(extractValueFromConfig,io_lib:format("Key: ~p",[Key])),
  {ok, ConfigListe} = file:consult("server.cfg"),
  {ok, Value} = werkzeug:get_config_value(Key, ConfigListe),
  Value.

% Logt start einer Funktion
log_start(Funktion) -> log_status(Funktion, io_lib:format("~p started. ~n",[Funktion])).

% Logt den aktuellen Status / Wert / ... einer Funktion.
log_status(Funktion,Status) -> werkzeug:logging(?SERVERLOG, io_lib:format("~p ~p hat status: ~s. ~n",[werkzeug:timeMilliSecond(), Funktion, Status])).