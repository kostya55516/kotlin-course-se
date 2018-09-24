package ru.hse.spb

interface SyntaxNode
interface Statement : SyntaxNode
interface Expression : Statement

data class Arguments(val args: List<Expression>) : SyntaxNode

data class Block(val statements: List<Statement>) : Statement
data class Function(val name: String, val params: List<String>, val body: Block) : Statement
data class Variable(val name: String, val exp: Expression?) : Statement
data class While(val condition: Expression, val body: Block) : Statement
data class If(val condition: Expression, val ifBody: Block, val elseBody: Block?) : Statement
data class Assignment(val name: String, val value: Expression) : Statement
data class Return(val value: Expression) : Statement

data class FunctionCall(val name: String, val args: Arguments) : Expression
data class Identifier(val name: String) : Expression
data class Literal(val value: Int) : Expression
data class BinaryExpression(val left: Expression, val right: Expression, val operation: Operation) : Expression {
    enum class Operation(val func: (Int, Int) -> Int) {
        PLUS({ x, y -> x + y }),
        MINUS({ x, y -> x - y }),
        MUL({ x, y -> x * y }),
        DIV({ x, y -> x / y }),
        MOD({ x, y -> x % y }),
        LE({ x, y -> toInt(x < y) }),
        GR({ x, y -> toInt(x > y) }),
        LEQ({ x, y -> toInt(x <= y) }),
        GRQ({ x, y -> toInt(x >= y) }),
        EQ({ x, y -> toInt(x == y) }),
        NEQ({ x, y -> toInt(x != y) }),
        AND({ x, y -> toInt(x != 0 && y != 0) }),
        OR({ x, y -> toInt(x != 0 || y != 0) });

        operator fun invoke(x: Int, y: Int) = func(x, y)
    }
}

private fun toInt(b: Boolean) = if (b) 1 else 0


