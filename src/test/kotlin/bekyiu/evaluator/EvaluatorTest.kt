package bekyiu.evaluator

import bekyiu.`object`.Error
import bekyiu.`object`.Function
import bekyiu.`object`.Integer
import bekyiu.`object`.Null
import bekyiu.`object`.Object
import bekyiu.lexer.Lexer
import bekyiu.parser.Parser
import org.junit.Test

/**
 * @Date 2021/12/13 8:00 下午
 * @Created by bekyiu
 */
class EvaluatorTest {

    @Test
    fun testClosure() {
        val input = """
            let newAdder = fn(x) {
                fn(y) { x + y; };
            };
            let addTwo = newAdder(2);
            addTwo(5);
        """.trimIndent()
        testIntegerObject(testEval(input), 7)
    }

    @Test
    fun testFunctionApplication() {
        class Sample(val input: String, val expected: Long)

        val cases = listOf(
            Sample("let identity = fn(x) { x; }; identity(5);", 5),
            Sample("let identity = fn(x) { return x; }; identity(5);", 5),
            Sample("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20),
            Sample("fn(x) { x; }(5);", 5),
        )
        for (case in cases) {
            val v = testEval(case.input)
            testIntegerObject(v, case.expected)
        }
    }

    @Test
    fun testFunctionObject() {
        val input = "fn(x) {x+2;};"
        var v = testEval(input)
        assert(v is Function)
        v = v as Function
        assert(v.parameters.size == 1)
        assert(v.body.toString() == "(x + 2)")
    }

    @Test
    fun testLetStatement() {
        class Sample(val input: String, val expected: Long)

        val cases = listOf(
            Sample("let a = 10; a;", 10),
            Sample("let a = 2 * 5; a;", 10),
            Sample("let a = 77; let b = 2; let c = a * b; c;", 154),
            // Sample("let d = if (c > a) { 99 } else { 100 }; d;", 99),
        )
        for (case in cases) {
            val v = testEval(case.input)
            testIntegerObject(v, case.expected)
        }
    }

    @Test
    fun testErrorHanding() {
        class Sample(val input: String, val expected: String)

        val cases = listOf(
            Sample("5 + true;", "type mismatch: INTEGER + BOOLEAN"),
            Sample("5 + true; 5;", "type mismatch: INTEGER + BOOLEAN"),
            Sample("-true;", "unknown operator: -BOOLEAN"),
            Sample(
                """if (10 > 1) {
                                if (10 > 1) {
                                    return true + false;
                                }
                                return 1;
                            }""", "unknown operator: BOOLEAN + BOOLEAN"
            ),
            Sample("foobar;", "identifier not found: foobar"),
        )
        for (case in cases) {
            when (val value = testEval(case.input)) {
                is Error -> assert(value.message == case.expected)
            }
        }
    }

    @Test
    fun testReturnStatement() {
        class Sample(val input: String, val expected: Any?)

        val cases = listOf(
            Sample("return 1 + 1;", 2),
            Sample("return true;", true),
            Sample("1; return 10; 2;", 10),
            Sample(
                """if (10 > 1) {
                                if (10 > 1) {
                                    return 10;
                                }
                                return 1;
                            }""", 10
            ),
        )

        for (case in cases) {
            when (val value = testEval(case.input)) {
                is Integer -> testIntegerObject(value, (case.expected as Int).toLong())
                is bekyiu.`object`.Boolean -> testBooleanObject(value, case.expected as Boolean)
            }
        }
    }

    @Test
    fun testIfExpression() {
        class Sample(val input: String, val expected: Any?)

        val cases = listOf(
            Sample("if (true) {10}", 10),
            Sample("if (false) {10}", null),
            Sample("if (1 < 2) {10} else {20}", 10),
            Sample("if (1 > 2) {10} else {20}", 20),
        )

        for (case in cases) {
            when (val value = testEval(case.input)) {
                is Integer -> testIntegerObject(value, (case.expected as Int).toLong())
                else -> testNullObject(value)
            }
        }

    }

    fun testNullObject(obj: Object): Boolean {
        println("====")
        if (obj != Null.NULL) {
            System.err.println("obj不是null")
            return false
        }
        return true
    }

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
            Sample("1 > 2", false),
            Sample("1 < 2", true),
            Sample("1 == 2", false),
            Sample("1 != 2", true),
            Sample("(1 != 2) == true", true),
            Sample("(1 < 2) != true", false),
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
            Sample("1 + 1 - 5", -3),
            Sample("-1 + 1 - 5", -5),
            Sample("-1 + (1 - 5) * 3", -13),
            Sample("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50),
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
        val env = Environment()
        return e.eval(program, env) ?: Null.NULL
    }

    fun testIntegerObject(obj: Object, expected: Long) {
        assert(obj is Integer)
        val int = obj as Integer
        assert(int.value == expected)
    }
}