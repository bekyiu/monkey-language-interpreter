package bekyiu.parser

import bekyiu.lexer.Token
import bekyiu.lexer.TokenType

/**
 * @Date 2021/12/12 4:18 下午
 * @Created by bekyiu
 */
class ParseException : RuntimeException {

    constructor(expected: TokenType, actual: Token) : super(
        "parse error, expected: ${expected.literal}, " +
                "actually: ${actual.type.literal}(${actual.literal})"
    )

    constructor(msg: String) : super("parse error: $msg")
}