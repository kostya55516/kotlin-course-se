package ru.hse.spb.nedikov.tex

import org.junit.Assert.assertEquals
import org.junit.Test

class TexVisitorTest {
    @Test
    fun emptyDocumentTest() {
        val doc = document {
        }
        assertEquals(
                """|\begin{document}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun usePackageTest() {
        val doc = document {
            usepackage("pack1")
        }
        assertEquals(
                """|\usepackage{pack1}
                    |\begin{document}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun usePackageWithParamsTest() {
        val doc = document {
            usepackage("pack2", "a")
            usepackage("pack3", "a", "b", "c")
        }
        assertEquals(
                """\usepackage[a]{pack2}
                    |\usepackage[a, b, c]{pack3}
                    |\begin{document}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun documentClassTest() {
        val doc = document {
            documentClass("beamer")
        }
        assertEquals(
                """\documentClass{beamer}
                    |\begin{document}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun documentClassWithParamsTest() {
        val doc = document {
            documentClass("beamer", "1", "2", "baa")
        }
        assertEquals(
                """\documentClass[1, 2, baa]{beamer}
                    |\begin{document}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun emptyEnumerateTest() {
        val doc = document {
            enumerate {
            }
        }
        assertEquals(
                """\begin{document}
                    |    \begin{enumerate}
                    |    \end{enumerate}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun enumerateTest() {
        val doc = document {
            enumerate {
                item { }
                item("param") { }
            }
        }
        assertEquals(
                """\begin{document}
                    |    \begin{enumerate}
                    |        \item
                    |        \item[param]
                    |    \end{enumerate}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    private val bigBody: TagWithBody.() -> Unit = {
        enumerate {}
        itemize {}
        customTag("tag") {}
        frame("title") {}
        math {}
        align {}
        +"some text"
    }

    private fun emptyScope(name: String, spaces: String): String {
        return "$spaces\\begin{$name}\n" +
                "$spaces\\end{$name}\n"
    }

    private fun bigBodyString(ident: Int): String {
        val spaces: String = generateSequence { "    " }.take(ident).joinToString("")
        return emptyScope("enumerate", spaces) +
                emptyScope("itemize", spaces) +
                emptyScope("tag", spaces) +
                "$spaces\\begin{frame}{title}\n" +
                "$spaces\\end{frame}\n" +
                emptyScope("math", spaces) +
                emptyScope("align", spaces) +
                "${spaces}some text\n"
    }

    @Test
    fun tagWithBodyTest() {
        val body = ToStringVisitor().let {
            document(bigBody).acceptChildren(it)
            it.toString()
        }
        assertEquals(body, bigBodyString(0))
    }

    @Test
    fun itemTest() {
        val doc = document {
            enumerate {
                item(null, bigBody)
            }
        }
        assertEquals(
                """\begin{document}
                    |    \begin{enumerate}
                    |        \item
                    |""".trimMargin() +
                        bigBodyString(3) +
                        """
                    |    \end{enumerate}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun itemizeTest() {
        val doc = document {
            itemize {
                item { }
                item("param") { }
            }
        }
        assertEquals(
                """\begin{document}
                    |    \begin{itemize}
                    |        \item
                    |        \item[param]
                    |    \end{itemize}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun customTagTest() {
        val doc = document {
            customTag("name") {}
            customTag("name2", "lol", "mol") {}
            customTag("name3", "a" to "b", "c" to "d") {}
            customTag("name", init = bigBody)
        }
        assertEquals(
                """\begin{document}
                    |    \begin{name}
                    |    \end{name}
                    |    \begin{name2}[lol, mol]
                    |    \end{name2}
                    |    \begin{name3}[a=b, c=d]
                    |    \end{name3}
                    |    \begin{name}
                    |""".trimMargin() +
                        bigBodyString(2) +
                    """|    \end{name}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun frameTest() {
        val doc = document {
            frame("name") {}
            frame("name2", "lol", "mol") {}
            frame("name3", "a" to "b", "c" to "d") {}
            frame("name", init = bigBody)
        }
        assertEquals(
                """\begin{document}
                    |    \begin{frame}{name}
                    |    \end{frame}
                    |    \begin{frame}[lol, mol]{name2}
                    |    \end{frame}
                    |    \begin{frame}[a=b, c=d]{name3}
                    |    \end{frame}
                    |    \begin{frame}{name}
                    |""".trimMargin() +
                        bigBodyString(2) +
                        """|    \end{frame}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun mathTest() {
        val doc = document {
            math { +"some text" }
        }
        assertEquals(
                """\begin{document}
                    |    \begin{math}
                    |        some text
                    |    \end{math}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }

    @Test
    fun align() {
        val doc = document {
            align { +"some text" }
            align("param") { +"some text" }
        }
        assertEquals(
                """\begin{document}
                    |    \begin{align}
                    |        some text
                    |    \end{align}
                    |    \begin{align}[param]
                    |        some text
                    |    \end{align}
                    |\end{document}
                    |""".trimMargin(),
                doc.toString()
        )
    }
}