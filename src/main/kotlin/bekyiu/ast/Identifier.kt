package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/4 1:25 下午
 * @Created by bekyiu
 */
data class Identifier(
    // IDENT
    val token: Token,
    val value: String
) : Expression {

    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        return value
    }
}