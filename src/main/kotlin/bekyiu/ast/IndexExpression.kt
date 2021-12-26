package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2021/12/26 2:44 下午
 * @Created by bekyiu
 */
// like this: a[0]
class IndexExpression(
    // [
    val token: Token,
    // a
    val left: Expression,
    // 0
    val index: Expression,
) : Expression {
    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        val sb = StringBuilder(32)
        return sb.append("(")
            .append(left.toString())
            .append("[")
            .append(index.toString())
            .append("])").toString()
    }
}