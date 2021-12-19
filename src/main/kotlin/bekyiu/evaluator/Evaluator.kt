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
            is Program -> evalProgram(node.statements)
            is ExpressionStatement -> eval(node.expression)
            is IntegerLiteral -> Integer(node.value)
            is Bool -> Boolean.nativeToObject(node.value)
            is BlockStatement -> evalBlockStatement(node.statements)
            is IfExpression -> evalIfExpression(node)
            is PrefixExpression -> {
                val right = eval(node.right)
                if (right is Error) {
                    return right
                }
                evalPrefixExpression(node.operator, right!!)
            }
            is InfixExpression -> {
                val left = eval(node.left)
                if (left is Error) {
                    return left
                }
                val right = eval(node.right)
                if (right is Error) {
                    return right
                }
                evalInfixExpression(node.operator, left!!, right!!)
            }
            is ReturnStatement -> {
                when (val v = eval(node.returnValue)) {
                    is Error -> return v
                    else -> ReturnValue(v!!)
                }
            }
            else -> null
        }
        // println(v)
        return v
    }

    private fun evalIfExpression(node: IfExpression): Object? {
        val cond = eval(node.condition)
        return when {
            cond is Error -> cond
            isTruthy(cond!!) -> eval(node.consequence)
            node.alternative != null -> eval(node.alternative!!)
            else -> null
        }
    }

    private fun isTruthy(obj: Object): kotlin.Boolean {
        return when (obj) {
            Null.NULL -> false
            Boolean.TRUE -> true
            Boolean.FALSE -> false
            else -> true
        }
    }

    private fun evalInfixExpression(operator: String, left: Object, right: Object): Object {
        return when {
            left.type() != right.type() -> Error("type mismatch: ${left.type()} $operator ${right.type()}")
            left.type() == ObjectType.INTEGER && right.type() == ObjectType.INTEGER -> evalIntegerInfixExpression(
                operator,
                left as Integer,
                right as Integer,
            )
            // check equality between booleans
            operator == "==" -> Boolean.nativeToObject(left == right)
            operator == "!=" -> Boolean.nativeToObject(left != right)
            else -> Error("unknown operator: ${left.type()} $operator ${right.type()}")
        }
    }

    private fun evalIntegerInfixExpression(operator: String, left: Integer, right: Integer): Object {
        return when (operator) {
            "+" -> Integer(left.value + right.value)
            "-" -> Integer(left.value - right.value)
            "*" -> Integer(left.value * right.value)
            "/" -> Integer(left.value / right.value)
            "<" -> Boolean.nativeToObject(left.value < right.value)
            ">" -> Boolean.nativeToObject(left.value > right.value)
            "==" -> Boolean.nativeToObject(left.value == right.value)
            "!=" -> Boolean.nativeToObject(left.value != right.value)
            else -> Error("unknown operator: ${left.type()} $operator ${right.type()}")
        }

    }

    private fun evalPrefixExpression(operator: String, right: Object): Object {
        return when (operator) {
            "!" -> evalBangOperatorExpression(right)
            "-" -> evalMinusPrefixOperatorExpression(right)
            else -> Error("unknown operator: ${operator}${right.type()}")
        }
    }

    private fun evalMinusPrefixOperatorExpression(right: Object): Object {
        return when (right) {
            is Integer -> Integer(-right.value)
            else -> Error("unknown operator: -${right.type()}")
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

    // if we have nested block statements
    // we can’t unwrap the value of ReturnValue on first sight
    // because we need to further keep track of it
    // so we can stop the execution in the outermost block statement
    private fun evalBlockStatement(stmts: MutableList<Statement>): Object? {
        var result: Object? = null
        for (stmt in stmts) {
            result = eval(stmt)
            if ((result is ReturnValue) or (result is Error)) {
                // return itself
                return result
            }
        }
        return result
    }

    private fun evalProgram(stmts: MutableList<Statement>): Object? {
        var result: Object? = null
        for (stmt in stmts) {
            result = eval(stmt)
            when (result) {
                is ReturnValue -> return result.value // unwrap the ReturnValue
                is Error -> return result
            }
        }
        return result
    }
}