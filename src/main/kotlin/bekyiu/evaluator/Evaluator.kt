package bekyiu.evaluator

import bekyiu.`object`.*
import bekyiu.ast.*

/**
 * @Date 2021/12/13 8:00 下午
 * @Created by bekyiu
 */
class Evaluator {

    fun eval(node: Node, env: Environment): _Object? {
        val v = when (node) {
            is Program -> evalProgram(node.statements, env)
            is ExpressionStatement -> eval(node.expression, env)
            is IntegerLiteral -> _Integer(node.value)
            is Bool -> _Boolean.nativeToObject(node.value)
            is BlockStatement -> evalBlockStatement(node.statements, env)
            is IfExpression -> evalIfExpression(node, env)
            is Identifier -> evalIdentifier(node, env)
            is StringLiteral -> _String(node.value)
            is FunctionLiteral -> _Function(node.parameters, node.body, env)
            is PrefixExpression -> {
                val right = eval(node.right, env)
                if (right is _Error) {
                    return right
                }
                evalPrefixExpression(node.operator, right!!)
            }
            is InfixExpression -> {
                val left = eval(node.left, env)
                if (left is _Error) {
                    return left
                }
                val right = eval(node.right, env)
                if (right is _Error) {
                    return right
                }
                evalInfixExpression(node.operator, left!!, right!!)
            }
            is ReturnStatement -> {
                when (val v = eval(node.returnValue, env)) {
                    is _Error -> return v
                    else -> _ReturnValue(v!!)
                }
            }
            is LetStatement -> {
                val v = eval(node.value, env)
                if (v is _Error) {
                    return v
                }
                env.set(node.name.value, v!!)
            }
            is CallExpression -> {
                // object._Function
                val func = eval(node.function, env)
                if (func is _Error) {
                    return func
                }
                val args = evalExpressions(node.arguments, env)
                if ((args.size == 1) and (args[0] is _Error)) {
                    return args[0]
                }
                applyFunction(func, args)
            }
            else -> null
        }
        // println(v)
        return v
    }

    private fun applyFunction(func: _Object?, args: MutableList<_Object>): _Object? {
        return when (func) {
            is _Function -> {
                val curEnv = Environment(outer = func.env)
                func.parameters.forEachIndexed { idx, param ->
                    curEnv.set(param.value, args[idx])
                }
                val evaluated = eval(func.body, curEnv)
                // unwrap the return value
                if (evaluated is _ReturnValue) {
                    evaluated.value
                } else {
                    // implicitly return
                    evaluated
                }
            }
            is _Builtin -> func.fn(args)
            else -> _Error("not a function: ${func?.type()}")
        }

    }

    // process params
    private fun evalExpressions(exps: MutableList<Expression>, env: Environment): MutableList<_Object> {
        val ret = mutableListOf<_Object>()
        for (exp in exps) {
            val p = eval(exp, env)
            if (p is _Error) {
                ret.add(p)
                return ret
            }
            ret.add(p!!)
        }
        return ret
    }

    private fun evalIdentifier(node: Identifier, env: Environment): _Object {
        return env.get(node.value) ?: builtins[node.value] ?: _Error("identifier not found: ${node.value}")
    }

    private fun evalIfExpression(node: IfExpression, env: Environment): _Object? {
        val cond = eval(node.condition, env)
        return when {
            cond is _Error -> cond
            isTruthy(cond!!) -> eval(node.consequence, env)
            node.alternative != null -> eval(node.alternative!!, env)
            else -> null
        }
    }

    private fun isTruthy(obj: _Object): Boolean {
        return when (obj) {
            _Null.NULL -> false
            _Boolean.TRUE -> true
            _Boolean.FALSE -> false
            else -> true
        }
    }

    private fun evalInfixExpression(operator: String, left: _Object, right: _Object): _Object {
        return when {
            left.type() != right.type() -> _Error("type mismatch: ${left.type()} $operator ${right.type()}")
            left.type() == _ObjectType.INTEGER && right.type() == _ObjectType.INTEGER -> evalIntegerInfixExpression(
                operator,
                left as _Integer,
                right as _Integer,
            )
            left.type() == _ObjectType.STRING && right.type() == _ObjectType.STRING -> evalStringInfixExpression(
                operator,
                left as _String,
                right as _String,
            )
            // check equality between booleans
            operator == "==" -> _Boolean.nativeToObject(left == right)
            operator == "!=" -> _Boolean.nativeToObject(left != right)
            else -> _Error("unknown operator: ${left.type()} $operator ${right.type()}")
        }
    }

    private fun evalStringInfixExpression(operator: String, left: _String, right: _String): _Object {
        if (operator != "+") {
            return _Error("unknown operator: ${left.type()} $operator ${right.type()}")
        }
        return _String(left.value + right.value)
    }

    private fun evalIntegerInfixExpression(operator: String, left: _Integer, right: _Integer): _Object {
        return when (operator) {
            "+" -> _Integer(left.value + right.value)
            "-" -> _Integer(left.value - right.value)
            "*" -> _Integer(left.value * right.value)
            "/" -> _Integer(left.value / right.value)
            "<" -> _Boolean.nativeToObject(left.value < right.value)
            ">" -> _Boolean.nativeToObject(left.value > right.value)
            "==" -> _Boolean.nativeToObject(left.value == right.value)
            "!=" -> _Boolean.nativeToObject(left.value != right.value)
            else -> _Error("unknown operator: ${left.type()} $operator ${right.type()}")
        }

    }

    private fun evalPrefixExpression(operator: String, right: _Object): _Object {
        return when (operator) {
            "!" -> evalBangOperatorExpression(right)
            "-" -> evalMinusPrefixOperatorExpression(right)
            else -> _Error("unknown operator: ${operator}${right.type()}")
        }
    }

    private fun evalMinusPrefixOperatorExpression(right: _Object): _Object {
        return when (right) {
            is _Integer -> _Integer(-right.value)
            else -> _Error("unknown operator: -${right.type()}")
        }
    }

    // the behaviour of the ! is specified
    private fun evalBangOperatorExpression(right: _Object): _Object {
        return when (right) {
            _Boolean.TRUE -> _Boolean.FALSE
            _Boolean.FALSE -> _Boolean.TRUE
            _Null.NULL -> _Boolean.TRUE
            else -> _Boolean.FALSE
        }
    }

    // if we have nested block statements
    // we can’t unwrap the value of _ReturnValue on first sight
    // because we need to further keep track of it
    // so we can stop the execution in the outermost block statement
    private fun evalBlockStatement(stmts: MutableList<Statement>, env: Environment): _Object? {
        var result: _Object? = null
        for (stmt in stmts) {
            result = eval(stmt, env)
            if ((result is _ReturnValue) or (result is _Error)) {
                // return itself
                return result
            }
        }
        return result
    }

    private fun evalProgram(stmts: MutableList<Statement>, env: Environment): _Object? {
        var result: _Object? = null
        for (stmt in stmts) {
            result = eval(stmt, env)
            when (result) {
                is _ReturnValue -> return result.value // unwrap the _ReturnValue
                is _Error -> return result
            }
        }
        return result
    }
}