package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/25 11:29 上午
 * @Created by bekyiu
 */
class StringLiteral(
    val token: Token,
    val value: String
) : Expression {

    override fun tokenLiteral() = token.literal

    override fun toString() = token.literal
}