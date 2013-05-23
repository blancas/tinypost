lexer grammar Scanner;

@lexer::header {
package blancas.tinypost;
}

INTEGER  : ('-')*('0'..'9')+ ;

RADIX_INTEGER
		 : ('2'..'9') '#' ('0'..'9'|'a'..'z'|'A'..'Z')+
		 | ('1'..'2') ('0'..'9') '#' ('0'..'9'|'a'..'z'|'A'..'Z')+
		 | ('3') ('0'..'6') '#' ('0'..'9'|'a'..'z'|'A'..'Z')+
		 ;

REAL
    : ('-')*('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    | ('-')*'.' ('0'..'9')+ EXPONENT?
    | ('-')*('0'..'9')+ EXPONENT
    ;

NAME 
    : ('a'..'z'
      |'A'..'Z'
      |'0'..'9'
      |'!'|'"'|'#'|'$'|'&'|'\''|'*'|','|'-'|'.'|':'|';'|'='|'?'|'@'|'^'|'_'|'`'|'|'|'~'|'+'
      )+
    | '['
    | ']'
    ;

PERCENT  : '%' ;

SLASH    : '/' ;
    
LPAREN 	 : '(' ;
    
RPAREN 	 : ')' ;

BEGIN    : '{' ;

END      : '}' ;
    
LESS     : '<' ;
    
GREATER  : '>' ;

LITERAL_NAME : SLASH NAME ;

COMMENT
    : PERCENT ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;

WS  : ( ' '
      | '\t'
      | '\r'
      | '\n'
      ) {$channel=HIDDEN;}
    ;

STRING
    :  LPAREN ( ESC_SEQ | ~('\\'|'('|')') )* RPAREN
    ;

HEX_STRING
	: LESS (HEX_DIGIT)+ GREATER
	;
	
fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\\'|'('|')')
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;
