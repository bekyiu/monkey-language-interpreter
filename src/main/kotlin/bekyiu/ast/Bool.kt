package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/6 6:25 下午
 * @Created by bekyiu
 */
data class Bool(
    val token: Token,
    val value: Boolean,
) : Expression {
    override fun tokenLiteral() = token.literal

    override fun toString() = token.literal
}