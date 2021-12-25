package bekyiu.evaluator

import bekyiu.`object`._Object


/**
 * @Date 2021/12/19 6:15 下午
 * @Created by bekyiu
 */
class Environment(
    val store: MutableMap<String, _Object> = mutableMapOf(),
    // The outer scope encloses the inner scope. And the inner scope extends the outer one
    var outer: Environment? = null,
) {
    fun set(k: String, v: _Object): _Object {
        store[k] = v
        return v
    }

    fun get(k: String): _Object? {
        if (store.containsKey(k)) {
            return store[k]
        }
        return outer?.get(k)
    }
}