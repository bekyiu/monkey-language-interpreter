package bekyiu.`object`

/**
 * @Date 2021/12/26 3:41 下午
 * @Created by bekyiu
 */
class _Array(
    val elements: MutableList<_Object>
) : _Object {
    override fun type() = _ObjectType.ARRAY

    override fun toString() = elements.toString()
}