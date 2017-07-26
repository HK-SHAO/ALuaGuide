package com.sf.LuaEditor;

/**
 * Created by zy on 2015/8/26.
 */
public enum LuaTokenTypes {
    WRONG,
    NL_BEFORE_LONGSTRING,
    WS,
    NEWLINE,
    SHEBANG,
    LONGCOMMENT,
    SHORTCOMMENT,
    LUADOC_COMMENT,
    LONGCOMMENT_BEGIN,
    LONGCOMMENT_END,
    NAME,
    NUMBER,
    STRING,
    LONGSTRING,
    LONGSTRING_BEGIN,
    LONGSTRING_END,
    UNTERMINATED_STRING,
    DIV,
    MULT,
    LPAREN,
    RPAREN,
    LBRACK,
    RBRACK,
    LCURLY,
    RCURLY,
    COLON,
    COMMA,
    DOT,
    ASSIGN,
    SEMI,
    EQ,
    NE,
    PLUS,
    MINUS,
    GE,
    GT,
    EXP,
    LE,
    LT,
    ELLIPSIS,
    CONCAT,
    GETN,
    MOD,

    IF,
    ELSE,
    ELSEIF,
    WHILE,
    WITH,
    THEN,
    FOR,
    IN,
    RETURN,
    BREAK,
    CONTINUE,
    TRUE,
    FALSE,
    NIL,
    FUNCTION,
    DO,
    NOT,
    AND,
    OR,
    LOCAL,
    REPEAT,
    UNTIL,
    END
}
