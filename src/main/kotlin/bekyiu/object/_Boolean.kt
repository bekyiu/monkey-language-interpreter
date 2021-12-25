package bekyiu.`object`

import kotlin.Boolean

/**
 * @Date 2021/12/12 8:45 下午
 * @Created by bekyiu
 */
class _Boolean private constructor(var value: Boolean) : _Object {

    companion object {
        val TRUE = _Boolean(true)
        val FALSE = _Boolean(false)
        fun nativeToObject(v: Boolean) = if (v) TRUE else FALSE
    }

    override fun type() = _ObjectType.BOOLEAN
    override fun toString(): String {
        return value.toString()
    }
//    override fun inspect() = value.toString()

}