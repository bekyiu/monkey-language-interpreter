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
        registerPrefix(TokenType.BANG, ::parsePrefixExpression)
        registerPrefix(TokenType.MINUS, ::parsePrefixExpression)

        registerInfix(TokenType.PLUS, ::parseInfixExpression)
        registerInfix(TokenType.MINUS, ::parseInfixExpression)
        registerInfix(TokenType.SLASH, ::parseInfixExpression)
        registerInfix(TokenType.ASTERISK, ::parseInfixExpression)
        registerInfix(TokenType.EQ, ::parseInfixExpression)
        registerInfix(TokenType.NOT_EQ, ::parseInfixExpression)
        registerInfix(TokenType.LT, ::parseInfixExpression)
        registerInfix(TokenType.GT, ::parseInfixExpression)
    }

    companion object {
        // this map associates token types with their precedence
        val precedences = mapOf(
            TokenType.EQ to Precedence.EQUALS,
            TokenType.NOT_EQ to Precedence.EQUALS,
            TokenType.LT to Precedence.LESS_GREATER,
            TokenType.GT to Precedence.LESS_GREATER,
            TokenType.PLUS to Precedence.SUM,
            TokenType.MINUS to Precedence.SUM,
            TokenType.SLASH to Precedence.PRODUCT,
            TokenType.ASTERISK to Precedence.PRODUCT,
        )
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

    private fun parseExpression(precedence: Precedence): Expression {
        val prefixFn = prefixParseFuns[curToken.type]
        var leftExp = prefixFn?.let {
            val leftExp = it()
            leftExp
        } ?: throw ParseException("no prefix parse function ${curToken.type.literal}(${curToken.literal}) found")

        // if peek token has higher precedence
        // we need to make it deeper in ast
        // it means current leftExp needs to precedent combine with the peek token
        // otherwise the current leftExp needs to combine with the last token
        // e.g: 1 + 2 * 3, '*' has higher precedence than '+'
        // so '2' needs to precedent combine with '*' but not '+'
        while (!peekTokenIs(TokenType.SEMICOLON) && precedence.lt(peekPrecedence())) {
            val infixFn = infixParseFuns[peekToken.type] ?: return leftExp
            nextToken()
            leftExp = infixFn(leftExp)
        }
        return leftExp
    }

    // <identifier>;
    private fun parseIdentifier(): Expression {
        return Identifier(curToken, curToken.literal)
    }

    // <integer literal>;
    private fun parseIntegerLiteral(): Expression {
        return IntegerLiteral(curToken, curToken.literal.toLong())
    }

    // <prefix operator><expression>;
    private fun parsePrefixExpression(): Expression {
        val token = curToken
        nextToken()
        val right = parseExpression(Precedence.PREFIX)
        return PrefixExpression(token, token.literal, right)
    }

    // <expression> <infix operator> <expression>
    private fun parseInfixExpression(left: Expression): Expression {
        val token = curToken
        val precedence = curPrecedence()
        nextToken()
        val right = parseExpression(precedence)
        return InfixExpression(token, left, token.literal, right)
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

    private fun peekPrecedence(): Precedence {
        return precedences[peekToken.type] ?: Precedence.LOWEST
    }

    private fun curPrecedence(): Precedence {
        return precedences[curToken.type] ?: Precedence.LOWEST
    }
}

class ParseException : RuntimeException {

    constructor(expected: TokenType, actual: Token) : super(
        "parse error, expected: ${expected.literal}, " +
                "actually: ${actual.type.literal}(${actual.literal})"
    )

    constructor(msg: String) : super("parse error: $msg")
}

