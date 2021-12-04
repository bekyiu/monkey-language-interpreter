package bekyiu

import bekyiu.lexer.Lexer
import bekyiu.lexer.TokenType
import java.util.*

/**
 * @Date 2021/12/4 12:01 下午
 * @Created by bekyiu
 */
fun main() {
    repl()
}


fun repl() {
    val sc = Scanner(System.`in`)
    println(">> Monkey Language Interpreter V0.1")
    println(">> Press 'q' to exit")
    print(">> ")
    while (sc.hasNextLine()) {
        val input = sc.nextLine()
        if (input == "q") {
            break
        }

        val lexer = Lexer(input)
        do {
            val token = lexer.nextToken()
            println(token)
        } while (token.type != TokenType.EOF)
        print(">> ")
    }
}
