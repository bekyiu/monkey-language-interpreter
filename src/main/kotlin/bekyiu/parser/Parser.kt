package bekyiu.parser

import bekyiu.ast.*
import bekyiu.lexer.Lexer
import bekyiu.lexer.Token
import bekyiu.lexer.TokenType

/**
 * @Date 2021/12/4 1:39 下午
 * @Created by bekyiu
 */

/*
 prefixParseFun gets called when we encounter the associated token type in prefix position
 infixParseFun gets called when we encounter the associated token type in infix position

 start with curToken being the type of token you’re associated with
 return with curToken being the last token that’s part of your expression type
 won't advance the tokens too far
 */
typealias prefixParseFun = () -> Expression
typealias infixParseFun = (Expression) -> Expression

class Parser(
    val lexer: Lexer
) {
    var curToken: Token
    var peekToken: Token
    var prefixParseFuns: MutableMap<TokenType, prefixParseFun>
    var infixParseFuns: MutableMap<TokenType, infixParseFun>

    init {
        curToken = lexer.nextToken()
        peekToken = lexer.nextToken()
        prefixParseFuns = mutableMapOf()
        infixParseFuns = mutableMapOf()

        registerPrefix(TokenType.IDENT, ::parseIdentifier)
        registerPrefix(TokenType.INT, ::parseIntegerLiteral)
    }

    fun parseProgram(): Program {
        val program = Program(mutableListOf())
        while (curToken.type != TokenType.EOF) {
            parseStatement().let {
                program.statements.add(it)
            }
            nextToken()
        }
        return program
    }

    private fun registerPrefix(type: TokenType, fn: prefixParseFun) {
        prefixParseFuns[type] = fn
    }

    private fun registerInfix(type: TokenType, fn: infixParseFun) {
        infixParseFuns[type] = fn
    }

    private fun nextToken() {
        curToken = peekToken
        peekToken = lexer.nextToken()
    }

    private fun parseStatement(): Statement {
        return when (curToken.type) {
            TokenType.LET -> parseLetStatement()
            TokenType.RETURN -> parseReturnStatement()
            else -> parseExpressionStatement()
        }
    }

    private fun parseExpressionStatement(): Statement {
        val stmt = ExpressionStatement(curToken, null)
        stmt.expression = parseExpression(Precedence.LOWEST)
        // optional semicolon make it easier to type in repl
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }
        return stmt
    }

    private fun parseExpression(precedence: Precedence): Expression? {
        val prefixFn = prefixParseFuns[curToken.type]
        return prefixFn?.let {
            val leftExp = it()
            leftExp
        }
    }

    private fun parseIdentifier(): Expression {
        return Identifier(curToken, curToken.literal)
    }

    private fun parseIntegerLiteral(): Expression {
        return IntegerLiteral(curToken, curToken.literal.toLong())
    }

    // return <expression>;
    private fun parseReturnStatement(): Statement {
        // RETURN
        val token = curToken
        nextToken()
        // todo skipping expression
        while (!curTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }
        return ReturnStatement(token, null)
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

