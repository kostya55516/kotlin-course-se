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
        assertNull(result)
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
                "var x = 42 \n" +
                        "return x"
        )
        assertThat(result, `is`(42))
    }


    @Test
    fun assignmentTest() {
        val result = result(
                "var x\n" +
                        "x = 42\n" +
                        "return x"
        )
        assertThat(result, `is`(42))
    }

    @Test
    fun doubleAssignmentTest() {
        val result = result(
                "var x\n" +
                        "x = 42\n" +
                        "x = 53\n" +
                        "return x"
        )
        assertThat(result, `is`(53))
    }

    @Test
    fun functionTest() {
        val result = result(
                "fun foo() {\n" +
                        "   var x = 2 + 3\n" +
                        "   return x\n" +
                        "}\n" +
                        "return foo()\n"
        )
        assertThat(result, `is`(5))
    }

    @Test
    fun functionArgsTest() {
        val result = result(
                "fun foo(a) {\n" +
                        "   var x = a * a\n" +
                        "   return x\n" +
                "a = 15\n" +
                        "}\n" +
                        "return foo(5)"
        )
        assertThat(result, `is`(25))
    }

    @Test
    fun functionParamScopeTest() {
        val result = result(
                "var a = 15\n" +
                        "fun foo(a) {\n" +
                        "   var x = a * a\n" +
                        "   return x\n" +
                        "}\n" +
                        "return foo(5) + a"
        )
        assertThat(result, `is`(40))
    }

    @Test
    fun varScopeTest() {
        val result = result(
                "var a = 15\n" +
                        "var x = 20\n" +
                        "fun foo() {\n" +
                        "   var a = 20\n" +
                        "   return a == x\n" +
                        "}\n" +
                        "return foo() && (a == 15)"
        )
        assertThat(result, not(0))
    }

    @Test
    fun funScopeTest() {
        val result = result(
                        "fun foo() { " +
                        "   fun foo() { return 9 }\n" +
                        "   return foo() + 7\n" +
                        "}\n" +
                        "return foo()"
        )
        assertThat(result, `is`(16))
    }

    @Test
    fun whileTest() {
        val result = result(
                "var i = 0\n" +
                        "var x = 0\n" +
                        "while(i < 5) {\n" +
                        "   x = x + i\n" +
                        "   i = i + 1\n" +
                        "}\n" +
                        "return x\n"
        )
        assertThat(result, `is`(10))
    }
}