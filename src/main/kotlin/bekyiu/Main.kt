package bekyiu

import bekyiu.lexer.Lexer
import bekyiu.lexer.TokenType
import bekyiu.parser.ParseException
import bekyiu.parser.Parser
import java.util.*

/**
 * @Date 2021/12/4 12:01 下午
 * @Created by bekyiu
 */
fun main() {
    repl()
}

const val MONKEY_HELLO = """
███╗   ███╗  ██████╗  ███╗   ██╗ ██╗  ██╗ ███████╗ ██╗   ██╗
████╗ ████║ ██╔═══██╗ ████╗  ██║ ██║ ██╔╝ ██╔════╝ ╚██╗ ██╔╝
██╔████╔██║ ██║   ██║ ██╔██╗ ██║ █████╔╝  █████╗    ╚████╔╝ 
██║╚██╔╝██║ ██║   ██║ ██║╚██╗██║ ██╔═██╗  ██╔══╝     ╚██╔╝  
██║ ╚═╝ ██║ ╚██████╔╝ ██║ ╚████║ ██║  ██╗ ███████╗    ██║   
>> Monkey Language Interpreter V0.1
>> Press 'q' to exit
>> """

fun repl() {
    val sc = Scanner(System.`in`)
    print(MONKEY_HELLO)
    while (sc.hasNextLine()) {
        val input = sc.nextLine()
        if (input == "q") {
            break
        }

        val lexer = Lexer(input)
        val parser = Parser(lexer)

        try {
            val program = parser.parseProgram()
            println(program)
        } catch (e: ParseException) {
            println(e.message)
        }
        print(">> ")
    }
}
