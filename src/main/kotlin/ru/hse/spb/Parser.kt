package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStream
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

class Parser(charStream: CharStream) {
    val lexer = ExpLexer(charStream)
    val parser = ExpParser(BufferedTokenStream(lexer))
    val block: Block

    init {
        val visitor = Visitor()
        block = parser.file().accept(visitor) as Block
    }
}