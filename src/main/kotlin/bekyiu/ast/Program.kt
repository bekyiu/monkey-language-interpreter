package bekyiu.ast

/**
 * @Date 2021/12/4 12:41 下午
 * @Created by bekyiu
 */
// root node of every ast
// every valid money program is a series of statements
data class Program(
    var statements: MutableList<Statement>
) : Node {

    override fun tokenLiteral() =
        if (statements.isNotEmpty()) {
            statements[0].tokenLiteral()
        } else {
            ""
        }

    override fun toString(): String {
        val str = StringBuilder(1024)
        for (s in statements) {
            str.append(s.toString())
        }
        return str.toString()
    }
}