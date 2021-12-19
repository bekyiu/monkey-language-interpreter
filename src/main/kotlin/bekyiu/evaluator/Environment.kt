package bekyiu.evaluator

import bekyiu.`object`.Object

/**
 * @Date 2021/12/19 6:15 下午
 * @Created by bekyiu
 */
class Environment(
    val store: MutableMap<String, Object> = mutableMapOf(),
    // The outer scope encloses the inner scope. And the inner scope extends the outer one
    var outer: Environment? = null,
) {
    fun set(k: String, v: Object): Object {
        store[k] = v
        return v
    }

    fun get(k: String): Object? {
        if (store.containsKey(k)) {
            return store[k]
        }
        return outer?.get(k)
    }
}