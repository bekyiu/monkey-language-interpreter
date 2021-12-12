package bekyiu.ast

import bekyiu.lexer.Token
import java.lang.StringBuilder

/**
 * @Date 2021/12/12 11:10 上午
 * @Created by bekyiu
 */
// Block statements are a series of statements enclosed by an opening { and a closing }
class BlockStatement(
    // {
    val token: Token,
    val statements: MutableList<Statement>
) : Statement {
    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        val sb = StringBuilder(1024)
        for (s in statements) {
            sb.append(s.toString())
        }
        return sb.toString()
    }
}