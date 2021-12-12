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
        registerPrefix(TokenType.TRUE, ::parseBool)
        registerPrefix(TokenType.FALSE, ::parseBool)
        registerPrefix(TokenType.LPAREN, ::parseGroupedExpression)
        registerPrefix(TokenType.IF, ::parseIfExpression)
        registerPrefix(TokenType.FUNCTION, ::parseFunctionLiteral)

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
            val s = parseStatement()
            program.statements.add(s)
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

    // fn <parameters> <block statement>
    private fun parseFunctionLiteral(): Expression {
        // fn
        val token = curToken
        expectPeek(TokenType.LPAREN)
        val params = parseFunctionParameters()
        expectPeek(TokenType.LBRACE)
        val body = parseBlockStatement()
        return FunctionLiteral(token, params, body)
    }

    // (ident1, ident2, ...)
    // (ident1, ident2)
    private fun parseFunctionParameters(): MutableList<Identifier> {
        val params = mutableListOf<Identifier>()
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken()
            return params
        }
        // skip (
        nextToken()

        var ident = Identifier(curToken, curToken.literal)
        params.add(ident)

        while (peekTokenIs(TokenType.COMMA)) {
            nextToken()
            nextToken()
            ident = Identifier(curToken, curToken.literal)
            params.add(ident)
        }
        expectPeek(TokenType.RPAREN)
        return params
    }

    // {<statements>}
    private fun parseBlockStatement(): BlockStatement {
        // {
        val token = curToken
        nextToken()
        val statements = mutableListOf<Statement>()
        while (!curTokenIs(TokenType.RBRACE) && !curTokenIs(TokenType.EOF)) {
            statements.add(parseStatement())
            nextToken()
        }
        // current token is }
        return BlockStatement(token, statements)
    }

    // if (<condition>) <consequence> else <alternative>
    private fun parseIfExpression(): Expression {
        // if
        val token = curToken
        expectPeek(TokenType.LPAREN)
        nextToken()
        val condition = parseExpression(Precedence.LOWEST)
        expectPeek(TokenType.RPAREN)
        expectPeek(TokenType.LBRACE)
        val consequence = parseBlockStatement()
        var alternative: BlockStatement? = null
        // curToken is }
        if (peekTokenIs(TokenType.ELSE)) {
            nextToken()
            expectPeek(TokenType.LBRACE)
            alternative = parseBlockStatement()
        }
        return IfExpression(token, condition, consequence, alternative)
    }

    // (<expression>);
    private fun parseGroupedExpression(): Expression {
        // e.g: (1 + 2) * 3
        nextToken()
        // 1 + 2) * 3
        // it can be seen as ')' in place of the original '*' 's position
        // and ')' is lower than '+', so '2' combine with '+'
        val exp = parseExpression(Precedence.LOWEST)
        expectPeek(TokenType.RPAREN)
        return exp
    }

    // <true or false>;
    private fun parseBool(): Expression {
        return Bool(curToken, curToken.literal.toBoolean())
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

