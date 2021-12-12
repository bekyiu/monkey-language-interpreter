package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/4 1:24 下午
 * @Created by bekyiu
 */
class LetStatement(
    // LET
    val token: Token,
    // hold the identifier of the binding
    val name: Identifier,
    val value: Expression?,
) : Statement {

    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        return "${tokenLiteral()} ${name.toString()} = ${value?.toString()};"
    }
}