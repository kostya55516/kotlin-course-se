package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import ru.hse.spb.BinaryExpression.Operation.*

class ParserTest {

    private fun parserFromString(s: String): Parser {
        return Parser(CharStreams.fromString(s))
    }

    private fun blockWith(vararg statements: Statement): Block {
        return Block(statements.toList())
    }

    private fun blockWithInt(x: Int): Block {
        return blockWith(Literal(x))
    }

    @Test
    fun emptyInputTest() {
        val parser = parserFromString("")
        assertThat(parser.block, `is`(Block(emptyList())))
    }

    @Test
    fun emptyFunctionStatement() {
        val parser = parserFromString("fun foo() {}")
        assertThat(parser.block, `is`(blockWith(Function("foo", emptyList(), Block(emptyList())))))
    }

    @Test
    fun withOneArgumentFunctionStatement() {
        val parser = parserFromString("fun foo(x) {}")
        assertThat(parser.block, `is`(blockWith(Function("foo", listOf("x"), Block(emptyList())))))
    }

    @Test
    fun withArgumentsFunctionStatement() {
        val parser = parserFromString("fun foo(x, y, z) {}")
        assertThat(parser.block, `is`(blockWith(Function("foo", listOf("x", "y", "z"), Block(emptyList())))))
    }

    @Test
    fun emptyVariableTest() {
        val parser = parserFromString("var x")
        assertThat(parser.block, `is`(blockWith(Variable("x", null))))
    }


    @Test
    fun variableConstantTest() {
        val parser = parserFromString("var x = 0")
        assertThat(parser.block, `is`(blockWith(Variable("x", Literal(0)))))
    }

    @Test
    fun whileTest() {
        val parser = parserFromString("while (1) { 0 }")
        assertThat(parser.block, `is`(blockWith(While(
                Literal(1), blockWith(Literal(0)))
        )))
    }

    @Test
    fun ifTest() {
        val parser = parserFromString("if (1) {0}")
        assertThat(parser.block, `is`(blockWith(If(
                Literal(1), blockWithInt(0), null
        ))))
    }

    @Test
    fun ifElseTest() {
        val parser = parserFromString("if (1) {0} else {42}")
        assertThat(parser.block, `is`(blockWith(If(
                Literal(1), blockWithInt(0), blockWithInt(42)
        ))))
    }

    @Test
    fun assignmentTest() {
        val parser = parserFromString("x = 42")
        assertThat(parser.block, `is`(blockWith(Assignment("x", Literal(42)))))
    }

    @Test
    fun returnTest() {
        val parser = parserFromString("return 42")
        assertThat(parser.block, `is`(blockWith(Return(Literal(42)))))
    }

    @Test
    fun functionCallWithEmptyTest() {
        val parser = parserFromString("foo()")
        assertThat(parser.block, `is`(blockWith(FunctionCall("foo", Arguments(emptyList())))))
    }

    @Test
    fun functionCallWithArgumentsTest() {
        val parser = parserFromString("foo(x, 12, y)")
        assertThat(parser.block, `is`(blockWith(FunctionCall("foo",
                Arguments(listOf(Identifier("x"), Literal(12), Identifier("y")))
        ))))
    }

    @Test
    fun identifierTest() {
        val parser = parserFromString("some_name")
        assertThat(parser.block, `is`(blockWith(Identifier("some_name"))))
    }

    @Test
    fun literalTest() {
        val parser = parserFromString("42")
        assertThat(parser.block, `is`(blockWith(Literal(42))))
    }

    private fun binary(a: Int, b: Int, op: BinaryExpression.Operation): BinaryExpression {
        return BinaryExpression(Literal(a), Literal(b), op)
    }

    @Test
    fun simpleBinaryExpression() {
        var parser = parserFromString("1 + 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, PLUS))))

        parser = parserFromString("1 - 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, MINUS))))

        parser = parserFromString("1 * 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, MUL))))

        parser = parserFromString("1 / 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, DIV))))

        parser = parserFromString("1 % 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, MOD))))

        parser = parserFromString("1 < 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, LE))))

        parser = parserFromString("1 > 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, GR))))

        parser = parserFromString("1 <= 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, LEQ))))

        parser = parserFromString("1 >= 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, GRQ))))

        parser = parserFromString("1 == 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, EQ))))

        parser = parserFromString("1 != 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, NEQ))))

        parser = parserFromString("1 && 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, AND))))

        parser = parserFromString("1 || 2")
        assertThat(parser.block, `is`(blockWith(binary(1, 2, OR))))
    }

    @Test
    fun assocBinaryTest() {
        var parser = parserFromString("1 + 2 * 3")
        assertThat(parser.block, `is`(blockWith(BinaryExpression(
                Literal(1), binary(2, 3, MUL), PLUS
        ))))

        parser = parserFromString("1 / 2 - 3")
        assertThat(parser.block, `is`(blockWith(BinaryExpression(
                binary(1, 2, DIV), Literal(3), MINUS
        ))))

        parser = parserFromString("1 / 2 % 3")
        assertThat(parser.block, `is`(blockWith(BinaryExpression(
                binary(1, 2, DIV), Literal(3), MOD
        ))))
    }

    @Test
    fun correctBinaryPriority() {
        val parser = parserFromString("1 + 2 <= 3 || 1")
        assertThat(parser.block, `is`(blockWith(BinaryExpression(
                BinaryExpression(
                        binary(1, 2, PLUS), Literal(3), LEQ
                ), Literal(1), OR
        ))))
    }

    @Test
    fun largeBlockTest() {
        val parser = parserFromString("" +
                "var x = 2 > 8 " +
                "lol " +
                "1 + 2")
        assertThat(parser.block, `is`(blockWith(
                Variable("x", binary(2, 8, GR)),
                Identifier("lol"),
                binary(1, 2, PLUS)
        )))
    }

    @Test
    fun commentTest() {
        val parser = parserFromString("//lol")
        assertThat(parser.block, `is`(blockWith()))
    }

    @Test
    fun commentWithCodeTest() {
        val parser = parserFromString("//lol\n" +
                "42")
        assertThat(parser.block, `is`(blockWith(Literal(42))))
    }
}