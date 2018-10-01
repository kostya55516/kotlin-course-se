@DslMarker
annotation class TexDsl

@TexDsl
interface TexElement {
    fun accept(visitor: TexVisitor)
}

class Frame : TexElement {
    override fun accept(visitor: TexVisitor) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class TexText(val text: String) : TexElement {
    override fun accept(visitor: TexVisitor) {
        visitor.visit(this)
        visitor.exit(this)
    }
}

open class Itemize : TexElement {
    protected val items = ArrayList<Item>()

    fun item(init: Item.() -> Unit): Item {
        val item = Item()
        item.init()
        items.add(item)
        return item
    }

    override fun accept(visitor: TexVisitor) {
        visitor.visit(this)
        for (item in items) {
            item.accept(visitor)
        }
        visitor.exit(this)
    }
}

class Enumerate : Itemize() {
    override fun accept(visitor: TexVisitor) {
        visitor.visit(this)
        for (item in items) {
            item.accept(visitor)
        }
        visitor.exit(this)
    }
}

data class Package(val name: String, val options: List<String>) : TexElement {
    override fun accept(visitor: TexVisitor) {
        visitor.visit(this)
        visitor.exit(this)
    }
}

abstract class TagWithBody: TexElement {
    val children = ArrayList<TexElement>()

    fun itemize(init: Itemize.() -> Unit): Itemize {
        val itemize = Itemize()
        itemize.init()
        children.add(itemize)
        return itemize
    }

    fun enumerate(init: Enumerate.() -> Unit): Enumerate {
        val enum = Enumerate()
        enum.init()
        children.add(enum)
        return enum
    }

    operator fun String.unaryPlus() {
        children.add(TexText(this))
    }

}

class Item : TagWithBody() {
    override fun accept(visitor: TexVisitor) {
        visitor.visit(this)
        for (child in children) {
            child.accept(visitor)
        }
        visitor.exit(this)
    }

}

class Document : TagWithBody() {
    var documentClass: String? = null
    var documentOptions: Array<out String>? = null

    val packages = ArrayList<Package>()

    fun usepackage(name: String, vararg options: String) {
        packages.add(Package(name, options.toList()))
    }

    fun documentClass(name: String, vararg options: String) {
        documentClass = name
        documentOptions = options
    }

    override fun accept(visitor: TexVisitor) {
        visitor.visit(this)
        for (pack in packages) {
            pack.accept(visitor)
        }
        for (child in children) {
            child.accept(visitor)
        }
        visitor.exit(this)
    }

    override fun toString(): String {
        val visitor = ToStringVisitor()
        accept(visitor)
        return visitor.toString()
    }
}


fun document(init:Document.() -> Unit) : Document {
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
            item { +"papapa" }
        }
        enumerate {
            item { +"ulyalya" }
        }
    })
}
