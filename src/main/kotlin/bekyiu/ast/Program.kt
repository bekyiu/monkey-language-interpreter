package bekyiu.ast

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

}