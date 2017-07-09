%%%-------------------------------------------------------------------
%%% @author Arne Thiele & Michael Müller
%%% @copyright (C) 2017, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 13. Apr 2017 13:30
%%%-------------------------------------------------------------------
-module(cmem).
-author("Arne Thiele & Michael Müller").

%% API
-export([initCMEM/2, delCMEM/1, updateClient/4, getClientNNr/2]).

%% CONSTANTS
-define(CMEMLOG,'cmem.log').

%////////////////////////////////
%      CMEM
% Beispiel mit einem Client: [RemTime,{clientpid,timestamp,letztenachrichtennummer}
%////////////////////////////////

%------------------------------------------------------------------------------------------------------
%																					>>INIT UND LOOPS<<
%------------------------------------------------------------------------------------------------------


%------------------------------------------------------------------------------------------------------
%																	>>SCHNITTSTELLEN UND HANDLER<<
%------------------------------------------------------------------------------------------------------

% Initialisiert die CMEM
initCMEM(RemTime,Datei) ->
  log_status(initCMEM, io_lib:format("input: RemTime: ~p, Datei: ~p",[RemTime,Datei])),
  [RemTime].

% Löschen der CMEM
delCMEM(_CMEM) ->
  log_start(delCMEM),
  ok.

% Updated den Client mit der eingegebenen Nachrichtennummer NNr.
updateClient([RemTime|ClientList],ClientId,NNr, _Datei) ->
  log_status(updateClient, io_lib:format("input: ClientId: ~p, NNr: ~p",[ClientId, NNr])),
  NewClientList = updateClient_(ClientList,ClientId,NNr,[]),
  [RemTime|NewClientList].

% Holt die nächste für den Kunden erwartete Nachrichtennummer
getClientNNr([RemTime|ClientList],ClientId) ->
  log_status(getClientNNr,io_lib:format("input: ClientId: ~p",[ClientId])),
  Client = getClient(ClientList,ClientId),
  ClientIsValid = clientIsValid(Client,RemTime),
  % Is Client existent? Yes -> ExpectedNNR = NNR + 1 No? -> = 1
  % Is Client To Old? Yes -> ExpectedNNR = 1.
  if
    ClientIsValid ->
      log_status(getClientNNr,io_lib:format("ClientIsValid: ~p",[Client])),
      {_ClientId,_ClientTS,ClientNNr} = Client,
      ExpectedNNR = ClientNNr + 1;
    true ->
      ExpectedNNR = 1
  end,
  log_status(getClientNNr,io_lib:format("ExpectedNNR Result: ~p",[ExpectedNNR])),
  ExpectedNNR.

%------------------------------------------------------------------------------------------------------
%																					>>HILFSMETHODEN<<
%------------------------------------------------------------------------------------------------------

updateClient_([],ClientId,NNr,Akku) ->
  %Client wurde nicht gefunden und es gibt keine weiteren -> hinten hinzufügen
  log_status(updateClient_,"ClientId in ClientListe nicht gefunden, neuer Eintrag wird erstellt"),
  NewClient = createNewClient(ClientId,NNr),
  NewClientList = [NewClient|Akku],
  NewClientList;
updateClient_([{ClientId,_,_}|ClientRest],ClientId,NNr,Akku) ->
  %Client wurde gefudnen -> einfügen und neue ClientList zurückgeben.
  log_status(updateClient_,"ClientId gefunden"),
  NewClient = createNewClient(ClientId,NNr),
  NewClientList = lists:append([[NewClient],Akku,ClientRest]),
  NewClientList;
updateClient_([{HeadId,HeadTS,HeadNNr}|ClientRest],ClientId,NNr,Akku) ->
  %Client wurde noch nicht gefunden aber noch nicht alle durchsucht -> weiterlaufen
  NewAkku = [{HeadId,HeadTS,HeadNNr}|Akku],
  updateClient_(ClientRest,ClientId,NNr,NewAkku).

% Holt den Client aus der Liste, bzw wenn nicht gefunden leeres Tupel {}
getClient([],_ClientId) ->
  log_status(getClient,"ClientListe leer, Client nicht gefunden"),
  {};
getClient([{ClientId,ClientTS,ClientNNr}|_ClientRest],ClientId) ->
  log_status(getClient,"Client gefunden"),
  {ClientId,ClientTS,ClientNNr};
getClient([{_Id,_TS,_NNr}|RestClients],ClientId) ->
  getClient(RestClients,ClientId).

% erstellt einen neuen Client für die CMEM.
createNewClient(ClientId,NNr)-> {ClientId,erlang:timestamp(),NNr}.

% überprüft ob ein Client OK ist -> Ob es ein richtiges Tupel ist und ob er schon abelaufen ist.
clientIsValid({_ClientId,{MegaSecs,Secs,MilliSecs},_ClientNNr},RemTime) ->
  log_status(clientIsValid,io_lib:format("input: ClientSecs: ~p, RemTime: ~p",[Secs, RemTime])),
  TSPlusRemTime = Secs + RemTime,
  Comparison = werkzeug:compareNow({MegaSecs,Secs,MilliSecs},{MegaSecs,TSPlusRemTime,MilliSecs}),
  if
    (Comparison == before) or (Comparison == concurrent) ->
      log_status(clientIsValid,"ClientTS ist noch nicht ausgelaufen"),
      Result = true;
    Comparison == afterw ->
      log_status(clientIsValid,"ClientTS ist ausgelaufen"),
      Result = false;
    true -> 
      log_status(clientIsValid,"Mit werkzeug:compareNow ging was schief"),
      Result = false
  end,
  Result;
clientIsValid(Any,_) ->
  log_status(clientIsValid, io_lib:format("Kein valider client: ~p",[Any])),
  false.


%------------------------------------------------------------------------------------------------------
%																					>>LOGGING UND CONFIG<<
%------------------------------------------------------------------------------------------------------

% Logt start einer Funktion
log_start(Funktion) -> log_status(Funktion, io_lib:format("~p started. ~n",[Funktion])).

% Logt den aktuellen Status / Wert / ... einer Funktion.
log_status_file(Funktion,Status,Datei) -> werkzeug:logging(Datei, io_lib:format("~p ~p hat status: ~s. ~n",[werkzeug:timeMilliSecond(), Funktion, Status])).
log_status(Funktion,Status) -> log_status_file(Funktion,Status,?CMEMLOG).