package bekyiu.evaluator

import bekyiu.`object`.*

/**
 * @Date 2021/12/25 2:26 下午
 * @Created by bekyiu
 */
var builtins = mapOf(
    "len" to _Builtin(::len),
    "first" to _Builtin(::first),
    "last" to _Builtin(::last),
    "rest" to _Builtin(::rest),
    "push" to _Builtin(::push),
)

// typealias BuiltinFunction = (List<_Object>) -> _Object

private fun len(args: List<_Object>): _Object {
    if (args.size != 1) {
        return _Error("wrong number of arguments: ${args.size}, expected: 1")
    }

    return when (val arg = args[0]) {
        is _String -> _Integer(arg.value.length.toLong())
        is _Array -> _Integer(arg.elements.size.toLong())
        else -> _Error(
            "argument to len not supported: ${args[0].type()}, " +
                    "expected: ${_ObjectType.STRING} or ${_ObjectType.ARRAY}"
        )
    }
}

private fun first(args: List<_Object>): _Object {
    if (args.size != 1) {
        return _Error("wrong number of arguments: ${args.size}, expected: 1")
    }
    return when (val arg = args[0]) {
        is _Array -> {
            if (arg.elements.size > 0) {
                arg.elements[0]
            } else {
                _Null.NULL
            }
        }
        else -> _Error(
            "argument to first not supported: ${args[0].type()}, " +
                    "expected: ${_ObjectType.ARRAY}"
        )
    }
}

private fun last(args: List<_Object>): _Object {
    if (args.size != 1) {
        return _Error("wrong number of arguments: ${args.size}, expected: 1")
    }
    return when (val arg = args[0]) {
        is _Array -> {
            if (arg.elements.size > 0) {
                arg.elements[arg.elements.size - 1]
            } else {
                _Null.NULL
            }
        }
        else -> _Error(
            "argument to last not supported: ${args[0].type()}, " +
                    "expected: ${_ObjectType.ARRAY}"
        )
    }
}


private fun rest(args: List<_Object>): _Object {
    if (args.size != 1) {
        return _Error("wrong number of arguments: ${args.size}, expected: 1")
    }
    return when (val arg = args[0]) {
        is _Array -> {
            val elements = arg.elements
            if (elements.size > 0) {
                mutableListOf<_Object>().let {
                    it.addAll(elements.subList(1, elements.size))
                    _Array(it)
                }
            } else {
                _Null.NULL
            }
        }
        else -> _Error(
            "argument to rest not supported: ${args[0].type()}, " +
                    "expected: ${_ObjectType.ARRAY}"
        )
    }
}


private fun push(args: List<_Object>): _Object {
    if (args.size != 2) {
        return _Error("wrong number of arguments: ${args.size}, expected: 2")
    }
    return when (val arg = args[0]) {
        is _Array -> {
            val elements = arg.elements
            mutableListOf<_Object>().let {
                it.addAll(elements)
                it.add(args[1])
                _Array(it)
            }
        }
        else -> _Error(
            "argument to push not supported: ${args[0].type()}, " +
                    "expected: ${_ObjectType.ARRAY}"
        )
    }
}