package bekyiu.`object`

/**
 * @Date 2021/12/25 11:45 上午
 * @Created by bekyiu
 */
data class _String(
    val value: String
) : _Object, _Hashable {

    override fun type() = _ObjectType.STRING

    override fun toString() = value
}