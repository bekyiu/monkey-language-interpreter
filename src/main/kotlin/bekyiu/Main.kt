package bekyiu

import bekyiu.evaluator.Environment
import bekyiu.evaluator.Evaluator
import bekyiu.lexer.Lexer
import bekyiu.lexer.TokenType
import bekyiu.parser.ParseException
import bekyiu.parser.Parser
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @Date 2021/12/4 12:01 下午
 * @Created by bekyiu
 */
fun main(args: Array<String>) {
//    repl()
    evalFromFile(args[0])
}

private fun evalFromFile(filename: String) {
    val file = File(filename)
    val inputStream = FileInputStream(file)
    val input = inputStream.readAllBytes().toString(StandardCharsets.UTF_8)
    val env = Environment()
    val lexer = Lexer(input)
    val parser = Parser(lexer)
    val program = parser.parseProgram()
    val evaluator = Evaluator()
    val result = evaluator.eval(program, env)
    println(result)
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

private fun repl() {
    val sc = Scanner(System.`in`)
    print(MONKEY_HELLO)
    val env = Environment()
    while (sc.hasNextLine()) {
        val input = sc.nextLine()
        if (input == "q") {
            break
        }

        val lexer = Lexer(input)
        val parser = Parser(lexer)
        try {
            val program = parser.parseProgram()
            val evaluator = Evaluator()
            val result = evaluator.eval(program, env)
            println(result)
        } catch (e: ParseException) {
            println(e.message)
        }
        print(">> ")
    }
}
