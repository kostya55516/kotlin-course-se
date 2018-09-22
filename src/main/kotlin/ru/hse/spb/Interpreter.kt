package ru.hse.spb

import java.util.*
import kotlin.Exception
import kotlin.collections.HashMap

class Interpreter(private val block: Block) {

    var scope: Scope = Scope()
    init {
        scope.functions["println"] = {
            println(it.joinToString(" "))
            0
        }
    }

    fun interpret(): Int {
        return block.eval()
    }

    private fun Block.eval(): Int {
        val oldScope = scope
        scope = Scope(scope)
        var result: Int? = null
        statements.forEach {
            val res = it.eval()
            if (it is Return) {
                result?.apply { throw Exception("second return in block") }
                result = res
            }
        }
        scope = oldScope
        return result ?: 0
    }

    private fun Function.eval(): Int {
        scope.functions[name] = { values: List<Int> ->
            if (values.size != params.size) {
                throw Exception("wrong count of parameters for function $name")
            }
            val oldScope = scope
            scope = Scope(scope)
            params.zip(values).forEach { (k, v) ->
                scope.variables[k] = OptionalInt.of(v)
            }
            val result = body.eval()

            scope = oldScope
            result
        }
        return 0
    }

    private fun Variable.eval(): Int {
        scope.variables[name] = exp?.eval()?.let { OptionalInt.of(it) } ?: OptionalInt.empty()
        return 0
    }

    private fun While.eval(): Int {
        while (condition.eval() != 0) {
            body.eval()
        }
        return 0
    }

    private fun If.eval(): Int {
        if (condition.eval() != 0) {
            return ifBody.eval()
        }
        return elseBody?.eval() ?: 0
    }

    private fun Assignment.eval(): Int {
        scope.variables[name] = OptionalInt.of(value.eval())
        return 0
    }

    private fun FunctionCall.eval(): Int {
        return scope.functions[name](args.args.map { it.eval() })
    }

    private fun Identifier.eval(): Int {
        return scope.variables[name].orElseThrow { Exception("$name is not defined in this scope") }
    }

    private fun Literal.eval(): Int {
        return value
    }

    private fun Return.eval(): Int {
        return value.eval()
    }

    private fun BinaryExpression.eval(): Int {
        return operation.invoke(left.eval(), right.eval())
    }

    private fun Expression.eval(): Int {
        return when (this) {
            is BinaryExpression -> eval()
            is FunctionCall -> eval()
            is Identifier -> eval()
            is Literal -> eval()
            else -> throw Exception("unresolved expression")
        }
    }

    private fun Statement.eval(): Int {
        return when (this) {
            is Block -> eval()
            is Function -> eval()
            is Variable -> eval()
            is While -> eval()
            is If -> eval()
            is Assignment -> eval()
            is Return -> eval()
            is Expression -> eval()
            else -> throw Exception("unresolved statement")
        }
    }
}

class Scope(fromScope: Scope? = null) {
    var variables: Dictionary<OptionalInt> = Dictionary(fromScope?.variables)
    var functions: Dictionary<(List<Int>) -> Int> = Dictionary(fromScope?.functions)
}

class Dictionary<T>(private val parent: Dictionary<T>? = null) {
    private val map = HashMap<String, T?>()

    operator fun get(name: String): T {
        return map[name] ?: parent?.get(name) ?: throw Exception("$name is not defined in this scope")
    }

    operator fun set(name: String, value: T?) {
        map.put(name, value)?.also { throw Exception("$name is already defined in this scope") }
    }
}