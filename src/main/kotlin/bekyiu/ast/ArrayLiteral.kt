package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/25 3:13 下午
 * @Created by bekyiu
 */
class ArrayLiteral(
    // [
    val token: Token,
    var elements: MutableList<Expression>
) : Expression {
    override fun tokenLiteral() = token.literal
    override fun toString() = elements.toString()
}