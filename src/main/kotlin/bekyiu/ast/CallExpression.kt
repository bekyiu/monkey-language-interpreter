package bekyiu.ast

import bekyiu.lexer.Token
import java.lang.StringBuilder

/**
 * @Date 2021/12/12 3:32 下午
 * @Created by bekyiu
 */
data class CallExpression(
    // (
    val token: Token,
    // identifier or function literal
    val function: Expression,
    val arguments: MutableList<Expression>,
) : Expression {
    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        val args = mutableListOf<String>()
        for (a in arguments) {
            args.add(a.toString())
        }

        return StringBuilder(1024)
            .append(function.toString())
            .append(args.joinToString(prefix = "(", postfix = ")"))
            .toString()
    }
}