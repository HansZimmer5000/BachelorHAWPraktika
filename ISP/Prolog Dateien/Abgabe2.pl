%constraint bekommt 2 Variablen (struktur), der 2. Wert jeder Variable Ist zuerst eine Domain.
%Wenn der normale AC3 Fertig ist, werden ggf. Statt Domains konkrete Belegungen an 2. Stelle der struktur stehen!
%constraint(variable(norwegisch,1),_).
%constraint(variable(milch,3),_).

constraint(variable(britisch,Val1,nation),variable(rot,Val1,farbe)).
constraint(variable(schwedisch,Val1,nation),variable(hund,Val1,tier)).
constraint(variable(daenisch,Val1,nation),variable(tee,Val1,getraenk)).
constraint(variable(gruen,Val1,farbe),variable(kaffee,Val1,getraenk)).
constraint(variable(pallmall,Val1,zigaretten),variable(vogel,Val1,tier)).
constraint(variable(gelb,Val1,farbe),variable(dunhill,Val1,zigaretten)).
constraint(variable(winfield,Val1,zigaretten),variable(bier,Val1,getraenk)).
constraint(variable(deutsch,Val1,nation),variable(rothmanns,Val1,zigaretten)).
constraint(variable(gruen,Val1,farbe),variable(weiss,Val2,farbe)):- not(var(Val1)), not(var(Val2)), Val1 is Val2 - 1.
constraint(variable(malboro,Val1,zigaretten),variable(katze,Val2,tier)):- not(var(Val1)), not(var(Val2)), ((Val1 is Val2 + 1) ; (Val1 is Val2 -1)).
constraint(variable(pferd,Val1,tier),variable(dunhill,Val2,zigaretten)):- not(var(Val1)), not(var(Val2)), ((Val1 is Val2 + 1) ; (Val1 is Val2 -1)).
constraint(variable(norwegisch,Val1,nation),variable(blau,Val2,farbe)):- not(var(Val1)), not(var(Val2)), ((Val1 is Val2 + 1) ; (Val1 is Val2 -1)).
constraint(variable(malboro,Val1,zigaretten),variable(wasser,Val2,getraenk)):- not(var(Val1)), not(var(Val2)), ((Val1 is Val2 + 1) ; (Val1 is Val2 -1)).


% Erzeugt alle Variablen mit Standard Domain, un�re constraints werden direkt ber�cksichtigt.
% Variablen werden in Bl�cken nach Kategorie erstellt.
init_variables(Variablen):-
                     InitDomain = [1,2,3,4,5],
                     Variablen = [
                     % InitDomain ist eine Liste
                     % Wird die Variable deutsch auf einen bestimmten Wert gesetzt, wird diese Liste mit einem konkreten Wert ersetzt.
                     % Dies wird vor AC3-FLA passieren

                     % initialisierung Nationlitaeten
                     variable(deutsch,InitDomain,nation),
                     variable(norwegisch,[1],nation),     % Un�rer Constraint! Vorverarbeitung
                     variable(britisch,InitDomain,nation),
                     variable(daenisch,InitDomain,nation),
                     variable(schwedisch,InitDomain,nation),

                     % initialisierung Hausfarbe
                     variable(gelb,InitDomain,farbe),
                     variable(blau,InitDomain,farbe),
                     variable(rot,InitDomain,farbe),
                     variable(gruen,InitDomain,farbe),
                     variable(weiss,InitDomain,farbe),

                     % initialisierung Getraenk
                     variable(tee,InitDomain,getraenk),
                     variable(bier,InitDomain,getraenk),
                     variable(wasser,InitDomain,getraenk),
                     variable(milch,[3],getraenk),         % Un�rer Constraint! Vorverarbeitung
                     variable(kaffee,InitDomain,getraenk),

                     % initialisierung Tier
                     variable(katze,InitDomain,tier),
                     variable(pferd,InitDomain,tier),
                     variable(hund,InitDomain,tier),
                     variable(fisch,InitDomain,tier),
                     variable(vogel,InitDomain,tier),

                     % initialisierung Zigaretten
                     variable(malboro,InitDomain,zigaretten),
                     variable(dunhill,InitDomain,zigaretten),
                     variable(pallmall,InitDomain,zigaretten),
                     variable(rothmanns,InitDomain,zigaretten),
                     variable(winfield,InitDomain,zigaretten)
           ].
%-----------------------------------------------------------------------------------------------------------------------------
%------------------ einstein/0
%-----------------------------------------------------------------------------------------------------------------------------

einstein:- % Nach Erstellung der Variablen wird ac3 ausgef�hrt, um Kantenkonsistenz herzustellen.
           init_variables(Variablen),
           ac3(Variablen,VariablenNew),
           
           %Konkrete Belegung des ersten Wertes (ggf. der nachfolgenden) und Suche nach der L�sung.
           fla(VariablenNew,FinalVariablen),

           %Variablen werden in fla so gesetzt, dass alle einen konkreten Wert haben, sprich eine Loesung.
           printlist(FinalVariablen).

%-----------------------------------------------------------------------------------------------------------------------------
%------------------ ac3/2
%-----------------------------------------------------------------------------------------------------------------------------
% init_q holt alle moeglichen Kanten (aus den Constraints)
% Die Variablenliste (Variablen) ist die Liste aller Eigenschaften.
% Die Variablenliste (VariablenNew) ist die Liste mit allen Eigenschaften mit nun eingeschr�nkteren Dom�nen.
ac3(Variablen,VariablenNew):- init_q(Q),
                              ac3_(Variablen,Q,VariablenNew).

%-----------------------------------------------------------------------------------------------------------------------------
%------------------ init_q/1
%-----------------------------------------------------------------------------------------------------------------------------
% Erstellt das notwendige Q fuer ac3.
% Q ist eine List die alle Kanten (Var1 -(Constraint)- Var2) enthaelt.
init_q(Q):- findall((Bez1,Bez2), constraint(variable(Bez1,2,_),variable(Bez2,3,_)), ErgList2),
            findall((Bez1,Bez2), constraint(variable(Bez1,_,_),variable(Bez2,_,_)), ErgList1),
            union(ErgList2,ErgList1,ErgList3),
            list_to_set(ErgList3,ErgSet),
            add_opposite_direction(ErgSet,Q).

%-----------------------------------------------------------------------------------------------------------------------------
%------------------ add_opposite_direction/2
%-----------------------------------------------------------------------------------------------------------------------------

% Erwartet eine Liste mit 2er Tupel: [(1,2),(a,b), (3,X), ...]
% Das Tupel wird in umgekehrter Richtung hinzugefuegt.
add_opposite_direction([],[]).
add_opposite_direction([(Var1,Var2)|RestList],[(Var1,Var2),(Var2,Var1)|RestQ]):- add_opposite_direction(RestList,RestQ).

%-----------------------------------------------------------------------------------------------------------------------------
%------------------ ac3_/3
%-----------------------------------------------------------------------------------------------------------------------------
% Ist die While-Schleife von ac3
% 1. Regel: Abbruch der Rekursion.
% 2. Regel: Falls revise etwas geaendert hat.
% 3. Regel: Falls revise nichts geaendert hat.
ac3_(UpdatedVariables,[],FinalVariablen):- check_all_different_constraint([],UpdatedVariables,UpdatedVariables,FinalVariablen).
ac3_(VariablenOld,[(BezVk,BezVm)|RestQ],VariablenNew):- get_variable(BezVk,VariablenOld,Vk),
                                                        get_variable(BezVm,VariablenOld,Vm),
                                                        revise(Vk,Vm,VkNew,Check),
                                                        not(var(Check)),!,
                                                        update(VariablenOld,VkNew,CurrentVariablenNew),
                                                        search_affected(VkNew,Vm,AffectedList),
                                                        union(AffectedList,RestQ,NewQ),
                                                        ac3_(CurrentVariablenNew,NewQ,VariablenNew).
ac3_(VariablenOld,[_|RestQ],VariablenNew):- ac3_(VariablenOld,RestQ,VariablenNew).

check_all_different_constraint(_,[],Result,Result).
check_all_different_constraint(PreVars,[variable(Bez,[Belegung],Kat)|VarRest],TmpResult,Result):- append(PreVars,[variable(Bez,[Belegung],Kat)],NewPreVars),
                                                                                             check_all_different_constraint_(variable(Bez,[Belegung],Kat),TmpResult,TmpResultNew),
                                                                                             check_all_different_constraint(NewPreVars,VarRest,TmpResultNew,Result).
check_all_different_constraint(PreVars,[Head|Rest],TmpResult,Result):- append(PreVars,[Head],NewPreVars),
                                                                       check_all_different_constraint(NewPreVars,Rest,TmpResult,Result).

check_all_different_constraint_(_,[],[]).
check_all_different_constraint_(variable(Bez1,[Belegung],Kat),[variable(Bez2,Dom,Kat)|VarRest],[variable(Bez2,NewDom,Kat)|RRest]):- Bez1 \= Bez2, subtract(Dom,[Belegung],NewDom),
                                                                                                                                   check_all_different_constraint_(variable(Bez1,[Belegung],Kat),VarRest,RRest).
check_all_different_constraint_(VkNew,[Head|Rest1],[Head|Rest2]):- check_all_different_constraint_(VkNew,Rest1,Rest2).
%-----------------------------------------------------------------------------------------------------------------------------
%------------------ get_variable/3
%-----------------------------------------------------------------------------------------------------------------------------

% Holt anhand des Bezeichners die dazugeh�rige Variable in der eingegebenen Variablenliste.
get_variable(Bez,VariablenOld,variable(Bez,Dom,Kat)):- subset([variable(Bez,Dom,Kat)],VariablenOld).

%-----------------------------------------------------------------------------------------------------------------------------
%------------------ revise/5
%-----------------------------------------------------------------------------------------------------------------------------

% Stellt lokale Konsistenz fuer Var1 her!
% true wenn min. ein Wert in der Domain von Var1 geloescht wurde!
% Ansonsten fail.
% Pr�fung: F�r alle Belegungen von Var1: Gibt es eine Belegung von Var2, sodass der Constraint erf�llt wird
% F�r jedes Tupel f�r das diese Einschr�nkung nicht gilt, wird die Belegung aus der Domain von Var1 gel�scht.
revise(variable(Bez1,[],Kat),_,variable(Bez1,[],Kat),_).
revise(variable(Bez1,[Head|Rest],Kat1),variable(Bez2,Dom2,Kat2),variable(Bez1,[Head|RestNew],Kat1),Check):- revise_check(Bez1,Head,Kat1,Bez2,Dom2,Kat2,_),!,
                                                                                                            revise(variable(Bez1,Rest,Kat1),variable(Bez2,Dom2,Kat2),variable(Bez1,RestNew,Kat1),Check).
revise(variable(Bez1,[_|Rest],Kat1),variable(Bez2,Dom2,Kat2),variable(Bez1,RestNew,Kat1),true):- revise(variable(Bez1,Rest,Kat1),variable(Bez2,Dom2,Kat2),variable(Bez1,RestNew,Kat1),true).

% Gibt es f�r die konkrete Belegung1 von Domain1 Ergebnisse in Domain2? True wenn klappt, fail wenn nicht.
revise_check(_,_,_,_,[],_,Consistent):- not(var(Consistent)).
revise_check(Bez1,Belegung1,Kat1,Bez2,[Belegung2|_],Kat2,_):- (constraint(variable(Bez1,Belegung1,Kat1),variable(Bez2,Belegung2,Kat2));
                                                                  constraint(variable(Bez2,Belegung2,Kat2),variable(Bez1,Belegung1,Kat1))).
revise_check(Bez1,Belegung1,Kat1,Bez2,[_|Rest2],Kat2,Consistent):- revise_check(Bez1,Belegung1,Kat1,Bez2,Rest2,Kat2,Consistent).


%-----------------------------------------------------------------------------------------------------------------------------
%------------------ update/4
%-----------------------------------------------------------------------------------------------------------------------------
% Sucht die Variablenbezeichnung in der VariablenList und updated die Domain.
% Immer true.
update([variable(Bez,_,Kat)|Rold],variable(Bez,DomNew,Kat),[variable(Bez,DomNew,Kat)|Rold]):- !.
update([HeadOld|RestOld],variable(BezNew,DomNew,Kat),[HeadOld|Rnew]):- update(RestOld,variable(BezNew,DomNew,Kat),Rnew).


%-----------------------------------------------------------------------------------------------------------------------------
%------------------ search_affected/3
%-----------------------------------------------------------------------------------------------------------------------------
% Sucht alle Constraints (Kanten) in denen Var an zweiter Stelle steht!
% Konkret werden Tupel mit Vi,Vk erstellt, wobei gilt: Vi != Vm und Vi ! = Vk.
% Gibt immer true zurueck, AffectedList ist ggf. leer.
search_affected(variable(Vk,_,_),variable(Vm,_,_),AffectedList):- init_q(Q),
                                                                  search_affected_(Vk,Vm,Q,AffectedList).

search_affected_(_,_,[],[]).
search_affected_(Vk,Vm,[(Vi,Vk)|QRest],[(Vi,Vk)|AffectedRest]):- Vi \= Vm, Vi \= Vk, search_affected_(Vk,Vm,QRest,AffectedRest).
search_affected_(Vk,Vm,[_|QRest],AffectedRest):- search_affected_(Vk,Vm,QRest,AffectedRest).

%-----------------------------------------------------------------------------------------------------------------------------
%------------------ fla/3
%-----------------------------------------------------------------------------------------------------------------------------
% fla uebernimmt die backtracking-geschichte und den ac3-fla aufruf.
% 1. Erste Var nehmen, 1. Wert der VarDomain setzen,
% hat ac3-fla mind. 1 Variable mit Dom>1? -> 2. Var nehmen, ac3-fla mind. 1 Variable mit Dom == 0 -> 1. Var, 2. Wert nehmen.
fla(Vars,FinalVars):- fla_([],Vars,FinalVars).

fla_(PreVars,[variable(Bez,Dom,Kat)|RestVars],FinalVars):-  member(Belegung,Dom),
                                                            append(PreVars,[variable(Bez,Belegung,Kat)|RestVars],NewVars),
                                                            ac3-fla(NewVars,variable(Bez,Belegung,Kat),CurrentResult),
                                                            check_result(CurrentResult, 2), %Gibt ResNum zurueck ( 2 = min. eine Dom>1, 0 = min. eine Dom == 0, 1 = alle Dom == 1, konkrete Belegung gefunden).
                                                            revise_all_different_check(Bez,Belegung,Kat,CurrentResult),
                                                            append(PreVars,[variable(Bez,Belegung,Kat)],NewPreVars),
                                                            subtract(CurrentResult,NewPreVars,NewRestVars),
                                                            fla_(NewPreVars,NewRestVars,FinalVars).
fla_(PreVars,[variable(Bez,Dom,Kat)|RestVars],FinalVars):-  member(Belegung,Dom),
                                                            append(PreVars,[variable(Bez,Belegung,Kat)|RestVars],NewVars),
                                                            ac3-fla(NewVars,variable(Bez,Belegung,Kat),CurrentResult),
                                                            check_result(CurrentResult, Num), %Gibt ResNum zurueck ( 2 = min. eine Dom>1, 0 = min. eine Dom == 0, 1 = alle Dom == 1, konkrete Belegung gefunden).
                                                            Num = 1,
                                                            revise_all_different_check(Bez,Belegung,Kat,CurrentResult),
                                                            FinalVars = CurrentResult.

% Check_result pr�ft die L�sung und gibt Kennzahlen zur�ck: betrachtet die L�nge der ausschlaggebenden Dom�ne.
check_result([],1). %Keine Domain mit length(Dom,0) oder length(Dom,
check_result([variable(_,Dom,_)|_],0):- is_list(Dom),
                                        length(Dom,0),!.
check_result([variable(_,Dom,_)|_],2):- is_list(Dom),
                                        length(Dom,Length),
                                        Length >= 2,!.
check_result([variable(_,[_],_)|_],2):- !.  % Ist semantisch gleich zum Fall 2. Weil er noch weiterbelegen soll!
check_result([variable(_,_,_)|RestVar],Num):- check_result(RestVar,Num). %Dom hat hier nun weder mehr als 1 Element noch 0 elemente. Dom hier also ein-Elementig oder eine Zahl -> OK.

% Pr�ft ob die Dom�ne leer ist.
check_consistent(Domvknew,0):- length(Domvknew,0),!,fail.
check_consistent(_,_).
%-----------------------------------------------------------------------------------------------------------------------------
%------------------ ac3-fla/3
%-----------------------------------------------------------------------------------------------------------------------------
% Variablen enth�lt alle unbestzten Variablen, die neu bestzte Variable und alle unbestzten Variablen
% Stellt die While-Schleife von ac3-fla dar.
ac3-fla(Variablen,variable(Bezcv,Belegungcv,Kat),ResultVars):- init_q_fla(Bezcv,Variablen,Q),
                                                               ac3-fla_(Variablen,variable(Bezcv,Belegungcv,Kat),Q,_,ResultVars).
                                                    
ac3-fla_(FinalVars,_,Q,Consistent,FinalVars):- (Q == []; Consistent == 0).
ac3-fla_(Variablen,Vcv,[(BezVk,BezVm)|RestQ],_,ResultVars):- get_variable(BezVk,Variablen,Vk),
                                                             get_variable(BezVm,Variablen,Vm),
                                                             revise_fla(Vk,Vm,Variablen,VkNew,Check),
                                                             not(var(Check)),!,
                                                             update(Variablen,VkNew,CurrentVariablenNew),
                                                             search_affected_fla(VkNew,Vm,Vcv,Variablen,AffectedList),
                                                             union(AffectedList,RestQ,NewQ),
                                                             VkNew = variable(_,Domvknew,_),
                                                             check_consistent(Domvknew,Consistent),
                                                             ac3-fla_(CurrentVariablenNew,Vcv,NewQ,Consistent,ResultVars).
ac3-fla_(Variablen,Vcv,[_|RestQ],_,ResultVars):- ac3-fla_(Variablen,Vcv,RestQ,_,ResultVars).
                                            
%-----------------------------------------------------------------------------------------------------------------------------
%------------------ init_q_fla/3
%-----------------------------------------------------------------------------------------------------------------------------
% Sucht alle Constraints (Kanten) in denen Var an zweiter Stelle steht!
% Und die Domaine der anderen Variable noch unbesetzt (= eine Liste) ist.
% Konkret werden Tupel mit Vi,Vk erstellt, wobei gilt: Vi != Vm und Vi ! = Vk und is_list(Di), (wenn Di eine List ist ist hier noch kein konrketer Wert gesetzt)
% Im Algorithmus steht: i>cv, hier umgesetzt durch i \= cv (Keine Gleichheit) und is_list(Dom), Variable ist noch unbelegt (Eine Domain).
% Gibt immer true zurueck, AffectedList ist ggf. leer.
init_q_fla(Vcv,Variablen,Qfla):- init_q(Q),
                                 init_q_fla_(Vcv,Variablen,Q,Qfla).

init_q_fla_(_,_,[],[]).
init_q_fla_(Vcv,Variablen,[(Vi,Vcv)|QRest],[(Vi,Vcv)|QflaRest]):- Vi \= Vcv,
                                                                  get_variable(Vi,Variablen,variable(Vi,Dom,_)),
                                                                  is_list(Dom),!,
                                                                  init_q_fla_(Vcv,Variablen,QRest,QflaRest).
init_q_fla_(Vcv,Variablen,[_|QRest],QflaRest):- init_q_fla_(Vcv,Variablen,QRest,QflaRest).

%-----------------------------------------------------------------------------------------------------------------------------
%------------------ search_affected_fla/3
%-----------------------------------------------------------------------------------------------------------------------------
% Sucht alle Constraints (Kanten) in denen Var an zweiter Stelle steht!
% Konkret werden Tupel mit Vi,Vk erstellt, wobei gilt: Vi != Vm und Vi ! = Vk.
% Gibt immer true zurueck, AffectedList ist ggf. leer.
search_affected_fla(variable(Vk,_,_),variable(Vm,_,_),variable(Vcv,_,_),Variablen,AffectedList):- init_q_fla(Vcv,Variablen,Q),
                                                                                                  search_affected_fla_(Vk,Vm,Vcv,Q,AffectedList).

search_affected_fla_(_,_,_,[],[]).
search_affected_fla_(Vk,Vm,Vcv,[(Vi,Vk)|QRest],[(Vi,Vk)|AffectedRest]):- Vi \= Vm, Vi \= Vk, Vi \= Vcv, search_affected_fla_(Vk,Vm,QRest,AffectedRest).
search_affected_fla_(Vk,Vm,Vcv,[_|QRest],AffectedRest):- search_affected_fla_(Vk,Vm,Vcv,QRest,AffectedRest).

%-----------------------------------------------------------------------------------------------------------------------------
%------------------ revise_fla/5
%-----------------------------------------------------------------------------------------------------------------------------

% Stellt lokale Konsistenz fuer Var1 her!
% true wenn min. ein Wert in der Domain von Var1 geloescht wurde!
% Ansonsten fail.
% Pr�fung: F�r alle Belegungen von Var1: Gibt es eine Belegung von Var2, sodass der Constraint erf�llt wird
% F�r jedes Tupel f�r das diese Einschr�nkung nicht gilt, wird die Belegung aus der Domain von Var1 gel�scht.
revise_fla(variable(Bez1,[],Kat),_,_,variable(Bez1,[],Kat),_).
revise_fla(variable(Bez1,[Head|Rest],Kat1),variable(Bez2,Dom2,Kat2),Variablen,variable(Bez1,[Head|RestNew],Kat1),Check):- (is_list(Dom2);revise_all_different_check(Bez2,Dom2,Kat2,Variablen)),
                                                                                                                          revise_all_different_check(Bez1,Head,Kat1,Variablen),
                                                                                                                          revise_check_fla(Bez1,Head,Kat1,Bez2,Dom2,Kat2,Variablen,_),!,
                                                                                                                          revise_fla(variable(Bez1,Rest,Kat1),variable(Bez2,Dom2,Kat2),Variablen,variable(Bez1,RestNew,Kat1),Check).
revise_fla(variable(Bez1,[_|Rest],Kat1),variable(Bez2,Dom2,Kat2),Variablen,variable(Bez1,RestNew,Kat1),true):- revise_fla(variable(Bez1,Rest,Kat1),variable(Bez2,Dom2,Kat2),Variablen,variable(Bez1,RestNew,Kat1),true).


% Gibt es andere von der gleichen Kategorie mit dem selben Wert?
revise_all_different_check(_,_,_,[]).
revise_all_different_check(Bez1,Belegung,Kat,[variable(Bez2,Dom,Kat)|RestVars]):- Bez1 \= Bez2,
                                                                                  not(is_list(Dom)),!,
                                                                                  Belegung \= Dom,
                                                                                  revise_all_different_check(Bez1,Belegung,Kat,RestVars).
revise_all_different_check(Bez1,Belegung,Kat,[_|RestVars]):- revise_all_different_check(Bez1,Belegung,Kat,RestVars).

% Gibt es f�r die konkrete Belegung1 von Domain1 Ergebnisse in Domain2?
revise_check_fla(_,_,_,_,[],_,_,Consistent):- not(var(Consistent)).
revise_check_fla(Bez1,Belegung1,Kat,Bez2,[Belegung2|_],Kat,_Variablen,_):- !,Belegung1 \= Belegung2, %Alle Variablen der Kategorie holen und vergleichen!!
                                                                          %revise_all_different_check(Bez1,Belegung1,Kat,Variablen),
                                                                          %revise_all_different_check(Bez2,Belegung2,Kat,Variablen),
                                                                          (constraint(variable(Bez1,Belegung1,Kat),variable(Bez2,Belegung2,Kat));
                                                                          constraint(variable(Bez2,Belegung2,Kat),variable(Bez1,Belegung1,Kat))).
revise_check_fla(Bez1,Belegung1,Kat1,Bez2,[Belegung2|_],Kat2,_Variablen,_):- %revise_all_different_check(Bez1,Belegung1,Kat1,Variablen),
                                                                            %revise_all_different_check(Bez2,Belegung2,Kat2,Variablen),
                                                                            (constraint(variable(Bez1,Belegung1,Kat1),variable(Bez2,Belegung2,Kat2));
                                                                            constraint(variable(Bez2,Belegung2,Kat2),variable(Bez1,Belegung1,Kat1))).
revise_check_fla(Bez1,Belegung1,Kat1,Bez2,[_|Rest2],Kat2,Variablen,Consistent):- revise_check_fla(Bez1,Belegung1,Kat1,Bez2,Rest2,Kat2,Variablen,Consistent).
revise_check_fla(Bez1,Belegung1,_Kat1,Bez2,Belegung2,_Kat2,_Variablen,_):- not(is_list(Belegung2)),
                                                                        (constraint(variable(Bez1,Belegung1,_),variable(Bez2,Belegung2,_));
                                                                        constraint(variable(Bez2,Belegung2,_),variable(Bez1,Belegung1,_))).
                                                                      
