package xyz.lbres.badselectorsv2.calculator.utils

import xyz.lbres.badselectorsv2.testutils.assertFailsWithMessage
import kotlin.test.Test
import kotlin.test.assertEquals

class RunComputationTest {
    @Test
    fun testInvalidText() {
        val expectedError = "Err: Syntax Error"

        // empty compute text
        var text: List<String> = emptyList()
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        // operator at start/end
        text = splitText("+")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("+1")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("1-")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("-1-")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("1+5/6x2-")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        // invalid character
        text = splitText("1+2-3^4")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("(1+2)")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("1 + 2")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("2.3")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        // repeat operators
        text = splitText("0++3")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("6+6-+3")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }
    }

    @Test
    fun testError() {
        var expectedError = "Err: Divide by 0"
        var text = splitText("1/0")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        text = splitText("5+12/0x3")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }

        expectedError = "Err: Overflow value"
        text = splitText(Long.MAX_VALUE.toString() + "x" + Long.MAX_VALUE.toString())
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text) }
    }

    @Test
    fun testValid() {
        // single number
        var text = splitText("0")
        var expected = 0
        assertEquals(expected, runComputation(text))

        text = splitText("1234")
        expected = 1234
        assertEquals(expected, runComputation(text))

        text = listOf("-15")
        expected = -15
        assertEquals(expected, runComputation(text))

        // single op
        text = splitText("12+18")
        expected = 30
        assertEquals(expected, runComputation(text))

        text = splitText("100-4")
        expected = 96
        assertEquals(expected, runComputation(text))

        text = listOf("-25") + splitText("x12")
        expected = -300
        assertEquals(expected, runComputation(text))

        text = splitText("18/3")
        expected = 6
        assertEquals(expected, runComputation(text))

        text = splitText("18/4")
        expected = 4
        assertEquals(expected, runComputation(text))

        // multiple ops
        text = splitText("12+7+4+1200")
        expected = 1223
        assertEquals(expected, runComputation(text))

        text = splitText("100-6+4")
        expected = 98
        assertEquals(expected, runComputation(text))

        text = splitText("5+8/2")
        expected = 9
        assertEquals(expected, runComputation(text))

        text = splitText("5+9/2")
        expected = 9
        assertEquals(expected, runComputation(text))

        text = splitText("9/2+5")
        expected = 9
        assertEquals(expected, runComputation(text))

        text = listOf("-5") + splitText("x4-60+5/2")
        expected = -78
        assertEquals(expected, runComputation(text))

        // big number
        val bigNumber = Int.MAX_VALUE - 10
        text = splitText("$bigNumber+10")
        expected = Int.MAX_VALUE
        assertEquals(expected, runComputation(text))
    }

    @Test
    fun testNonMultiDigit() {
        var text = splitText("1")
        var expected = 1
        assertEquals(expected, runComputation(text))

        text = splitText("1/2")
        expected = 0
        assertEquals(expected, runComputation(text))

        text = listOf("15") + splitText("-6/2+1")
        expected = 13
        assertEquals(expected, runComputation(text))

        // error
        val expectedError = "Err: Syntax Error"
        text = splitText("11")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text, false) }

        text = splitText("1+10")
        assertFailsWithMessage<Exception>(expectedError) { runComputation(text, false) }
    }

    private fun splitText(text: String): List<String> = text.toList().map(Char::toString)
}
