import java.io.OutputStream
import java.io.PrintStream

interface TexVisitor {
    fun visitDocument(document: Document)
    fun visitEnumerate(enumerate: Enumerate)
    fun visitItemize(itemize: Itemize)
    fun visitItem(item: Item)
    fun visitPackage(pack: Package)
    fun visitTexElement(element: TexElement)
    fun visitTexText(texText: TexText)
    fun visitFrame(frame: Frame)
    fun visitCustomTag(customTag: CustomTag)
}

abstract class PrintVisitor : TexVisitor {
    protected var spaces: String = ""

    protected fun addSpaces() {
        spaces += "    "
    }

    protected fun removeSpaces() {
        spaces = spaces.dropLast(4)
    }

    protected abstract fun addLine(s: String)

    private fun <T : TexElement> addScope(elem: TagWithChildren<T>, name: String, options: String = "") {
        addLine("\\begin{$name}$options")
        addSpaces()

        elem.acceptChildren(this)

        removeSpaces()
        addLine("\\end{$name}")
    }

    override fun visitDocument(document: Document) {
        addLine("\\documentClass" +
                (if (document.documentOptions?.isNotEmpty() == true) "${document.documentOptions}" else "") +
                "{${document.documentClass}}")

        document.acceptPackages(this)

        addScope(document, "document")
    }

    override fun visitEnumerate(enumerate: Enumerate) {
        addScope(enumerate, "enumerate")
    }

    override fun visitItemize(itemize: Itemize) {
        addScope(itemize, "itemize")
    }

    override fun visitItem(item: Item) {
        addLine("\\item${item.option?.let { "[$it]" } ?: ""}")
        addSpaces()
        item.acceptChildren(this)
        removeSpaces()
    }

    override fun visitPackage(pack: Package) {
        addLine("\\usepackage${if (pack.options.isNotEmpty()) "${pack.options}" else ""}{${pack.name}}")
    }

    override fun visitTexElement(element: TexElement) {
        addLine("\\begin{texTag}")
    }

    override fun visitTexText(texText: TexText) {
        addLine(texText.text)
    }

    override fun visitFrame(frame: Frame) {
        addScope(frame, "frame", (if (frame.options.isNotEmpty()) frame.options.toString() else "")
                + "{${frame.frameTitle}}")
    }

    override fun visitCustomTag(customTag: CustomTag) {
        addScope(customTag, customTag.name, if (customTag.options.isNotEmpty()) customTag.options.toString() else "")
    }
}

class ToStringVisitor : PrintVisitor() {
    private val builder = StringBuilder()
    override fun addLine(s: String) {
        builder.append("$spaces$s\n")
    }

    override fun toString(): String {
        return builder.toString()
    }
}

class ToOutputVisitor(private val printStream: PrintStream) : PrintVisitor() {
    override fun addLine(s: String) {
        printStream.println(spaces + s)
    }

}