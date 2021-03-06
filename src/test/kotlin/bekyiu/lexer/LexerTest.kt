package bekyiu.lexer

import org.junit.Test


/**
 * @Date 2021/12/4 12:03 下午
 * @Created by bekyiu
 */
class LexerTest {
    @Test
    fun testLexer() {
        val source = """
        let five = 5;
        let ten = 10;
        let add = fn(x, y) {
            x + y;
        };
        let result = add(five, ten);
        !-/*5;
        5 < 10 > 5;

        if (5 < 10) {
            return true;
        } else {
            return false;
        }

        10 == 10;
        10 != 9;
        "nishino nanase";
        "a asuka ";
        "";
        [1, 2];
        {"foo": "bar", 1: 2, true: false};
    """

        val lexer = Lexer(source)

        do {
            val token = lexer.nextToken()
            println(token)
        } while (token.type != TokenType.EOF)
    }
}