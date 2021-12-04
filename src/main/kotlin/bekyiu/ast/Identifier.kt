package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/4 1:25 下午
 * @Created by bekyiu
 */
class Identifier(
    // IDENT
    val token: Token,
    val value: String
) : Expression {

    override fun expressionNode() {
        TODO("Not yet implemented")
    }

    override fun tokenLiteral() = token.literal
}