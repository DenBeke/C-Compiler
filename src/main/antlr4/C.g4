grammar C;

prog
    : stmt
    ;
    
stmt
    : expr ';'
    ;
    
expr
    : expr '+' expr
    | expr '-' expr
    | expr '=' expr
    | ID
    | INT
    ;    

NEWLINE : [\r\n]+;
WHITESPACE : [ \t]+;
ID : [a-zA-Z]+;
INT : [0-9]+ ;