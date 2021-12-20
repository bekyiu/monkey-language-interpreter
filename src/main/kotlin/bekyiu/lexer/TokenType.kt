package bekyiu.lexer

/**
 * @Date 2021/12/4 12:01 下午
 * @Created by bekyiu
 */
enum class TokenType(val literal: String) {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    // Identifiers + literals
    IDENT("IDENT"), // add, foobar, x, y, ...
    INT("INT"), // 1343456
    STRING("STRING"), // "nanase"

    // Operators
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    BANG("!"),
    ASTERISK("*"),
    SLASH("/"),
    LT("<"),
    GT(">"),
    EQ("=="),
    NOT_EQ("!="),

    // Delimiters
    COMMA(","),
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    // Keywords
    FUNCTION("FUNCTION"),
    LET("LET"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    IF("IF"),
    ELSE("ELSE"),
    RETURN("RETURN"),
}
