import java.io.PrintStream

@DslMarker
annotation class TexDsl

@TexDsl
interface TexElement {
    fun accept(visitor: TexVisitor)
}

private val pairToString: (Pair<String, String>) -> String = { "${it.first}=${it.second}" }

class CustomTag(val name: String, val options: List<String>) : TagWithBody() {
    constructor(name: String, options: Array<out Pair<String, String>>)
            : this(name, options.map(pairToString))

    override fun accept(visitor: TexVisitor) {
        visitor.visitCustomTag(this)
    }
}

class Frame(val frameTitle: String, val options: List<String>) : TagWithBody() {
    constructor(title: String, options: Array<out Pair<String, String>>)
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

    fun frame(frameTitle: String, vararg params: Pair<String, String>, init: Frame.() -> Unit) {
        initElement(Frame(frameTitle, params), init)
    }

    fun customTag(name: String, vararg params: String, init: CustomTag.() -> Unit) {
        initElement(CustomTag(name, params.toList()), init)
    }

    fun customTag(name: String, vararg params: Pair<String, String>, init: CustomTag.() -> Unit) {
        initElement(CustomTag(name, params), init)
    }

    fun itemize(init: Itemize.() -> Unit) = initElement(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initElement(Enumerate(), init)

    operator fun String.unaryPlus() {
        children += TexText(this)
    }
}

class Item(val option: String?) : TagWithBody() {
    override fun accept(visitor: TexVisitor) = visitor.visitItem(this)
}

class Document : TagWithBody() {
    var documentClass: String = "default"
    var documentOptions: Array<out String>? = null

    private val packages = ArrayList<Package>()

    fun usepackage(name: String, vararg options: String) {
        packages += Package(name, options.toList())
    }

    fun documentClass(name: String, vararg options: String) {
        documentClass = name
        documentOptions = options
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


fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}


fun main(args: Array<String>) {
    println(document {
        documentClass("lol")
        usepackage("lolpack", "lol2", "lol3")
        usepackage("tralala")
        itemize {
            item { +"lol" }
            item { +"papapa"
                frame("as", "asd") {
                }
            }
        }
        enumerate {
            item("lol") { +"ulyalya" }
        }
    })

    println()

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
