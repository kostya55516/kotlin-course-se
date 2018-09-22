grammar Exp;

file: body=block EOF;

block: (statement)*;

statement: function | variable | expression | whileLoop | ifStatement | assigment | returnStatement;

function: 'fun' name=IDENTIFIER '(' params=parameterNames ')' '{'  body=block '}';

variable: 'var' name=IDENTIFIER ('=' exp=expression)?;

parameterNames: (IDENTIFIER (',' IDENTIFIER)*)?;

whileLoop: 'while' '(' cond=expression ')' '{'  body=block '}';

ifStatement: 'if' '(' cond=expression ')' '{'  ifBody=block '}' ('else' '{'  elseBody=block '}')?;

assigment: name=IDENTIFIER '=' value=expression;

returnStatement: 'return' value=expression;

expression
    : functionCall
    | identifier
    | literal
    | bracedExpression
    | <assoc=left> left=expression (op=MUL | op=DIV | op=MOD) right=expression
    | <assoc=left> left=expression (op=PLUS | op=MINUS) right=expression
    | left=expression (op=LE | op=GR | op=GRQ | op=LEQ) right=expression
    | left=expression ( op=EQ | op=NEQ) right=expression
    | left=expression op=AND right=expression
    | left=expression op=OR right=expression
    ;

identifier: IDENTIFIER;

literal: LITERAL;

bracedExpression: '(' exp=expression ')';

functionCall: name=IDENTIFIER '(' args=arguments ')';

arguments: (expression (',' expression))?;


PLUS: '+';
MUL: '*';
MINUS: '-';
DIV: '/';
MOD: '%';
AND: '&&';
OR: '||';
LE: '<';
GR: '>';
LEQ: '<=';
GRQ: '>=';
EQ: '==';
NEQ: '!=';

IDENTIFIER: [a-zA-Z][a-zA-Z0-9_]*;

LITERAL:    [1-9][0-9]? | '0';

ENDLINE:  ('\r')? '\n' | '\r';

WS : (' ' | '\t' | ENDLINE | COMMENTS) -> skip;

COMMENTS: '//' .*? ENDLINE?;
//TODO eof comment