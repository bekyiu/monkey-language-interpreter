package bekyiu.`object`

/**
 * @Date 2021/12/12 8:40 下午
 * @Created by bekyiu
 */
enum class _ObjectType(val type: String) {
    INTEGER("INTEGER"),
    BOOLEAN("BOOLEAN"),
    NULL("NULL"),
    RETURN_VALUE("RETURN_VALUE"),
    ERROR("ERROR"),
    FUNCTION("FUNCTION"),
    STRING("STRING"),
    BUILTIN("BUILTIN"),
    ARRAY("ARRAY"),
    HASH("HASH"),
}