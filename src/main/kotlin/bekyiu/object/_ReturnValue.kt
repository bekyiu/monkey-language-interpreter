package bekyiu.`object`

/**
 * @Date 2021/12/19 3:36 下午
 * @Created by bekyiu
 */
class _ReturnValue(
    var value: _Object
) : _Object {
    override fun type() = _ObjectType.RETURN_VALUE
}