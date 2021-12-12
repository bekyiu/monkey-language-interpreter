package bekyiu.`object`

/**
 * @Date 2021/12/12 8:58 下午
 * @Created by bekyiu
 */
class Null : Object {
    override fun type() = ObjectType.NULL

    override fun inspect() = "null"
}