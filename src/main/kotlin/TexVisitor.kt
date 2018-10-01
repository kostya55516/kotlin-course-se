interface TexVisitor {
    fun visit(document: Document)
    fun exit(document: Document)
    fun visit(enumerate: Enumerate)
    fun exit(enumerate: Enumerate)
    fun visit(itemize: Itemize)
    fun exit(itemize: Itemize)
    fun visit(item: Item)
    fun exit(item: Item)
    fun visit(pack: Package)
    fun exit(pack: Package)
    fun visit(element: TexElement)
    fun exit(element: TexElement)
    fun visit(texText: TexText)
    fun exit(texText: TexText)
}

class ToStringVisitor : TexVisitor {
    private val builder = StringBuilder()
    private var beginDocument: (() -> Unit)? = null
    private var spaces: String = ""

    private fun addSpaces() {
        spaces += "    "
    }

    private fun removeSpaces() {
        spaces = spaces.dropLast(4)
    }

    private fun addLine(s: String) {
        builder.append("$spaces$s\n")
    }

    override fun visit(document: Document) {
        addLine("\\documentClass" +
                (if (document.documentOptions?.isNotEmpty() == true) "${document.documentOptions}" else "") +
                "{${document.documentClass}}")

        beginDocument = {
            addLine("\n\\begin{document}")
            addSpaces()
            beginDocument = null
        }
    }

    override fun exit(document: Document) {
    }

    override fun visit(enumerate: Enumerate) {
        beginDocument?.invoke()
        addLine("\\begin{enumerate}")
        addSpaces()
    }

    override fun exit(enumerate: Enumerate) {
        removeSpaces()
        addLine("\\end{enumerate}")
    }

    override fun visit(itemize: Itemize) {
        beginDocument?.invoke()
        addLine("\\begin{itemize}")
        addSpaces()
    }

    override fun exit(itemize: Itemize) {
        removeSpaces()
        addLine("\\end{itemize}")
    }

    override fun visit(item: Item) {
        addLine("\\item")
        addSpaces()
    }

    override fun exit(item: Item) {
        removeSpaces()
    }

    override fun visit(pack: Package) {
        addLine("\\usepackage${ if (pack.options.isNotEmpty()) "${pack.options}" else "" }{${pack.name}}")
    }


    override fun exit(pack: Package) {
    }

    override fun toString(): String {
        beginDocument?.invoke()
        removeSpaces()
        addLine("\\end{document}")
        return builder.toString()
    }

    override fun visit(element: TexElement) {
        addLine("\\begin{texTag}")
    }

    override fun exit(element: TexElement) {
        addLine("\\end{texTag}")
    }

    override fun visit(texText: TexText) {
        addLine(texText.text)
    }

    override fun exit(texText: TexText) {
    }
}