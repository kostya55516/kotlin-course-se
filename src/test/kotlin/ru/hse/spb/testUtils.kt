package ru.hse.spb


fun blockWith(vararg statements: Statement): Block {
    return Block(statements.toList())
}

fun blockWith(x: Int): Block {
    return blockWith(Literal(x))
}

fun binary(a: Int, b: Int, op: BinaryExpression.Operation): BinaryExpression {
    return BinaryExpression(Literal(a), Literal(b), op)
}