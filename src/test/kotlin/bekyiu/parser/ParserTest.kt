package bekyiu.parser

import bekyiu.ast.LetStatement
import bekyiu.lexer.Lexer
import org.junit.Assert.*
import org.junit.Test

/**
 * @Date 2021/12/4 2:07 下午
 * @Created by bekyiu
 */
class ParserTest {
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
    fun testParseError() {
        val code = """
            let x  5;
        """
        val lexer = Lexer(code)
        val parser = Parser(lexer)
        parser.parseProgram()
    }
}