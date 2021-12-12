package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/6 11:16 上午
 * @Created by bekyiu
 */
class InfixExpression(
    // the operator token like +
    val token: Token,
    val left: Expression,
    val operator: String,
    val right: Expression,
) : Expression {
    override fun tokenLiteral() = token.literal

    override fun toString() = "(${left} $operator ${right})"
}