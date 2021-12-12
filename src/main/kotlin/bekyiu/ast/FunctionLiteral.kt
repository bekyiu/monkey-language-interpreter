package bekyiu.ast

import bekyiu.lexer.Token
import java.lang.StringBuilder

/**
 * @Date 2021/12/12 12:43 下午
 * @Created by bekyiu
 */
class FunctionLiteral(
    // fn
    val token: Token,
    val parameters: MutableList<Identifier>,
    val body: BlockStatement
) : Expression {

    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        val params = mutableListOf<String>()
        for (p in parameters) {
            params.add(p.toString())
        }

        val sb = StringBuilder(1024)
        sb.append("fn")
            .append(params.joinToString(prefix = " (", postfix = ") "))
            .append(body.toString())

        return sb.toString()
    }
}