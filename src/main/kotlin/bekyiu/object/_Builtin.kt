package bekyiu.`object`

/**
 * @Date 2021/12/25 1:40 下午
 * @Created by bekyiu
 */
typealias BuiltinFunction = (List<_Object>) -> _Object

class _Builtin(
    val fn: BuiltinFunction
) : _Object {

    override fun type() = _ObjectType.BUILTIN
    override fun toString() = "builtin function"
}
