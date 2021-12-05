package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/4 4:32 下午
 * @Created by bekyiu
 */
class ReturnStatement(
    val token: Token,
    val returnValue: Expression?,
) : Statement {
    override fun statementNode() {
        TODO("Not yet implemented")
    }

    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        return "${tokenLiteral()} ${returnValue?.toString()};"
    }
}