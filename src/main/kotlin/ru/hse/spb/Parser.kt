package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStream
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

class Parser(val charStream: CharStream) {
    val lexer = ExpLexer(charStream)
    val parser = ExpParser(BufferedTokenStream(lexer))

    fun invoke() : Block {
        val visitor = Visitor()
        val file = parser.file()
        return file.accept(visitor) as Block
    }
}