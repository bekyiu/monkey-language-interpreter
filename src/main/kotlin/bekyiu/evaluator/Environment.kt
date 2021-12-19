package bekyiu.evaluator

import bekyiu.`object`.Object

/**
 * @Date 2021/12/19 6:15 下午
 * @Created by bekyiu
 */
class Environment(
    val store: MutableMap<String, Object> = mutableMapOf()
) {
    fun set(k: String, v: Object): Object {
        store[k] = v
        return v
    }

    fun get(k: String): Object? {
        return store[k]
    }
}