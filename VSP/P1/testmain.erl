%%%-------------------------------------------------------------------
%%% @author Arne Thiele & Michael Müller
%%% @copyright (C) 2017, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 30. Mär 2017 08:42
%%%-------------------------------------------------------------------
-module(testmain).
-author("Arne Thiele & Michael Müller").

%% API
-export([start/0, testfun/1]).

%% CONSTANTS
-define(SERVERNAME, extractValueFromConfig("client.cfg",servername)).
-define(SERVERNODE, extractValueFromConfig("client.cfg",servernode)).
-define(HBQNAME, extractValueFromConfig("server.cfg",hbqname)).
-define(HBQNODE, extractValueFromConfig("server.cfg",hbqnode)).
-define(TESTLOG,'test.log').

-define(TESTNNR, 1).
-define(TESTMSG, "Nachricht pushHBQTest").

% Holt aus der Configdatei (client.cfg) den benötigten Wert für den eingegebenen Key.
% Erwartet Configdatei mit "{key1,value1}. {key2,value2}. ....."
extractValueFromConfig(File,Key) ->
  log_status(extractValueFromConfig,io_lib:format("File: ~p Key: ~p",[File, Key])),
  {ok, ConfigListe} = file:consult(File),
  {ok, Value} = werkzeug:get_config_value(Key, ConfigListe),
  Value.

% Nebeläufigkeits Beispiel
start() ->
	HBQInterface = {?HBQNAME,?HBQNODE},
	ServerPid = spawn(fun() -> tests(HBQInterface) end),
	register('test',ServerPid),
	ServerPid.

tests(HBQInterface) ->
	IsInitiated = initHBQTest(HBQInterface),
	if
		IsInitiated ->
			PushDeliverOK = pushAndDeliverHBQTest(HBQInterface,?TESTNNR),
			Push3 = pushHBQTest(HBQInterface,?TESTNNR+2),
			Push2 = pushHBQTest(HBQInterface,?TESTNNR+1),
			if
				PushDeliverOK and Push2 and Push3 ->
					log_status(test,"Every Test ok!")
			end
	end.

initHBQTest(HBQInterface) ->
	HBQInterface ! {self(),{request,initHBQ}},
	receive
		{reply, ok} -> 
			log_status(initHBQTest, "OK"),
			Result = true,
			Result;
		Any ->
			log_status(initHBQTest, io_lib:format("FAIL: ~p",[Any])),
			Result = false,
			Result
	end.





pushAndDeliverHBQTest(HBQInterface,NNr) ->
	log_status(pushAndDeliverHBQTest,io_lib:format("input: NNr: ~p",[NNr])),
	PushOK = pushHBQTest(HBQInterface,NNr),
	DeliverOK = deliverMSGTest(HBQInterface,NNr),
	log_status(pushAndDeliverHBQTest,io_lib:format("output: NNr: ~p PushOK: ~p DeliverOK: ~p",[NNr, PushOK,DeliverOK])),
	PushOK and DeliverOK.

pushHBQTest(HBQInterface,NNr) ->
	log_status(pushHBQTest,io_lib:format("input: NNr: ~p",[NNr])),
	HBQInterface ! {self(), {request,pushHBQ,[NNr,[?TESTMSG],erlang:timestamp()]}},
	log_status(pushHBQTest,"an HBQ gesendet"),
	receive
		{reply, ok} -> 
			log_status(pushHBQTest, "OK"),
			Result = true;
		Any ->
			log_status(pushHBQTest, io_lib:format("FAIL: ~p",[Any])),
			Result = false
	end,
	Result.

deliverMSGTest(HBQInterface,NNr) ->
	log_status(deliverMSGTest,io_lib:format("input: NNr: ~p",[NNr])),
	HBQInterface ! {self(), {request,deliverMSG,NNr,self()}},
	receive
		{reply, SentNNr} -> 
			if
				SentNNr == NNr ->
					log_status(deliverMSGTest, "SentNNr OK"),
					Result = true;
				true ->
					log_status(deliverMSGTest, "SentNNr fail"),
					Result = false
			end
	end,
	log_status(deliverMSGTest,"reply,SentNNr bekommen."),
	receive
		{reply,[SentNNr|_MsgRest],false} ->
			if
				SentNNr == NNr ->
					log_status(deliverMSGTest, "ReceivedMessage OK"),
					Result = Result and true;
				true ->
					log_status(deliverMSGTest, "ReceivedMessage fail"),
					Result = Result and false
			end
	end,
	log_status(deliverMSGTest,"reply,[Message] bekommen."),
	Result.


testfun(X) ->
	if
		X == 3 ->
			3;
		true ->
			{lel,honk}
	end.

%------------------------------------------------------------------------------------------------------
%																					>>LOGGING UND CONFIG<<
%------------------------------------------------------------------------------------------------------

% Logt den aktuellen Status / Wert / ... einer Funktion.
log_status_file(Funktion,Status,Datei) -> werkzeug:logging(Datei, io_lib:format("~p ~p hat status: ~s. ~n",[werkzeug:timeMilliSecond(), Funktion, Status])).
log_status(Funktion,Status) -> log_status_file(Funktion,Status,?TESTLOG).