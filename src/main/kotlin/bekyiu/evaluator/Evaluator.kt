package bekyiu.evaluator

import bekyiu.`object`.*
import bekyiu.`object`.Boolean
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
            is BlockStatement -> evalStatements(node.statements)
            is IfExpression -> evalIfExpression(node)
            is PrefixExpression -> {
                val right = eval(node.right)
                evalPrefixExpression(node.operator, right!!)
            }
            is InfixExpression -> {
                val left = eval(node.left)
                val right = eval(node.right)
                return evalInfixExpression(node.operator, left!!, right!!)
            }
            else -> null
        }
        // println(v)
        return v
    }

    private fun evalIfExpression(node: IfExpression): Object? {
        val cond = eval(node.condition)
        return when {
            isTruthy(cond!!) -> {
                eval(node.consequence)
            }
            node.alternative != null -> {
                eval(node.alternative!!)
            }
            else -> {
                null
            }
        }
    }

    private fun isTruthy(obj: Object): kotlin.Boolean {
        return when(obj) {
            Null.NULL -> false
            Boolean.TRUE -> true
            Boolean.FALSE -> false
            else -> true
        }
    }

    private fun evalInfixExpression(operator: String, left: Object, right: Object): Object? {
        return when {
            left.type() == ObjectType.INTEGER && right.type() == ObjectType.INTEGER -> evalIntegerInfixExpression(
                operator,
                left as Integer,
                right as Integer,
            )
            // check equality between booleans
            operator == "==" -> Boolean.nativeToObject(left == right)
            operator == "!=" -> Boolean.nativeToObject(left != right)
            else -> null
        }
    }

    private fun evalIntegerInfixExpression(operator: String, left: Integer, right: Integer): Object? {
        return when (operator) {
            "+" -> Integer(left.value + right.value)
            "-" -> Integer(left.value - right.value)
            "*" -> Integer(left.value * right.value)
            "/" -> Integer(left.value / right.value)
            "<" -> Boolean.nativeToObject(left.value < right.value)
            ">" -> Boolean.nativeToObject(left.value > right.value)
            "==" -> Boolean.nativeToObject(left.value == right.value)
            "!=" -> Boolean.nativeToObject(left.value != right.value)
            else -> null
        }

    }

    private fun evalPrefixExpression(operator: String, right: Object): Object? {
        return when (operator) {
            "!" -> evalBangOperatorExpression(right)
            "-" -> evalMinusPrefixOperatorExpression(right)
            else -> null // throw error
        }
    }

    private fun evalMinusPrefixOperatorExpression(right: Object): Object? {
        return when (right) {
            is Integer -> Integer(-right.value)
            else -> null
        }
    }

    // the behaviour of the ! is specified
    private fun evalBangOperatorExpression(right: Object): Object {
        return when (right) {
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