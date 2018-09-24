package ru.hse.spb

import org.antlr.v4.runtime.CharStreams

fun main(args: Array<String>) {
    val parser = Parser(CharStreams.fromString(
            "" +
                    "fun a(y, z) {" +
                    "   println(z, 12, y)" +
                    "   println(y * y)" +
                    "   return y * z" +
                    "}" +
                    "var x = 4" +
                    "return a(x * 3, 5)"
    ))
    println(parser.block)
    val result = Interpreter(parser.block).interpret()
    println(result)
}