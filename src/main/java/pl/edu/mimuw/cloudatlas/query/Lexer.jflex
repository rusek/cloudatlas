package pl.edu.mimuw.cloudatlas.query;
/* A scanner for the **CloudAtlas** query language. */
import java_cup.runtime.*;

/**
 * This class is an auto-generated scanner for
 * the **CloudAtlas** query language.
 *
 * @author Jan Popieluch
 */
%%

/* scanner specs */
%class Lexer
%unicode
%cup
%line
%column

/* Java code pasted into the scanner */
%{
    StringBuffer string = new StringBuffer();

    private Symbol token(int type) {
        return new Symbol(type, this.yyline, this.yycolumn);
    }
    private Symbol token(int type, Object value) {
        return new Symbol(type, this.yyline, this.yycolumn, value);
    }
%}

/* macros */
LineTerminator     = \r|\n|\r\n|\n\r
WhiteSpace         = {LineTerminator} | [ \t\f]
Identifier         = [a-zA-Z][a-zA-Z0-9_]*
DecIntegerLiteral  = 0 | [1-9][0-9]*

/* additional high-level states */
%state STRING

%%

<YYINITIAL> {
    /* keywords */
    "SELECT"                       
        { return this.token(sym.KEYWORD_SELECT); }
    "AS"                           
        { return this.token(sym.KEYWORD_AS); }
    "WHERE"
    	{ return this.token(sym.KEYWORD_WHERE); }
   	"ORDER BY"
   		{ return this.token(sym.KEYWORD_ORDER_BY); }
   	"REGEXP"
   		{ return this.token(sym.KEYWORD_REGEXP); }
   	"ASC"
   		{ return this.token(sym.KEYWORD_ASC); }
   	"DESC"
   		{ return this.token(sym.KEYWORD_DESC); }
   	"FIRST"
   		{ return this.token(sym.KEYWORD_FIRST); }
   	"LAST"
   		{ return this.token(sym.KEYWORD_LAST); }
   	"NULLS"
   		{ return this.token(sym.KEYWORD_NULLS); }
   	"AND"
   		{ return this.token(sym.KEYWORD_AND); }
   	"OR"
   		{ return this.token(sym.KEYWORD_OR); }
    /* identifiers */ 
    {Identifier}                   
        { return this.token(sym.IDENTIFIER, this.yytext()); }
    "+"                            
        { return this.token(sym.PLUS); }
    "-"                            
        { return this.token(sym.MINUS); }
    "*"                            
        { return this.token(sym.STAR); }
    "/"
    	{ return this.token(sym.SLASH); }
    "%"
    	{ return this.token(sym.PERCENT); }
    ";"                            
        { return this.token(sym.SEMI); }
    "("                            
        { return this.token(sym.LEFT_PARENTHESIS); }
    ")"                            
        { return this.token(sym.RIGHT_PARENTHESIS); }
    "["
    	{ return this.token(sym.LEFT_SQUARE); }
    "]"
    	{ return this.token(sym.RIGHT_SQUARE); }
    "{"
    	{ return this.token(sym.LEFT_BRACE); }
    "}"
    	{ return this.token(sym.RIGHT_BRACE); }
    "="                            
        { return this.token(sym.EQ); }
    "<>"                           
        { return this.token(sym.NOT_EQ); }
    "<"
    	{ return this.token(sym.LESS); }
    "<="
    	{ return this.token(sym.LESSEQ); }
    ">"
    	{ return this.token(sym.GREATER); }
    ">="
    	{ return this.token(sym.GREATEREQ); }
    ","
    	{ return this.token(sym.COMA); }
    /* constants */
    {DecIntegerLiteral}            
        { return this.token(
            sym.INTEGER_LITERAL, new Integer(this.yytext())); }
    \"                             
        { this.string.setLength(0); this.yybegin(STRING); }
    /* whitespace */
    {WhiteSpace}                   
        { /* ignore */ }
    <<EOF>>                        
        { return this.token(sym.EOF); }
}

<STRING> {
    /* end of string */
    \"                             
        { this.yybegin(YYINITIAL); return this.token(
            sym.STRING_LITERAL, this.string.toString()); }
    /* regular characters */
    [^\n\r\"\\]+                   
        { this.string.append(this.yytext()); }
    /* escaped characters */
    \\t                            
        { this.string.append('\t'); }
    \\n                            
        { this.string.append('\n'); }
    \\r                            
        { this.string.append('\r'); }
    \\\"                           
        { this.string.append('\"'); }
    \\                             
        { this.string.append('\\'); }
    <<EOF>>                        
        { throw new Error("Unterminated string at the end of input"); }
}

/* error fallback */
.|\n                               
    { throw new Error("Illegal character <" + this.yytext() + ">"); }
