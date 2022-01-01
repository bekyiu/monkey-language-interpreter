package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/5 5:44 下午
 * @Created by bekyiu
 */
data class PrefixExpression(
    // prefix token like ! or -
    val token: Token,
    val operator: String,
    val right: Expression,
) : Expression {

    override fun tokenLiteral() = token.literal

    override fun toString() = "($operator$right)"
}