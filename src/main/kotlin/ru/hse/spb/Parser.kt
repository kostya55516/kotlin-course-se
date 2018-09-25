package ru.hse.spb

import org.antlr.v4.runtime.*
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import kotlin.Exception

class Parser(charStream: CharStream) {
    private val lexer = ExpLexer(charStream)
    private val parser = ExpParser(BufferedTokenStream(lexer))

    init {
        parser.addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPos: Int, msg: String?, e: RecognitionException?) {
                throw Exception("Parser error at $line:$charPos. Message: $msg")
            }
        })

        lexer.addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPos: Int, msg: String?, e: RecognitionException?) {
                throw Exception("Lexer error at $line:$charPos. Message: $msg")
            }
        })
    }

    val result by lazy { parser.file().accept(Visitor()) as Block }
}