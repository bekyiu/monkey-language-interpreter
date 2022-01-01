package bekyiu.ast

import bekyiu.lexer.Token

/**
 * @Date 2022/1/1 1:42 下午
 * @Created by bekyiu
 */
data class HashLiteral(
    // {
    val token: Token,
    // the only admissible data type for hash keys are strings, integers, and booleans
    // but we check hash key types in eval stage not in parse stage
    // because many expressions can produce string, integer and boolean
    val pairs: MutableMap<Expression, Expression>
) : Expression {

    override fun tokenLiteral() = token.literal
    override fun toString() = pairs.toString()
}