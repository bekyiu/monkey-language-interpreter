package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/5 11:35 上午
 * @Created by bekyiu
 */
// it’s a statement that consists solely of one expression
// we can push the expression to statements list
data class ExpressionStatement(
    // the first token of the expression
    val token: Token,
    var expression: Expression,
) : Statement {
    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        return expression.toString()
    }
}