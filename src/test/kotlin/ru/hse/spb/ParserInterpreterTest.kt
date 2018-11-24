package ru.hse.spb


import org.antlr.v4.runtime.CharStreams
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNull
import org.junit.Test

class ParserInterpreterTest {
    private fun result(prog: String): Int? {
        return Interpreter(Parser(CharStreams.fromString(prog)).result).result
    }

    @Test
    fun emptyProgramTest() {
        val result = result("")
        assertThat(result, `is`(0))
    }

    @Test
    fun ifStatementTest() {
        var result = result(
                "if (1) { return 42 } else { return 12 }"
        )
        assertThat(result, `is`(42))

        result = result(
                "if (0) { return 42 } else { return 33 }"
        )
        assertThat(result, `is`(33))
    }

    @Test
    fun variableTest() {
        val result = result(
                """|var x = 42 
                    |return x
                    |""".trimMargin()
        )
        assertThat(result, `is`(42))
    }


    @Test
    fun assignmentTest() {
        val result = result(
                """|var x
                    |x = 42
                    |return x
                    |""".trimMargin()
        )
        assertThat(result, `is`(42))
    }

    @Test
    fun doubleAssignmentTest() {
        val result = result(
                """|var x
                    |x = 42
                    |x = 53
                    |return x
                    |""".trimMargin()
        )
        assertThat(result, `is`(53))
    }

    @Test
    fun functionTest() {
        val result = result(
                """|fun foo() {
                    |   var x = 2 + 3
                    |   return x
                    |}
                    |return foo()
                    |""".trimMargin()
        )
        assertThat(result, `is`(5))
    }

    @Test
    fun functionArgsTest() {
        val result = result(
                """|fun foo(a) {
                    |   var x = a * a
                    |   return x
                    |   a = 15
                    |}
                    |return foo(5)
                    |""".trimMargin()
        )
        assertThat(result, `is`(25))
    }

    @Test
    fun functionParamScopeTest() {
        val result = result(
                """|var a = 15
                    |fun foo(a) {
                    |   var x = a * a
                    |   return x
                    |}
                    |return foo(5) + a
                    |""".trimMargin()
        )
        assertThat(result, `is`(40))
    }

    @Test
    fun varScopeTest() {
        val result = result(
                """|var a = 15
                    |var x = 20
                    |fun foo() {
                    |   var a = 20
                    |   return a == x
                    |}
                    |return foo() && (a == 15)
                    |""".trimMargin()
        )
        assertThat(result, not(0))
    }

    @Test
    fun funScopeTest() {
        val result = result(
                """|fun foo() {
                    |   fun foo() { return 9 }
                    |   return foo() + 7
                    |}
                    |return foo()
                    |""".trimMargin()
        )
        assertThat(result, `is`(16))
    }

    @Test
    fun whileTest() {
        val result = result(
                """|var i = 0
                    |var x = 0
                    |while(i < 5) {
                    |   x = x + i
                    |   i = i + 1
                    |}
                    |return x
                    |""".trimMargin()
        )
        assertThat(result, `is`(10))
    }
}