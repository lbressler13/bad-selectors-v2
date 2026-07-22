package xyz.lbres.customview.data

import com.ibm.icu.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class PositionTest {
    @Test
    fun testToIntPosition() {
        var doublePosition = Position(0.0, 0.0)
        assertEquals(Position(0, 0), doublePosition.toIntPosition())

        doublePosition = Position(0.5, -0.1)
        assertEquals(Position(0, 0), doublePosition.toIntPosition())

        doublePosition = Position(123.4123, 100001.5)
        assertEquals(Position(123, 100001), doublePosition.toIntPosition())

        val bytePosition = Position(15.toByte(), 2.toByte())
        assertEquals(Position(15, 2), bytePosition.toIntPosition())

        val bigPosition = Position(BigDecimal("10.1231231231231231"), BigDecimal("-987654.456789"))
        assertEquals(Position(10, -987654), bigPosition.toIntPosition())
    }
}
