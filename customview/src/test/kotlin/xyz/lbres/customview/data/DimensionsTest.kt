package xyz.lbres.customview.data

import kotlin.test.Test
import kotlin.test.assertEquals

class DimensionsTest {
    @Test
    fun testInit() {
        var intDimensions = Dimensions(0, 0)
        assertEquals(0, intDimensions.min)
        assertEquals(0, intDimensions.max)

        intDimensions = Dimensions(50, 13)
        assertEquals(13, intDimensions.min)
        assertEquals(50, intDimensions.max)

        intDimensions = Dimensions(13, 50)
        assertEquals(13, intDimensions.min)
        assertEquals(50, intDimensions.max)

        var doubleDimensions = Dimensions(1.0, 1.0)
        assertEquals(1.0, doubleDimensions.min, 0.0)
        assertEquals(1.0, doubleDimensions.max, 0.0)

        doubleDimensions = Dimensions(1.1, 1.11)
        assertEquals(1.1, doubleDimensions.min, 0.0)
        assertEquals(1.11, doubleDimensions.max, 0.0)

        doubleDimensions = Dimensions(1.11, 1.1)
        assertEquals(1.1, doubleDimensions.min, 0.0)
        assertEquals(1.11, doubleDimensions.max, 0.0)
    }
}
