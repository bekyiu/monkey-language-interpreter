package bekyiu.evaluator

import bekyiu.`object`.Integer
import bekyiu.`object`.Null
import bekyiu.`object`.Object
import bekyiu.lexer.Lexer
import bekyiu.parser.Parser
import org.junit.Assert.*
import org.junit.Test

/**
 * @Date 2021/12/13 8:00 下午
 * @Created by bekyiu
 */
class EvaluatorTest {

    @Test
    fun testBangOperator() {
        class Sample(val input: String, val expected: Boolean)

        val cases = listOf(
            Sample("!true", false),
            Sample("!false", true),
            Sample("!!!false", true),
            Sample("!5", false),
            Sample("!!5", true),
        )

        for (case in cases) {
            val value = testEval(case.input)
            testBooleanObject(value, case.expected)
        }
    }

    @Test
    fun testEvalBooleanExpression() {
        class Sample(val input: String, val expected: Boolean)

        val cases = listOf(
            Sample("true", true),
            Sample("false", false),
        )

        for (case in cases) {
            val value = testEval(case.input)
            testBooleanObject(value, case.expected)
        }
    }

    fun testBooleanObject(obj: Object, expected: Boolean) {
        assert(obj is bekyiu.`object`.Boolean)
        val bool = obj as bekyiu.`object`.Boolean
        assert(bool.value == expected)
    }

    @Test
    fun testEvalIntegerExpression() {
        class Sample(val input: String, val expected: Long)

        val cases = listOf(
            Sample("5", 5),
            Sample("77", 77),
            Sample("-77", -77),
        )

        for (case in cases) {
            val value = testEval(case.input)
            testIntegerObject(value, case.expected)
        }
    }

    fun testEval(input: String): Object {
        val l = Lexer(input)
        val p = Parser(l)
        val program = p.parseProgram()
        val e = Evaluator()
        return e.eval(program) ?: Null.NULL
    }

    fun testIntegerObject(obj: Object, expected: Long) {
        assert(obj is Integer)
        val int = obj as Integer
        assert(int.value == expected)
    }
}