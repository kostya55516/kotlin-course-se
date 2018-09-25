package ru.hse.spb

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNull
import org.junit.Test
import ru.hse.spb.BinaryExpression.Operation.*

class InterpreterTest {
    private fun result(vararg statements: Statement): Int? {
        return Interpreter(Block(statements.toList())).result
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

    @Test
    fun variableTest() {
        val result = result(
                Variable("x", Literal(42)), Return(Identifier("x"))
        )
        assertThat(result, `is`(42))
    }


    @Test
    fun assignmentTest() {
        val result = result(
                Variable("x", null),
                Assignment("x", Literal(42)),
                Return(Identifier("x"))
        )
        assertThat(result, `is`(42))
    }

    @Test
    fun doubleAssignmentTest() {
        val result = result(
                Variable("x", null),
                Assignment("x", Literal(42)),
                Assignment("x", Literal(53)),
                Return(Identifier("x"))
        )
        assertThat(result, `is`(53))
    }

    @Test
    fun functionTest() {
        val result = result(
                Function("foo", emptyList(), blockWith(
                        Variable("x", binary(2, 3, PLUS)),
                        Return(Identifier("x"))
                )),
                Return(FunctionCall("foo"))
        )
        assertThat(result, `is`(5))
    }

    @Test
    fun functionArgsTest() {
        val result = result(
                Function("foo", listOf("a"), blockWith(
                        Variable("x", BinaryExpression(Identifier("a"), Identifier("a"), MUL)),
                        Return(Identifier("x"))
                )),
                Return(FunctionCall("foo", listOf(Literal(5))))
        )
        assertThat(result, `is`(25))
    }

    @Test
    fun functionParamScopeTest() {
        val result = result(
                Variable("a", Literal(15)),
                Function("foo", listOf("a"), blockWith(
                        Variable("x", BinaryExpression(Identifier("a"), Identifier("a"), MUL)),
                        Return(Identifier("x"))
                )),
                Return(BinaryExpression(
                        FunctionCall("foo", listOf(Literal(5))),
                        Identifier("a"),
                        PLUS
                ))
        )
        assertThat(result, `is`(40))
    }

    @Test
    fun varScopeTest() {
        val result = result(
                Variable("a", Literal(15)),
                Variable("x", Literal(20)),
                Function("foo", emptyList(), blockWith(
                        Variable("a", Literal(20)),
                        Return(BinaryExpression(Identifier("a"), Identifier("x"), EQ))
                )),
                Return(BinaryExpression(
                        FunctionCall("foo"),
                        BinaryExpression(
                                Identifier("a"),
                                Literal(15),
                                EQ
                        ),
                        AND
                ))
        )
        assertThat(result, not(0))
    }

    @Test
    fun funScopeTest() {
        val result = result(
                Function("foo", emptyList(), blockWith(
                        Function("foo", emptyList(), blockWith(
                                Return(Literal(9))
                        )),
                        Return(BinaryExpression(
                                FunctionCall("foo"),
                                Literal(7),
                                PLUS
                        ))
                )),
                Return(FunctionCall("foo"))
        )
        assertThat(result, `is`(16))
    }

    @Test
    fun whileTest() {
        val result = result(
                Variable("i", Literal(0)),
                Variable("x", Literal(0)),
                While(BinaryExpression(Identifier("i"), Literal(5), LE), blockWith(
                        Assignment("x", BinaryExpression(Identifier("x"), Identifier("i"), PLUS)),
                        Assignment("i", BinaryExpression(Identifier("i"), Literal(1), PLUS))
                )),
                Return(Identifier("x"))
        )
        assertThat(result, `is`(10))
    }
}