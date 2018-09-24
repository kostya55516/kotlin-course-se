package ru.hse.spb

import org.antlr.v4.runtime.CharStreams

fun main(args: Array<String>) {
    val parser = Parser(CharStreams.fromString(
            "" +
                    "fun main() {" +
                    "   if (1) {return 42} else {return 33}" +
                    "}" +
                    "return main()"
    ))
    println(parser.result.value)
    val result = Interpreter(parser.result.value).result.value
    println(result)
}