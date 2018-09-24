package ru.hse.spb

import ru.hse.spb.BinaryExpression.Operation.*
import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser

class Visitor : ExpBaseVisitor<SyntaxNode>() {
    override fun visitFile(ctx: ExpParser.FileContext): SyntaxNode {
        return ctx.body.accept(this)
    }

    override fun visitBlock(ctx: ExpParser.BlockContext?): SyntaxNode {
        return Block(ctx?.children?.map { it.accept(this) as Statement } ?: emptyList())
    }

    override fun visitStatement(ctx: ExpParser.StatementContext): SyntaxNode {
        return ctx.getChild(0).accept(this)
    }

    override fun visitFunction(ctx: ExpParser.FunctionContext): SyntaxNode {
        return Function(ctx.name.text,
                ctx.params.IDENTIFIER()?.map { it.text } ?: emptyList(),
                ctx.body.accept(this) as Block)
    }

    override fun visitVariable(ctx: ExpParser.VariableContext): SyntaxNode {
        return Variable(ctx.name.text, ctx.exp?.accept(this) as Expression?)
    }

    override fun visitWhileLoop(ctx: ExpParser.WhileLoopContext): SyntaxNode {
        return While(ctx.cond.accept(this) as Expression, ctx.body.accept(this) as Block)
    }

    override fun visitIfStatement(ctx: ExpParser.IfStatementContext): SyntaxNode {
        return If(ctx.cond.accept(this) as Expression,
                ctx.ifBody.accept(this) as Block,
                ctx.elseBody?.accept(this) as Block?)
    }

    override fun visitAssigment(ctx: ExpParser.AssigmentContext): SyntaxNode {
        return Assignment(ctx.name.text, ctx.value.accept(this) as Expression)
    }

    override fun visitReturnStatement(ctx: ExpParser.ReturnStatementContext): SyntaxNode {
        return Return(ctx.value.accept(this) as Expression)
    }

    override fun visitExpression(ctx: ExpParser.ExpressionContext): SyntaxNode {
        if (ctx.childCount == 1) {
            return ctx.getChild(0).accept(this)
        }
        val left = ctx.left.accept(this) as Expression
        val right = ctx.right.accept(this) as Expression
        val expressionCons = { op: BinaryExpression.Operation -> BinaryExpression(left, right, op) }

        return when (ctx.op.type) {
            ExpParser.PLUS -> expressionCons(PLUS)
            ExpParser.MINUS -> expressionCons(MINUS)
            ExpParser.MUL -> expressionCons(MUL)
            ExpParser.DIV -> expressionCons(DIV)
            ExpParser.MOD -> expressionCons(MOD)
            ExpParser.LE -> expressionCons(LE)
            ExpParser.GR -> expressionCons(GR)
            ExpParser.LEQ -> expressionCons(LEQ)
            ExpParser.GRQ -> expressionCons(GRQ)
            ExpParser.EQ -> expressionCons(EQ)
            ExpParser.NEQ -> expressionCons(NEQ)
            ExpParser.AND -> expressionCons(AND)
            ExpParser.OR -> expressionCons(OR)
            else -> throw Exception("Wrong operation")
        }
    }

    override fun visitIdentifier(ctx: ExpParser.IdentifierContext): SyntaxNode {
        return Identifier(ctx.IDENTIFIER().text)
    }

    override fun visitLiteral(ctx: ExpParser.LiteralContext): SyntaxNode {
        return Literal(ctx.LITERAL().text.toInt())
    }

    override fun visitBracedExpression(ctx: ExpParser.BracedExpressionContext): SyntaxNode {
        return ctx.exp.accept(this)
    }

    override fun visitFunctionCall(ctx: ExpParser.FunctionCallContext): SyntaxNode {
        return FunctionCall(ctx.name.text, ctx.args.accept(this) as Arguments)
    }

    override fun visitArguments(ctx: ExpParser.ArgumentsContext): SyntaxNode {
        return Arguments(ctx.expression()?.map { it.accept(this) as Expression } ?: emptyList())
    }
}