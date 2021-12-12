package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/5 4:24 下午
 * @Created by bekyiu
 */
class IntegerLiteral(
    val token: Token,
    val value: Long,
) : Expression {

    override fun tokenLiteral() = token.literal

    override fun toString() = token.literal
}