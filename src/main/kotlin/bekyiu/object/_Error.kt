package bekyiu.`object`

/**
 * @Date 2021/12/19 4:47 下午
 * @Created by bekyiu
 */

data class _Error(
    val message: String
) : _Object {
    override fun type() = _ObjectType.ERROR
    override fun toString() = message
}