package bekyiu.evaluator

import bekyiu.`object`.*

/**
 * @Date 2021/12/25 2:26 下午
 * @Created by bekyiu
 */
var builtins = mapOf(
    "len" to _Builtin(::len)
)

fun len(args: List<_Object>): _Object {
    if (args.size != 1) {
        return _Error("wrong number of arguments: ${args.size}, expected: 1")
    }
    if (args[0] !is _String) {
        return _Error("argument to len not supported: ${args[0].type()}, expected: ${_ObjectType.STRING}")
    }
    val str = args[0] as _String
    return _Integer(str.value.length.toLong())
}