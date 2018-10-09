package ru.hse.spb.nedikov.tex

import java.io.PrintStream

@DslMarker
annotation class TexDslMarker

@TexDslMarker
interface TexElement {
    fun accept(visitor: TexVisitor)
}

typealias PairParam = Pair<String, String>

private val pairToString: (PairParam) -> String = { "${it.first}=${it.second}" }

class CustomTag(val name: String, val options: List<String>) : TagWithBody() {
    constructor(name: String, vararg options: PairParam)
            : this(name, options.map(pairToString))

    override fun accept(visitor: TexVisitor) {
        visitor.visitCustomTag(this)
    }
}

class Frame(val frameTitle: String, val options: List<String>) : TagWithBody() {
    constructor(title: String, vararg options: PairParam)
            : this(title, options.map(pairToString))

    override fun accept(visitor: TexVisitor) {
        visitor.visitFrame(this)
    }
}

class TexText(val text: String) : TexElement {
    override fun accept(visitor: TexVisitor) = visitor.visitTexText(this)
}

abstract class TagWithItems : TagWithChildren<Item>() {
    fun item(option: String? = null, init: Item.() -> Unit) = initElement(Item(option), init)
}

class Itemize : TagWithItems() {
    override fun accept(visitor: TexVisitor) = visitor.visitItemize(this)
}

class Enumerate : TagWithItems() {
    override fun accept(visitor: TexVisitor) = visitor.visitEnumerate(this)
}

class Package(val name: String, val options: List<String>) : TexElement {
    override fun accept(visitor: TexVisitor) = visitor.visitPackage(this)
}

abstract class TagWithChildren<T : TexElement> : TexElement {
    protected val children = ArrayList<T>()

    protected fun <G : T> initElement(elem: G, init: G.() -> Unit): G {
        elem.init()
        children += elem
        return elem
    }

    fun acceptChildren(visitor: TexVisitor) = children.forEach { it.accept(visitor) }
}

abstract class TagWithBody : TagWithChildren<TexElement>() {
    fun frame(frameTitle: String, vararg params: String, init: Frame.() -> Unit) {
        initElement(Frame(frameTitle, params.toList()), init)
    }

    fun frame(frameTitle: String, param: PairParam, vararg params: PairParam, init: Frame.() -> Unit) {
        initElement(Frame(frameTitle, param, *params), init)
    }

    fun customTag(name: String, vararg params: String, init: CustomTag.() -> Unit) {
        initElement(CustomTag(name, params.toList()), init)
    }

    fun customTag(name: String, param: PairParam, vararg params: PairParam, init: CustomTag.() -> Unit) {
        initElement(CustomTag(name, param, *params), init)
    }

    fun itemize(init: Itemize.() -> Unit) = initElement(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initElement(Enumerate(), init)

    fun math(init: Math.() -> Unit) = initElement(Math(), init)

    fun align(param: String? = null, init: Align.() -> Unit) = initElement(Align(param), init)

    operator fun String.unaryPlus() {
        children += TexText(this)
    }
}

class Item(val option: String?) : TagWithBody() {
    override fun accept(visitor: TexVisitor) = visitor.visitItem(this)
}

class Document : TagWithBody() {
    var documentClass: String? = null
    var documentOptions: List<String>? = null

    private val packages = ArrayList<Package>()

    fun usepackage(name: String, vararg options: String) {
        packages += Package(name, options.toList())
    }

    fun documentClass(name: String, vararg options: String) {
        documentClass = name
        if (options.isNotEmpty()) {
            documentOptions = options.toList()
        }
    }

    fun acceptPackages(visitor: TexVisitor) = packages.forEach { it.accept(visitor) }

    override fun accept(visitor: TexVisitor) = visitor.visitDocument(this)

    override fun toString(): String {
        val visitor = ToStringVisitor()
        accept(visitor)
        return visitor.toString()
    }

    fun toOutputStream(printStream: PrintStream) {
        accept(ToOutputVisitor(printStream))
    }
}

abstract class TagWithTextChildren : TagWithChildren<TexText>() {
    operator fun String.unaryPlus() {
        children += TexText(this)
    }
}

class Math : TagWithTextChildren() {
    override fun accept(visitor: TexVisitor) = visitor.visitMath(this)
}

class Align(val parameter: String? = null) : TagWithTextChildren() {
    override fun accept(visitor: TexVisitor) = visitor.visitAlign(this)
}

fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}



fun main(args: Array<String>) {
    document {
        documentClass("beamer")
        usepackage("babel", "russian" /* varargs */)
        frame("frametitle", "arg1" to "arg2") {
            itemize {
                for (row in listOf(1, 2, 3)) {
                    item { +"$row text" }
                }
            }

            // begin{pyglist}[language=kotlin]...\end{pyglist}
            customTag("pyglist", "language" to "kotlin") {
                +"""
               |val a = 1
               |
            """
            }
        }
    }.toOutputStream(System.out)
}
