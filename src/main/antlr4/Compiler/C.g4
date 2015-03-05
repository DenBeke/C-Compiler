grammar C;

@members {
	public void handleVarDecl(String id) {};
	public void handleExpr() {};
	public void handleInt() {};
	public void handleAssign() {};
	public void handleID(String id) {};
}

file
    : (varDecl | funcDecl)*
    ;

varDecl
	: type id=ID ('=' expr)? ';' {handleVarDecl($id.text);}
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
	| id=ID {handleID($id.text);}
	| expr '/' expr
	| expr '+' expr
	| expr '-' expr
	| expr '=' expr {handleAssign();}
	| type ID ('=' expr)?
	| expr '==' expr
	| expr '!=' expr
	| expr '<' expr
	| expr '<=' expr
	| expr '>' expr
	| expr '>=' expr
	| expr '++'
	| expr '--'
	| ID '(' (expr (',' expr)*)? ')'
	| literal
	;

stmt
	: block
	| expr ';'
	| 'return' expr ';'
	| 'while' '(' expr ')' stmt
	| 'for' '(' expr? ';' expr? ';' expr? ')' stmt
	| 'if' '(' expr ')' stmt ('else' stmt)?
	| 'break' ';'
	| 'continue' ';'
	;

literal
	: INT {handleInt();}
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
