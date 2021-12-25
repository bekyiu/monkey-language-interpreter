package bekyiu.`object`

/**
 * @Date 2021/12/12 8:58 下午
 * @Created by bekyiu
 */
class _Null private constructor() : _Object {

    companion object {
        val NULL = _Null()
    }

    override fun type() = _ObjectType.NULL

    override fun toString(): String {
        return "null"
    }
//    override fun inspect() = "null"
}