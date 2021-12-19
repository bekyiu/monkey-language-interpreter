package bekyiu.`object`

/**
 * @Date 2021/12/19 4:47 下午
 * @Created by bekyiu
 */

class Error(
    val message: String
) : Object {
    override fun type() = ObjectType.ERROR
}