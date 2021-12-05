package bekyiu.ast

import java.lang.StringBuilder

/**
 * @Date 2021/12/4 12:41 下午
 * @Created by bekyiu
 */
// root node of every ast
// every valid money program is a series of statements
class Program(
    var statements: MutableList<Statement>
) {

    fun tokenLiteral() =
        if (statements.isNotEmpty()) {
            statements[0].tokenLiteral()
        } else {
            ""
        }

    override fun toString(): String {
        val str = StringBuilder(1024)
        for (s in statements) {
            str.append(s.toString()).append("\n")
        }
        return str.toString()
    }
}