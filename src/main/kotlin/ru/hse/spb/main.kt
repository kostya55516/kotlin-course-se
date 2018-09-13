package ru.hse.spb

import java.lang.StringBuilder
import java.util.*
import java.util.stream.Collectors

enum class BHtmlTag {
    Table, EndTable, TR, EndTR, TD, EndTD, NULL
}

fun toTagList(table: String): List<BHtmlTag> {
    val list = StringTokenizer(table, ">").toList()
    return list.map {
        val s = it.toString()
        when {
            s.endsWith("<td") -> BHtmlTag.TD
            s.endsWith("<tr") -> BHtmlTag.TR
            s.endsWith("<table") -> BHtmlTag.Table
            s.endsWith("</td") -> BHtmlTag.EndTD
            s.endsWith("</tr") -> BHtmlTag.EndTR
            s.endsWith("</table") -> BHtmlTag.EndTable
            else -> BHtmlTag.NULL
        }
    }
}

fun tableSize(tagIt: Iterator<BHtmlTag>, result: MutableList<Int>) {
    var size = 0
    var inTable = true
    while (inTable && tagIt.hasNext()) {
        val tag = tagIt.next()
        when (tag) {
            BHtmlTag.TR -> size += rowSize(tagIt, result)
            BHtmlTag.EndTable -> inTable = false
            else -> {
            }
        }
    }
    result.add(size)
}

fun rowSize(tagIt: Iterator<BHtmlTag>, result: MutableList<Int>): Int {
    var size = 0
    var inRow = true
    while (inRow && tagIt.hasNext()) {
        val tag = tagIt.next()
        when (tag) {
            BHtmlTag.TD -> size++
            BHtmlTag.Table -> tableSize(tagIt, result)
            BHtmlTag.EndTR -> inRow = false
            else -> {
            }
        }
    }
    return size
}

fun readTable(): String {
    val stringBuilder = StringBuilder()
    while (true) {
        stringBuilder.append(readLine() ?: break)
    }
    return stringBuilder.toString()
}


fun main(args: Array<String>) {
    val list = toTagList(readTable())
    val result = ArrayList<Int>()
    tableSize(list.iterator(), result)
    result.sort()
    println(result.joinToString(separator = " "))
}