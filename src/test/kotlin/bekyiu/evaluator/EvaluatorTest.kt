package bekyiu.evaluator

import bekyiu.`object`.*
import bekyiu.lexer.Lexer
import bekyiu.parser.Parser
import org.junit.Test

/**
 * @Date 2021/12/13 8:00 下午
 * @Created by bekyiu
 */
class EvaluatorTest {

    @Test
    fun testHashLiteral() {
        val input = """
            let two = "two";
            {
                "one": 10 - 9,
                two: 1 + 1,
                "thr" + "ee": 6 / 2,
                4: 4,
                true: 5,
                false: 6
            }
        """.trimIndent()

        var evaluated = testEval(input)
        evaluated = evaluated as _Hash

        testIntegerObject(evaluated.pairs[_String("one")]!!, 1)
        testIntegerObject(evaluated.pairs[_String("two")]!!, 2)
        testIntegerObject(evaluated.pairs[_String("three")]!!, 3)
        testIntegerObject(evaluated.pairs[_Integer(4)]!!, 4)
        testIntegerObject(evaluated.pairs[_Boolean.TRUE]!!, 5)
        testIntegerObject(evaluated.pairs[_Boolean.FALSE]!!, 6)
    }

    @Test
    fun testIndexExpression() {
        class Sample(val input: String, val expected: Long)

        val cases = listOf(
            Sample("[1][0]", 1),
            Sample("[1, 2, 3][2];", 3),
            Sample("let i = 0; [7][i]", 7),
            Sample("let arr = [1, 2 + 2, 3]; arr[0 + 1];", 4),
            // return null
//            Sample("[1, 2, 3][3];", 0),
            // return null
//            Sample("[1, 2, 3][-1];", 0),

        )
        for (case in cases) {
            val v = testEval(case.input)
            // println(v)
            testIntegerObject(v, case.expected)
        }
    }

    @Test
    fun testArrayLiteral() {
        val input = """
            [1, 2 / 2, 2 + 2]
        """.trimIndent()

        var evaluated = testEval(input)
        evaluated = evaluated as _Array
        testIntegerObject(evaluated.elements[0], 1)
        testIntegerObject(evaluated.elements[1], 1)
        testIntegerObject(evaluated.elements[2], 4)
    }

    @Test
    fun testBuiltinFunctions() {
        class Sample(val input: String, val expected: Long)

        val cases = listOf(
            Sample("len(\"\");", 0),
            Sample("len(\"abc\");", 3),
            Sample("len(\" a b \");", 5),
            // error
            // Sample("len(1);", -1),
            Sample("let a = [1, 2, 3]; len(a);", 3),
            Sample("let a = []; len(a);", 0),
            Sample("let a = [999, 6]; first(a);", 999),
        )
        for (case in cases) {
            val v = testEval(case.input)
            testIntegerObject(v, case.expected)
        }
    }

    @Test
    fun testStringConcatenation() {
        val input = """
            "hello" + ", " + "world"
        """.trimIndent()

        var evaluated = testEval(input)
        evaluated = evaluated as _String
        assert(evaluated.value == "hello, world")
    }

    @Test
    fun testString() {
        val input = """
            "hello world"
        """.trimIndent()

        var evaluated = testEval(input)
        evaluated = evaluated as _String
        assert(evaluated.value == "hello world")
    }

    @Test
    fun testRecursive() {
        val input = """
            let counter = fn(x) {
                if (x > 800) {
                    return true;
                } else {
                    let foobar = 9999;
                    counter(x + 1);
                }
            };
            counter(0);
            """
        println(testEval(input))
    }

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
        assert(v is _Function)
        v = v as _Function
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
                is _Integer -> testIntegerObject(value, (case.expected as Int).toLong())
                is _Boolean -> testBooleanObject(value, case.expected as Boolean)
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
                is _Integer -> testIntegerObject(value, (case.expected as Int).toLong())
                else -> testNullObject(value)
            }
        }

    }

    fun testNullObject(obj: _Object): Boolean {
        println("====")
        if (obj != _Null.NULL) {
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

    fun testBooleanObject(obj: _Object, expected: Boolean) {
        assert(obj is bekyiu.`object`._Boolean)
        val bool = obj as bekyiu.`object`._Boolean
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

    fun testEval(input: String): _Object {
        val l = Lexer(input)
        val p = Parser(l)
        val program = p.parseProgram()
        val e = Evaluator()
        val env = Environment()
        return e.eval(program, env) ?: _Null.NULL
    }

    fun testIntegerObject(obj: _Object, expected: Long) {
        assert(obj is _Integer)
        val int = obj as _Integer
        assert(int.value == expected)
    }
}