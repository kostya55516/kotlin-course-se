package ru.hse.spb

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class InterpreterTest {
    private fun result(vararg statements: Statement): Int {
        return Interpreter(Block(statements.toList())).result.value
    }

    private fun blockWith(vararg statements: Statement): Block {
        return Block(statements.toList())
    }

    @Test
    fun emptyProgramTest() {
        val result = result()
        assertThat(result, `is`(0))
    }

    @Test
    fun ifStatementTest() {
        var result = result(
                If(Literal(1), blockWith(Return(Literal(42))), blockWith(Return(Literal(12))))
        )
        assertThat(result, `is`(42))

        result = result(
                If(Literal(0), blockWith(Return(Literal(42))), blockWith(Return(Literal(33))))
        )
        assertThat(result, `is`(33))
    }

}