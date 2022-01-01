package bekyiu.`object`

/**
 * @Date 2021/12/12 8:37 下午
 * @Created by bekyiu
 */
data class _Integer(
    var value: Long
) : _Object, _Hashable {
    override fun type() = _ObjectType.INTEGER

    override fun toString(): String {
        return value.toString()
    }
//    override fun inspect() = value.toString()

}