package bekyiu.evaluator

import bekyiu.`object`.Integer
import bekyiu.`object`.Null
import bekyiu.`object`.Object
import bekyiu.ast.*

/**
 * @Date 2021/12/13 8:00 下午
 * @Created by bekyiu
 */
class Evaluator {

    fun eval(node: Node): Object? {
        val v = when (node) {
            is Program -> evalStatements(node.statements)
            is ExpressionStatement -> eval(node.expression)
            is IntegerLiteral -> Integer(node.value)
            else -> null
        }
        // println(v)
        return v
    }

    private fun evalStatements(stmts: MutableList<Statement>): Object? {
        var result: Object? = null
        for (stmt in stmts) {
            result = eval(stmt)
        }
        return result
    }
}