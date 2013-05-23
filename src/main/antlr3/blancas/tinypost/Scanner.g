// Copyright (c) 2013 Armando Blancas. All rights reserved.
// The use and distribution terms for this software are covered by the
// Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
// which can be found in the file epl-v10.html at the root of this distribution.
// By using this software in any fashion, you are agreeing to be bound by
// the terms of this license.
// You must not remove this notice, or any other, from this software.

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
