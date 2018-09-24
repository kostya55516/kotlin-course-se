package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStream
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

class Parser(charStream: CharStream) {
    val lexer = ExpLexer(charStream)
    val parser = ExpParser(BufferedTokenStream(lexer))
    val result = lazy { parser.file().accept(Visitor()) as Block }
}