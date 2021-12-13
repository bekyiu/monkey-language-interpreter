package bekyiu.`object`

/**
 * @Date 2021/12/12 8:37 下午
 * @Created by bekyiu
 */
class Integer(
    var value: Long
) : Object {
    override fun type() = ObjectType.INTEGER

    override fun toString(): String {
        return value.toString()
    }
//    override fun inspect() = value.toString()

}