:- use_module(library(clpfd)).

test_ac3:- init_variables(Variablen),ac3(Variablen,VariablenNew),printlist(VariablenNew).

printlist([]):- writeln('__________________________').
printlist([X|List]) :-
        write(X),nl,
        printlist(List).