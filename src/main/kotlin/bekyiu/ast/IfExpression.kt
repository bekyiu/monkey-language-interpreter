package bekyiu.ast

import bekyiu.lexer.Token
import java.lang.StringBuilder

/**
 * @Date 2021/12/12 11:08 上午
 * @Created by bekyiu
 */
class IfExpression(
    // if
    val token: Token,
    val condition: Expression,
    val consequence: BlockStatement,
    var alternative: BlockStatement?,
) : Expression {
    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        val sb = StringBuilder(1024)
        sb.append("if")
            .append(condition.toString())
            .append(" ")
            .append(consequence.toString())

        alternative?.let {
            sb.append(" else ")
                .append(it.toString())
        }

        return sb.toString()
    }
}