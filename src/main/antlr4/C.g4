grammar C;

file
    : (varDecl | funcDecl)*
    ;

varDecl
	: type ID ('=' expr)? ';'
	;

funcDecl
	: type ID '(' formalParameters? ')' block
	;

formalParameters
	:	formalParameter (',' formalParameter)*
	;

formalParameter
	: type ID
	;

block
	: '{' stmt* '}'
	;

expr
	: '(' expr ')'
	| ID
	| expr '/' expr
	| expr '+' expr
	| expr '-' expr
	| expr '=' expr
	| expr '==' expr
	| expr '!=' expr
	| expr '<' expr
	| expr '<=' expr
	| expr '>' expr
	| expr '>=' expr
	| literal
	;

stmt
	: block
	| varDecl
	| ID '=' expr ';'
	| 'return' expr ';'
	| 'while' '(' expr ')' block
	| 'if' '(' expr ')' block ('else' block)?
	| 'break'
	| 'continue'
	;

literal
	: INT
	| CHAR
	| STRING
	;

type
	: 'int' '*'?
	| 'char' '*'?
	| 'void' '*'?
	| 'const' type
	;

NEWLINE : [\r\n]+ -> skip;
WHITESPACE : [ \t]+ -> skip;
ID : [a-zA-Z_][a-zA-Z_0-9]*;
CHAR : '\'' (ESC|.) '\'';
STRING : '"' (ESC|.)*? '"';
ESC : '\\"' | '\\\\';
INT : [0-9]+ ;
SINGLELINECOMMENT : '//' .*? NEWLINE -> skip;
MULTILINECOMMENT : '/*' .*? '*/' -> skip;
