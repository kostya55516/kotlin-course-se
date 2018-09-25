package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("wrong number of arguments")
        exitProcess(1)
    }
    try {
        val parser = Parser(CharStreams.fromPath(Paths.get(args[0])))
        exitProcess(Interpreter(parser.result).result)
    } catch (e: Throwable) {
        println(e.message)
    }
}