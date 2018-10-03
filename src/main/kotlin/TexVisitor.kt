interface TexVisitor {
    fun visitDocument(document: Document)
    fun visitEnumerate(enumerate: Enumerate)
    fun visitItemize(itemize: Itemize)
    fun visitItem(item: Item)
    fun visitPackage(pack: Package)
    fun visitTexElement(element: TexElement)
    fun visitTexText(texText: TexText)
}

class ToStringVisitor : TexVisitor {
    private val builder = StringBuilder()
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

    override fun visitDocument(document: Document) {
        addLine("\\documentClass" +
                (if (document.documentOptions?.isNotEmpty() == true) "${document.documentOptions}" else "") +
                "{${document.documentClass}}")

        document.acceptPackages(this)

        addLine("\n\\begin{document}")
        addSpaces()

        document.acceptChildren(this)

        removeSpaces()
        addLine("\\end{document}")
    }

    override fun visitEnumerate(enumerate: Enumerate) {
        addLine("\\begin{enumerate}")
        addSpaces()

        enumerate.acceptChildren(this)

        removeSpaces()
        addLine("\\end{enumerate}")
    }

    override fun visitItemize(itemize: Itemize) {
        addLine("\\begin{itemize}")
        addSpaces()

        itemize.acceptChildren(this)

        removeSpaces()
        addLine("\\end{itemize}")
    }

    override fun visitItem(item: Item) {
        addLine("\\item${item.option?.let {"[$it]"} ?: "" }")
        addSpaces()
        item.acceptChildren(this)
        removeSpaces()
    }

    override fun visitPackage(pack: Package) {
        addLine("\\usepackage${ if (pack.options.isNotEmpty()) "${pack.options}" else "" }{${pack.name}}")
    }


    override fun toString(): String {
        return builder.toString()
    }

    override fun visitTexElement(element: TexElement) {
        addLine("\\begin{texTag}")
    }

    override fun visitTexText(texText: TexText) {
        addLine(texText.text)
    }
}