package xyz.lbres.badselectorsv2.calculator.common

import kotlin.test.Test
import kotlin.test.assertEquals

class CalcDataTest {
    @Test
    fun testWithText() {
        val text1 = listOf("1", "+", "5", "4", "-")
        val text2 = listOf("1", "+", "5", "4", "-", "7")

        var calcData = CalcData(emptyList(), null, null)
        var expected = CalcData(emptyList(), null, null)
        calcData = calcData.withText(emptyList())
        assertEquals(expected, calcData)

        expected = CalcData(text1, null, null)
        calcData = calcData.withText(text1)
        assertEquals(expected, calcData)

        calcData = CalcData(text1, 6, null)
        expected = CalcData(text2, 6, null)
        calcData = calcData.withText(text2)
        assertEquals(expected, calcData)

        calcData = CalcData(text1, null, "err")
        expected = CalcData(text2, null, "err")
        calcData = calcData.withText(text2)
        assertEquals(expected, calcData)

        calcData = CalcData(text1, 6, "err")
        expected = CalcData(text2, 6, "err")
        calcData = calcData.withText(text2)
        assertEquals(expected, calcData)
    }
}
