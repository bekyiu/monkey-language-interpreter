package bekyiu.parser

/**
 * @Date 2021/12/5 2:37 下午
 * @Created by bekyiu
 */
// bigger number has higher precedence
enum class Precedence(val p: Int) {
    LOWEST(1),

    // ==
    EQUALS(2),

    // > or <
    LESS_GREATER(3),

    // +
    SUM(4),

    // *
    PRODUCT(5),

    // -x or !x
    PREFIX(6),

    // func(x)
    CALL(7),
    ;

    fun lt(precedence: Precedence) = this.p < precedence.p
}