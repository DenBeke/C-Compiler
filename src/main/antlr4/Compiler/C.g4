grammar C;

@members {
	public void handleVarDecl(String id) {};
	public void handleFuncDecl(String id) {};
	public void handleFormalParameters() {};
	public void handleFormalParameter(String id) {};
	public void handleBlock() {};
	public void handleExpr() {};
	public void handleExprStatement() {};
	public void handleInt(String n) {};
	public void handleAssign() {};
	public void handleID(String id) {};
	public void handleFile() {};
	public void handleBinaryOperator(String operator) {};
	public void startScope() {};
	public void endScope() {};
	public void handleUnaryOperator(String operator) {};
	
}

start
	: file {handleFile();}
	;

file
    : (varDecl | funcDecl)*
    ;

varDecl
	: type id=ID ('=' expr)? ';' {handleVarDecl($id.text);}
	;

funcDecl
	: type id=ID '(' formalParameters? ')' block {handleFuncDecl($id.text);}
	;

formalParameters
	:	formalParameter (',' formalParameter)* {handleFormalParameters();}
	;

formalParameter
	: type id=ID {handleFormalParameter($id.text);}
	;

block
	: {startScope();} '{' stmt* '}' {endScope(); handleBlock();}
	;

expr
	: '(' expr ')'
	| id=ID {handleID($id.text);}
	| expr '/' expr {handleBinaryOperator("/");}
	| expr '+' expr {handleBinaryOperator("+");}
	| expr '-' expr {handleBinaryOperator("-");}
	| expr '=' expr {handleBinaryOperator("=");}
	| type id=ID ('=' expr)? {handleVarDecl($id.text);}
	| expr '==' expr {handleBinaryOperator("==");}
	| expr '!=' expr {handleBinaryOperator("!=");}
	| expr '<' expr {handleBinaryOperator("<");}
	| expr '<=' expr {handleBinaryOperator(">=");}
	| expr '>' expr {handleBinaryOperator(">");}
	| expr '>=' expr {handleBinaryOperator(">=");}
	| expr '++' {handleUnaryOperator("++");}
	| expr '--' {handleUnaryOperator("--");}
	| ID '(' (expr (',' expr)*)? ')'
	| literal
	;

stmt
	: block
	| expr ';' {handleExprStatement();}
	| 'return' expr ';'
	| 'while' '(' expr ')' stmt
	| 'for' '(' expr? ';' expr? ';' expr? ')' stmt
	| 'if' '(' expr ')' stmt ('else' stmt)?
	| 'break' ';'
	| 'continue' ';'
	;

literal
	: i=INT {handleInt($i.text);}
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
