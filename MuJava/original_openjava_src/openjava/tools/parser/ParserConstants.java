/* Generated By:JavaCC: Do not edit this line. ParserConstants.java */
package openjava.tools.parser;

public interface ParserConstants {

  int EOF = 0;
  int SINGLE_LINE_COMMENT = 9;
  int FORMAL_COMMENT = 10;
  int MULTI_LINE_COMMENT = 11;
  int ABSTRACT = 13;
  int BOOLEAN = 14;
  int BREAK = 15;
  int BYTE = 16;
  int CASE = 17;
  int CATCH = 18;
  int CHAR = 19;
  int CLASS = 20;
  int CONST = 21;
  int CONTINUE = 22;
  int _DEFAULT = 23;
  int DO = 24;
  int DOUBLE = 25;
  int ELSE = 26;
  int EXTENDS = 27;
  int FALSE = 28;
  int FINAL = 29;
  int FINALLY = 30;
  int FLOAT = 31;
  int FOR = 32;
  int GOTO = 33;
  int IF = 34;
  int IMPLEMENTS = 35;
  int IMPORT = 36;
  int INSTANCEOF = 37;
  int INT = 38;
  int INTERFACE = 39;
  int LONG = 40;
  int NATIVE = 41;
  int NEW = 42;
  int NULL = 43;
  int PACKAGE = 44;
  int PRIVATE = 45;
  int PROTECTED = 46;
  int PUBLIC = 47;
  int RETURN = 48;
  int SHORT = 49;
  int STATIC = 50;
  int SUPER = 51;
  int SWITCH = 52;
  int SYNCHRONIZED = 53;
  int THIS = 54;
  int THROW = 55;
  int THROWS = 56;
  int TRANSIENT = 57;
  int TRUE = 58;
  int TRY = 59;
  int VOID = 60;
  int VOLATILE = 61;
  int WHILE = 62;
  int METACLASS = 63;
  int INSTANTIATES = 64;
  int INTEGER_LITERAL = 65;
  int LONG_LITERAL = 66;
  int DECIMAL_LITERAL = 67;
  int HEX_LITERAL = 68;
  int OCTAL_LITERAL = 69;
  int DOUBLE_FLOATING_POINT_LITERAL = 70;
  int FLOATING_POINT_LITERAL = 71;
  int EXPONENT = 72;
  int CHARACTER_LITERAL = 73;
  int STRING_LITERAL = 74;
  int IDENTIFIER = 75;
  int LETTER = 76;
  int DIGIT = 77;
  int LPAREN = 78;
  int RPAREN = 79;
  int LBRACE = 80;
  int RBRACE = 81;
  int LBRACKET = 82;
  int RBRACKET = 83;
  int SEMICOLON = 84;
  int COMMA = 85;
  int DOT = 86;
  int ASSIGN = 87;
  int GT = 88;
  int LT = 89;
  int BANG = 90;
  int TILDE = 91;
  int HOOK = 92;
  int COLON = 93;
  int EQ = 94;
  int LE = 95;
  int GE = 96;
  int NE = 97;
  int SC_OR = 98;
  int SC_AND = 99;
  int INCR = 100;
  int DECR = 101;
  int PLUS = 102;
  int MINUS = 103;
  int STAR = 104;
  int SLASH = 105;
  int BIT_AND = 106;
  int BIT_OR = 107;
  int XOR = 108;
  int REM = 109;
  int LSHIFT = 110;
  int RSIGNEDSHIFT = 111;
  int RUNSIGNEDSHIFT = 112;
  int PLUSASSIGN = 113;
  int MINUSASSIGN = 114;
  int STARASSIGN = 115;
  int SLASHASSIGN = 116;
  int ANDASSIGN = 117;
  int ORASSIGN = 118;
  int XORASSIGN = 119;
  int REMASSIGN = 120;
  int LSHIFTASSIGN = 121;
  int RSIGNEDSHIFTASSIGN = 122;
  int RUNSIGNEDSHIFTASSIGN = 123;
  
  int AT = 85;

  int DEFAULT = 0;
  int IN_SINGLE_LINE_COMMENT = 1;
  int IN_FORMAL_COMMENT = 2;
  int IN_MULTI_LINE_COMMENT = 3;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<token of kind 6>",
    "\"//\"",
    "\"/*\"",
    "<SINGLE_LINE_COMMENT>",
    "\"*/\"",
    "\"*/\"",
    "<token of kind 12>",
    "\"abstract\"",
    "\"boolean\"",
    "\"break\"",
    "\"byte\"",
    "\"case\"",
    "\"catch\"",
    "\"char\"",
    "\"class\"",
    "\"const\"",
    "\"continue\"",
    "\"default\"",
    "\"do\"",
    "\"double\"",
    "\"else\"",
    "\"extends\"",
    "\"false\"",
    "\"final\"",
    "\"finally\"",
    "\"float\"",
    "\"for\"",
    "\"goto\"",
    "\"if\"",
    "\"implements\"",
    "\"import\"",
    "\"instanceof\"",
    "\"int\"",
    "\"interface\"",
    "\"long\"",
    "\"native\"",
    "\"new\"",
    "\"null\"",
    "\"package\"",
    "\"private\"",
    "\"protected\"",
    "\"public\"",
    "\"return\"",
    "\"short\"",
    "\"static\"",
    "\"super\"",
    "\"switch\"",
    "\"synchronized\"",
    "\"this\"",
    "\"throw\"",
    "\"throws\"",
    "\"transient\"",
    "\"true\"",
    "\"try\"",
    "\"void\"",
    "\"volatile\"",
    "\"while\"",
    "\"metaclass\"",
    "\"instantiates\"",
    "<INTEGER_LITERAL>",
    "<LONG_LITERAL>",
    "<DECIMAL_LITERAL>",
    "<HEX_LITERAL>",
    "<OCTAL_LITERAL>",
    "<DOUBLE_FLOATING_POINT_LITERAL>",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<CHARACTER_LITERAL>",
    "<STRING_LITERAL>",
    "<IDENTIFIER>",
    "<LETTER>",
    "<DIGIT>",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\";\"",
    "\",\"",
    "\".\"",
    "\"=\"",
    "\">\"",
    "\"<\"",
    "\"!\"",
    "\"~\"",
    "\"?\"",
    "\":\"",
    "\"==\"",
    "\"<=\"",
    "\">=\"",
    "\"!=\"",
    "\"||\"",
    "\"&&\"",
    "\"++\"",
    "\"--\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"&\"",
    "\"|\"",
    "\"^\"",
    "\"%\"",
    "\"<<\"",
    "\">>\"",
    "\">>>\"",
    "\"+=\"",
    "\"-=\"",
    "\"*=\"",
    "\"/=\"",
    "\"&=\"",
    "\"|=\"",
    "\"^=\"",
    "\"%=\"",
    "\"<<=\"",
    "\">>=\"",
    "\">>>=\"",
  };

}
