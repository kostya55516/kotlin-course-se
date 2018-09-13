package ru.hse.spb

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.anyOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import ru.hse.spb.BHtmlTag.*

class TestSource {
    private lateinit var list: List<BHtmlTag>

    @Before
    fun fillTagList() {
       list = listOf(Table, TR, TD, EndTD, TD,
               Table, TR, TD, EndTD, EndTR, EndTable,
               EndTD, EndTR, EndTable)
    }

    @Test
    fun toTagListTest() {
        assertThat(toTagList("<table><tr><td></td><td>" +
                "<table><tr><td></td></tr></table>" +
                "</td></tr></table>"),
                `is`(list)
        )
    }

    @Test
    fun rowSizeTest() {
        val it = list.iterator()
        it.next()
        val result = ArrayList<Int>()
        assertThat(rowSize(it, result), `is`(2))
        assertThat(result, `is`(listOf(1)))
    }

    @Test
    fun tableSizeTest() {
        val it = list.iterator()
        val result = ArrayList<Int>()
        tableSize(it, result)
        assertThat(result, anyOf(`is`(listOf(2, 1)), `is`(listOf(1, 2))))
    }

}