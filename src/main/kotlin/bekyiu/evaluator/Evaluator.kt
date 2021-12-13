package bekyiu.evaluator

import bekyiu.`object`.Boolean
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
            is Bool -> Boolean.nativeToObject(node.value)
            is PrefixExpression -> {
                val right = eval(node.right)
                evalPrefixExpression(node.operator, right!!)
            }
            else -> null
        }
        // println(v)
        return v
    }

    private fun evalPrefixExpression(operator: String, right: Object): Object? {
        return when(operator) {
            "!" -> evalBangOperatorExpression(right)
            "-" -> evalMinusPrefixOperatorExpression(right)
            else -> null // throw error
        }
    }

    private fun evalMinusPrefixOperatorExpression(right: Object): Object? {
        return when(right) {
            is Integer -> Integer(-right.value)
            else -> null
        }
    }

    // the behaviour of the ! is specified
    private fun evalBangOperatorExpression(right: Object): Object {
        return when(right) {
            Boolean.TRUE -> Boolean.FALSE
            Boolean.FALSE -> Boolean.TRUE
            Null.NULL -> Boolean.TRUE
            else -> Boolean.FALSE
        }
    }

    private fun evalStatements(stmts: MutableList<Statement>): Object? {
        var result: Object? = null
        for (stmt in stmts) {
            result = eval(stmt)
        }
        return result
    }
}