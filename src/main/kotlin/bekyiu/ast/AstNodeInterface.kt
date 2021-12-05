package bekyiu.ast

/**
 * @Date 2021/12/4 12:31 下午
 * @Created by bekyiu
 */
interface Node {
    // returns the literal value of the token it’s associated with
    fun tokenLiteral(): String
}

interface Statement : Node {
    fun statementNode()
}

interface Expression : Node {
    fun expressionNode()
}