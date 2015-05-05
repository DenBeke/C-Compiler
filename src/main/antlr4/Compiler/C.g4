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
	public void handleChar(String n) {};
	public void handleString(String n) {};
	public void handleAssign() {};
	public void handleID(String id) {};
	public void handleFile() {};
	public void handleBinaryOperator(String operator) {};
	public void startScope() {};
	public void endScope() {};
	public void handleUnaryOperator(String operator) {};
	public void handleNothing() {};
	public void handleForStatement() {};
	public void handleReturnStatement() {};
	public void handleWhileStatement(){};
	public void handleIfStatement(){};
	public void handleBreakStatement(){};
	public void handleContinueStatement(){};
	public void handleParam(){};
	public void handleFunctionCall(String n) {};
	public void handleType(String t, String c) {};
	public void handlePointer() {};
	public void handleConst() {};
	public void handleStaticArray(String n) {};
	public void handleTypeCast() {};
	public void handleIncludeIO() {};
	public void handleReference() {};
	public void handleDereference() {};
	public void startParams() {};
}

start
	: file {handleFile();}
	;

file
    : (varDecl ';' | funcDecl)*
    ;

varDecl
	: type id=ID static_array? (('=' expr) | nothing) {handleVarDecl($id.text);}
	;

static_array
	: '[' n=INT ']' {handleStaticArray($n.text);}
	;

funcDecl
	: type id=ID '(' formalParameters? ')' block {handleFuncDecl($id.text);}
	| '#include <stdio.h>' {handleIncludeIO();}
	;

formalParameters
	:	formalParameter (',' formalParameter)* {handleFormalParameters();}
	;

formalParameter
	: type (id=ID)? {handleFormalParameter($id.text);}
	;

block
	: {startScope();} '{' stmt* '}' {endScope(); handleBlock();}
	;

expr
	: '(' type ')' expr {handleTypeCast();}
	| '(' expr ')'
	| '&' expr {handleReference();}
	| '*' expr {handleDereference();}
	| id=ID {handleID($id.text);}
	| expr '/' expr {handleBinaryOperator("/");}
	| expr '*' expr {handleBinaryOperator("*");}
	| expr '+' expr {handleBinaryOperator("+");}
	| expr '-' expr {handleBinaryOperator("-");}
	| expr '=' expr {handleBinaryOperator("=");}
	| varDecl
	| expr '==' expr {handleBinaryOperator("==");}
	| expr '!=' expr {handleBinaryOperator("!=");}
	| expr '<' expr {handleBinaryOperator("<");}
	| expr '<=' expr {handleBinaryOperator(">=");}
	| expr '>' expr {handleBinaryOperator(">");}
	| expr '>=' expr {handleBinaryOperator(">=");}
	| expr '++' {handleUnaryOperator("++");}
	| expr '--' {handleUnaryOperator("--");}
	| id=ID '(' {startParams();} (param (',' param)*)? ')' {handleFunctionCall($id.text);}
	| literal
	;

param : expr {handleParam();};

stmt
	: block
	| expr ';' {handleExprStatement();}
	| funcDecl
	| 'return' (expr|nothing) ';' {handleReturnStatement();}
	| 'while' '(' expr ')' stmt {handleWhileStatement();}
	| 'for' '(' (expr|nothing) ';' (expr|nothing) ';' (expr|nothing) ')' stmt {handleForStatement();}
	| 'if' '(' expr ')' stmt (('else' stmt)|(nothing)) {handleIfStatement();}
	| 'break' ';' {handleBreakStatement();}
	| 'continue' ';' {handleContinueStatement();}
	;
	
nothing
	: {handleNothing();}
	;

literal
	: i=INT {handleInt($i.text);}
	| c=CHAR {handleChar($c.text);}
	| s=STRING {handleString($s.text);}
	;

type
    //: ('const'|nothing) raw_type (const_type|pointer|nothing)
    : c='const'? t=raw_type (const_type|pointer)? {handleType($t.text, $c.text);}
    ;

raw_type
    : 'int'
    | 'char'
    | 'void'
    ;


pointer : '*' (const_type|pointer|nothing) {handlePointer();};

const_type : 'const' (pointer|nothing) {handleConst();};

/*
type
	: 'int' '*'?
	| 'char' '*'?
	| 'void' '*'?
	| 'const' type
	;
*/

NEWLINE : [\r\n]+ -> skip;
WHITESPACE : [ \t]+ -> skip;
ID : [a-zA-Z_][a-zA-Z_0-9]*;
CHAR : '\'' (ESC|.) '\'';
STRING : '"' (ESC|.)*? '"';
ESC : '\\"' | '\\\\' | '\\n' | '\\t';
INT : [0-9]+ ;
SINGLELINECOMMENT : '//' .*? NEWLINE -> skip;
MULTILINECOMMENT : '/*' .*? '*/' -> skip;
