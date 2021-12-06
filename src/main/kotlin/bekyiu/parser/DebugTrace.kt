package bekyiu.parser

/**
 * @Date 2021/12/6 5:56 下午
 * @Created by bekyiu
 *
 * help debug the parser how to work
 */
var traceLevel = 0
const val TAB = "\t"

fun trace(msg: String) {
    println("${TAB.repeat(traceLevel)} begin $msg")
    traceLevel++
}

fun unTrace(msg: String) {
    traceLevel--
    println("${TAB.repeat(traceLevel)} end $msg")
}