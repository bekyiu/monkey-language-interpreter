package bekyiu.`object`

import bekyiu.ast.BlockStatement
import bekyiu.ast.Identifier
import bekyiu.evaluator.Environment
import java.lang.StringBuilder

/**
 * @Date 2021/12/19 7:47 下午
 * @Created by bekyiu
 */
class Function(
    val parameters: MutableList<Identifier>,
    val body: BlockStatement,
    // the environment where the function is located, not the environment where the function is used
    val env: Environment,
) : Object {
    override fun type() = ObjectType.FUNCTION

    override fun toString(): String {
        val params = mutableListOf<String>()
        for (p in parameters) {
            params.add(p.toString())
        }

        val sb = StringBuilder(1024)
        sb.append("fn")
            .append(params.joinToString(prefix = " (", postfix = ") "))
            .append("{\n")
            .append(body.toString())
            .append("\n}")

        return sb.toString()
    }
}