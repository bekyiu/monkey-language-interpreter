package bekyiu.parser

import bekyiu.ast.*
import bekyiu.lexer.Lexer
import bekyiu.lexer.Token
import bekyiu.lexer.TokenType
import org.junit.Test

/**
 * @Date 2021/12/4 2:07 下午
 * @Created by bekyiu
 */
class ParserTest {

    @Test
    fun testCallExpression() {
//        val source = "add(2 + 2);"
//        val source = "add();"
//        val source = "add(2);"
        val source = "add(fn(x) {x + y}, 2 * 7);"
        val lexer = Lexer(source)
        val parser = Parser(lexer)
        val program = parser.parseProgram()
        assert(program.statements.size == 1)
        assert(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        val call = expressionStatement.expression as CallExpression
        println(call)
    }

    @Test
    fun testFunctionLiteral() {
//        val source = "fn (x, y) { x + y; }"
        val source = "fn (x) { x + y; }"
//        val source = "fn () { x + y; }"
        val lexer = Lexer(source)
        val parser = Parser(lexer)
        val program = parser.parseProgram()
        assert(program.statements.size == 1)
        assert(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        val fn = expressionStatement.expression as FunctionLiteral
        println(fn)
    }

    @Test
    fun testIfExpression() {
//        val source = "if (x < y) { x + 1; 10 }"
        val source = "if (x < y) { x } else { y }"
        val lexer = Lexer(source)
        val parser = Parser(lexer)
        val program = parser.parseProgram()
        assert(program.statements.size == 1)
        assert(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        val ifExp = expressionStatement.expression as IfExpression
        println(ifExp.condition)
        println(ifExp.consequence)
        ifExp.alternative?.let {
            println(it)
        }
    }

    @Test
    fun testOperatorPrecedence() {
        class PrecedenceTest(val input: String, val expected: String)

        val cases = listOf(
            PrecedenceTest("-a * b", "((-a) * b)"),
            PrecedenceTest("!-a", "(!(-a))"),
            PrecedenceTest("a + b - c", "((a + b) - c)"),
            PrecedenceTest("!false", "(!false)"),
            PrecedenceTest("false != true", "(false != true)"),
            PrecedenceTest("1 > 2 == false", "((1 > 2) == false)"),
            PrecedenceTest("a + b / c", "(a + (b / c))"),
            PrecedenceTest("a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"),
            PrecedenceTest("3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
            PrecedenceTest("(1 + 2) * 3", "((1 + 2) * 3)"),
            PrecedenceTest("-(1 + 2) * 3 / (7 + 7)", "(((-(1 + 2)) * 3) / (7 + 7))"),
        )

        for (case in cases) {
            val lexer = Lexer(case.input)
            val parser = Parser(lexer)
            val program = parser.parseProgram()
            println(program)
            assert(case.expected == program.toString())
        }
    }

    @Test
    fun testInfixExpression() {
        class InfixTest(val input: String, val left: Long, val op: String, val right: Long)

        val cases = listOf(
            InfixTest("5 + 5;", 5, "+", 5),
            InfixTest("5 / 5;", 5, "/", 5),
            InfixTest("5 > 5;", 5, ">", 5),
            InfixTest("5 == 5;", 5, "==", 5),
            InfixTest("5 != 5;", 5, "!=", 5),
        )

        for (case in cases) {
            val lexer = Lexer(case.input)
            val parser = Parser(lexer)
            val program = parser.parseProgram()
            assert(program.statements.size == 1)
            assert(program.statements[0] is ExpressionStatement)

            val expressionStatement = program.statements[0] as ExpressionStatement
            val exp = expressionStatement.expression as InfixExpression
            assert(exp.operator == case.op)
            val left = exp.left as IntegerLiteral
            assert(left.value == case.left)
            val right = exp.right as IntegerLiteral
            assert(right.value == case.right)
        }
    }

    @Test
    fun testPrefixExpressions() {
        class PrefixTest(val input: String, val operator: String, val integerValue: Long)

        val cases = listOf(
            PrefixTest("!5", "!", 5L),
            PrefixTest("-7", "-", 7L),
        )

        for (case in cases) {
            val lexer = Lexer(case.input)
            val parser = Parser(lexer)
            val program = parser.parseProgram()
            assert(program.statements.size == 1)
            assert(program.statements[0] is ExpressionStatement)
            val expressionStatement = program.statements[0] as ExpressionStatement
            val exp = expressionStatement.expression as PrefixExpression
            assert(exp.operator == case.operator)

            val right = exp.right as IntegerLiteral
            assert(right.value == case.integerValue)
        }

    }

    @Test
    fun testIntegerLiteralExpression() {
        val source = "7;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)
        val program = parser.parseProgram()
        assert(program.statements.size == 1)
        assert(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        val int = expressionStatement.expression as IntegerLiteral

        assert(int.value == 7L)
        assert(int.tokenLiteral() == "7")
    }

    @Test
    fun testIdentifierExpression() {
        val source = "foobar;"
        val lexer = Lexer(source)
        val parser = Parser(lexer)
        val program = parser.parseProgram()
        assert(program.statements.size == 1)
        assert(program.statements[0] is ExpressionStatement)

        val expressionStatement = program.statements[0] as ExpressionStatement
        val identifier = expressionStatement.expression as Identifier

        assert(identifier.value == "foobar")
        assert(identifier.tokenLiteral() == "foobar")
    }

    @Test
    fun testToString() {
        val source = "let a = b;"
        val statements = mutableListOf<Statement>()
        val letStatement = LetStatement(
            Token(TokenType.LET, "let"),
            Identifier(Token(TokenType.IDENT, "a"), "a"),
            Identifier(Token(TokenType.IDENT, "b"), "b"),
        )

        val program = Program(statements).apply {
            this.statements.add(letStatement)
        }
        assert(program.toString() == source)
    }

    @Test
    fun testLetStatements() {
        val code = """
            let x = 5;
            let y = 10;
            let foo = 982389238;
        """
        val lexer = Lexer(code)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assert(program.statements.size == 3)

        val expectedIdents = listOf("x", "y", "foo")

        for ((i, ident) in expectedIdents.withIndex()) {
            val stmt = program.statements[i]

            assert("let" == stmt.tokenLiteral())
            assert(stmt is LetStatement)
            val letStmt = stmt as LetStatement
            assert(ident == letStmt.name.value)
            assert(ident == letStmt.name.tokenLiteral())
        }
    }

    @Test
    fun testReturnStatements() {
        val code = """
            return 10;
            return 100;
            return 1023423;
        """
        val lexer = Lexer(code)
        val parser = Parser(lexer)

        val program = parser.parseProgram()

        assert(program.statements.size == 3)

        for (stmt in program.statements) {
            assert(stmt.tokenLiteral() == "return")
        }
    }

    @Test
    fun testParseError() {
        val code = """
            let x  5;
        """
        val lexer = Lexer(code)
        val parser = Parser(lexer)
        parser.parseProgram()
    }
}