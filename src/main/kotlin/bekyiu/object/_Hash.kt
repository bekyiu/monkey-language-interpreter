package bekyiu.`object`

/**
 * @Date 2022/1/1 3:18 下午
 * @Created by bekyiu
 */

// only the data type which implements the _Hashable interface can be the key of the hash map
// string, integer and boolean need to implement this
interface _Hashable

data class _Hash(
    val pairs: MutableMap<_Hashable, _Object>
) : _Object {
    override fun type() = _ObjectType.HASH
    override fun toString() = pairs.toString()
}