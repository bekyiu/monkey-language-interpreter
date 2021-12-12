package bekyiu.lexer

/**
 * @Date 2021/12/4 12:00 下午
 * @Created by bekyiu
 */
class Lexer(
    val input: String,
    var position: Int,
    var readPosition: Int,
    var ch: Char?
) {
    companion object {
        // keyword map to it's token type
        private val keywords = mapOf(
            "let" to TokenType.LET,
            "fn" to TokenType.FUNCTION,
            "true" to TokenType.TRUE,
            "false" to TokenType.FALSE,
            "if" to TokenType.IF,
            "else" to TokenType.ELSE,
            "return" to TokenType.RETURN
        )
    }

    constructor(input: String) : this(input, -1, 0, null) {
        readChar()
    }

    // read the next char and advance index
    private fun readChar() {
        ch = if (readPosition >= input.length) {
            // end of file
            null
        } else {
            input[readPosition]
        }
        position = readPosition
        readPosition++
    }

    private fun peekChar() =
        if (readPosition >= input.length) {
            null
        } else {
            input[readPosition]
        }

    fun nextToken(): Token {
        skipWhitespace()
        val tok = when (ch) {
            ';' -> Token(TokenType.SEMICOLON, ch.toString())
            '(' -> Token(TokenType.LPAREN, ch.toString())
            ')' -> Token(TokenType.RPAREN, ch.toString())
            ',' -> Token(TokenType.COMMA, ch.toString())
            '+' -> Token(TokenType.PLUS, ch.toString())
            '{' -> Token(TokenType.LBRACE, ch.toString())
            '}' -> Token(TokenType.RBRACE, ch.toString())
            '-' -> Token(TokenType.MINUS, ch.toString())
            '*' -> Token(TokenType.ASTERISK, ch.toString())
            '/' -> Token(TokenType.SLASH, ch.toString())
            '<' -> Token(TokenType.LT, ch.toString())
            '>' -> Token(TokenType.GT, ch.toString())

            '=' -> {
                if (peekChar() == '=') {
                    readChar()
                    Token(TokenType.EQ, "==")
                } else {
                    Token(TokenType.ASSIGN, ch.toString())
                }
            }
            '!' -> {
                if (peekChar() == '=') {
                    readChar()
                    Token(TokenType.NOT_EQ, "!=")
                } else {
                    Token(TokenType.BANG, ch.toString())
                }
            }
            else -> {
                when {
                    ch == null -> {
                        Token(TokenType.EOF, "")
                    }
                    ch!!.isJavaIdentifierStart() -> {
                        val ident = readIdentifier()
                        return Token(identType(ident), ident)
                    }
                    ch!!.isDigit() -> {
                        return Token(TokenType.INT, readNumber())
                    }
                    else -> {
                        Token(TokenType.ILLEGAL, ch.toString())
                    }
                }
            }
        }
        readChar()
        return tok
    }

    private fun skipWhitespace() {
        while (ch?.isWhitespace() == true) {
            readChar()
        }
    }

    // for convenient, use java identifier rule
    private fun readIdentifier(): String {
        var identifier = ""
        while (ch?.isJavaIdentifierStart() == true) {
            identifier += ch
            readChar()
        }
        return identifier
    }

    private fun readNumber(): String {
        var number = ""
        while (ch?.isDigit() == true) {
            number += ch
            readChar()
        }
        return number
    }

    private fun identType(ident: String): TokenType {
        val type = keywords[ident]
        return type ?: TokenType.IDENT
    }


}