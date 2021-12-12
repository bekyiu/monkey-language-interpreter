package bekyiu.`object`

import kotlin.Boolean

/**
 * @Date 2021/12/12 8:45 下午
 * @Created by bekyiu
 */
class Boolean(var value: Boolean) : Object {
    override fun type() = ObjectType.BOOLEAN

    override fun inspect() = value.toString()
}