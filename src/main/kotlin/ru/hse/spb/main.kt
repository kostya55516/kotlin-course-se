package ru.hse.spb

import org.antlr.v4.runtime.CharStreams

fun getGreeting(): String {
    val words = mutableListOf<String>()
    words.add("Hello,")

    words.add("world!")

    return words.joinToString(separator = " ")
}

fun main(args: Array<String>) {
    val parser = Parser(CharStreams.fromString("(1 + 2) * 5 + 4"))
    val block = parser.invoke()
    println(block)
    val result = Interpreter(block).interpret()
    println(result)
}