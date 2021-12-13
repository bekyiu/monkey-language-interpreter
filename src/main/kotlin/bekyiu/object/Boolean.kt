package bekyiu.`object`

import kotlin.Boolean

/**
 * @Date 2021/12/12 8:45 下午
 * @Created by bekyiu
 */
class Boolean private constructor(var value: Boolean) : Object {

    companion object {
        val TRUE = bekyiu.`object`.Boolean(true)
        val FALSE = bekyiu.`object`.Boolean(false)
        fun nativeToObject(v: Boolean) = if (v) TRUE else FALSE
    }

    override fun type() = ObjectType.BOOLEAN
    override fun toString(): String {
        return value.toString()
    }
//    override fun inspect() = value.toString()

}