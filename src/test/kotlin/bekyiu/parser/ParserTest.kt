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
        val source = "let a = b;\n"
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