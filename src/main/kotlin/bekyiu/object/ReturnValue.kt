package bekyiu.`object`

/**
 * @Date 2021/12/19 3:36 下午
 * @Created by bekyiu
 */
class ReturnValue(
    var value: Object
) : Object {
    override fun type() = ObjectType.RETURN_VALUE
}