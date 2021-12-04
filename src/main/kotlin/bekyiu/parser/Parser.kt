package bekyiu.parser

import bekyiu.ast.*
import bekyiu.lexer.Lexer
import bekyiu.lexer.Token
import bekyiu.lexer.TokenType

/**
 * @Date 2021/12/4 1:39 下午
 * @Created by bekyiu
 */
class Parser(
    val lexer: Lexer
) {
    var curToken: Token
    var peekToken: Token

    init {
        curToken = lexer.nextToken()
        peekToken = lexer.nextToken()
    }

    private fun nextToken() {
        curToken = peekToken
        peekToken = lexer.nextToken()
    }

    fun parseProgram(): Program {
        val program = Program(mutableListOf())
        while (curToken.type != TokenType.EOF) {
            parseStatement()?.let {
                program.statements.add(it)
            }
            nextToken()
        }
        return program
    }

    private fun parseStatement(): Statement? {
        return when (curToken.type) {
            TokenType.LET -> parseLetStatement()
            else -> null
        }
    }

    // let <identifier> = <expression>;
    private fun parseLetStatement(): LetStatement {
        // LET
        val token = curToken
        expectPeek(TokenType.IDENT)
        val ident = Identifier(curToken, curToken.literal)
        expectPeek(TokenType.ASSIGN)
        // todo skipping expression
        while (!curTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }
        return LetStatement(token, ident, null)
    }

    private fun expectPeek(type: TokenType) {
        if (peekTokenIs(type)) {
            nextToken()
            return
        }
        throw ParseException(type, peekToken)
    }

    private fun peekTokenIs(type: TokenType): Boolean {
        return peekToken.type == type
    }

    private fun curTokenIs(type: TokenType): Boolean {
        return curToken.type == type
    }
}

class ParseException(expectedType: String, actualType: String, actualValue: String) :
    RuntimeException("parse error, expected: $expectedType, actually: $actualType($actualValue)") {

    constructor(expected: TokenType, actual: Token) :
            this(expected.literal, actual.type.literal, actual.literal)
}
