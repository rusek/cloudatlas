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
%cupsym Sym
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
        { return this.token(Sym.KEYWORD_SELECT); }
    "AS"                           
        { return this.token(Sym.KEYWORD_AS); }
    "WHERE"
    	{ return this.token(Sym.KEYWORD_WHERE); }
   	"ORDER"
   		{ return this.token(Sym.KEYWORD_ORDER); }
        "BY"
                { return this.token(Sym.KEYWORD_BY); }
   	"REGEXP"
   		{ return this.token(Sym.KEYWORD_REGEXP); }
   	"ASC"
   		{ return this.token(Sym.KEYWORD_ASC); }
   	"DESC"
   		{ return this.token(Sym.KEYWORD_DESC); }
   	"FIRST"
   		{ return this.token(Sym.KEYWORD_FIRST); }
   	"LAST"
   		{ return this.token(Sym.KEYWORD_LAST); }
   	"NULLS"
   		{ return this.token(Sym.KEYWORD_NULLS); }
   	"AND"
   		{ return this.token(Sym.KEYWORD_AND); }
   	"OR"
   		{ return this.token(Sym.KEYWORD_OR); }
   	"NOT"
                { return this.token(Sym.KEYWORD_NOT); }
    /* identifiers */ 
    {Identifier}                   
        { return this.token(Sym.IDENTIFIER, this.yytext()); }
    "+"                            
        { return this.token(Sym.PLUS); }
    "-"                            
        { return this.token(Sym.MINUS); }
    "*"                            
        { return this.token(Sym.STAR); }
    "/"
    	{ return this.token(Sym.SLASH); }
    "%"
    	{ return this.token(Sym.PERCENT); }
    ";"                            
        { return this.token(Sym.SEMICOLON); }
    "("                            
        { return this.token(Sym.LEFT_PARENTHESIS); }
    ")"                            
        { return this.token(Sym.RIGHT_PARENTHESIS); }
    "["
    	{ return this.token(Sym.LEFT_BRACKET); }
    "]"
    	{ return this.token(Sym.RIGHT_BRACKET); }
    "{"
    	{ return this.token(Sym.LEFT_BRACE); }
    "}"
    	{ return this.token(Sym.RIGHT_BRACE); }
    "="                            
        { return this.token(Sym.EQUAL); }
    "<>"                           
        { return this.token(Sym.NOT_EQUAL); }
    "<"
    	{ return this.token(Sym.LESS); }
    "<="
    	{ return this.token(Sym.LESS_EQUAL); }
    ">"
    	{ return this.token(Sym.GREATER); }
    ">="
    	{ return this.token(Sym.GREATER_EQUAL); }
    ","
    	{ return this.token(Sym.COMMA); }
    /* constants */
    {DecIntegerLiteral}            
        { return this.token(
            Sym.INTEGER_LITERAL, new Long(this.yytext())); }
    \"                             
        { this.string.setLength(0); this.yybegin(STRING); }
    /* whitespace */
    {WhiteSpace}                   
        { /* ignore */ }
    <<EOF>>                        
        { return this.token(Sym.EOF); }
}

<STRING> {
    /* end of string */
    \"                             
        { this.yybegin(YYINITIAL); return this.token(
            Sym.STRING_LITERAL, this.string.toString()); }
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
    \\\\                             
        { this.string.append('\\'); }
    <<EOF>>                        
        { throw new Error("Unterminated string at the end of input"); }
}

/* error fallback */
.|\n                               
    { throw new Error("Illegal character <" + this.yytext() + ">"); }
