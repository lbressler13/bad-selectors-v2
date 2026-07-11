package xyz.lbres.customview.movingview.utils

import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContinuousLinearHelpersTest {
    private val downAngles = (10..170).toSet()
    private val upAngles = (190..350).toSet()
    private val leftAngles = (100..260).toSet()
    private val rightAngles = (280..360).toSet() + (0..80).toSet()

    @Test
    fun testGetAllowedAngles() {
        var dimensions = Dimensions(100, 200)

        // all angles
        var position = Position(10.0, 50.3)
        assertEquals((0..360).toSet(), getAllowedAngles(position, dimensions))

        position = Position(99.6, 199.1)
        assertEquals((0..360).toSet(), getAllowedAngles(position, dimensions))

        // no left
        position = Position(0.0, 99.0)
        assertEquals(rightAngles, getAllowedAngles(position, dimensions))

        position = Position(-3.0, 99.0)
        assertEquals(rightAngles, getAllowedAngles(position, dimensions))

        // no right
        position = Position(100.0, 99.0)
        assertEquals(leftAngles, getAllowedAngles(position, dimensions))

        position = Position(100.4, 99.0)
        assertEquals(leftAngles, getAllowedAngles(position, dimensions))

        // no up
        position = Position(40.0, 0.0)
        assertEquals(downAngles, getAllowedAngles(position, dimensions))

        position = Position(40.0, -3.0)
        assertEquals(downAngles, getAllowedAngles(position, dimensions))

        // no down
        position = Position(40.0, 200.0)
        assertEquals(upAngles, getAllowedAngles(position, dimensions))

        position = Position(40.0, 203.0)
        assertEquals(upAngles, getAllowedAngles(position, dimensions))

        dimensions = Dimensions(20, 10)

        // top left
        position = Position(0.0, 0.0)
        assertEquals((10..80).toSet(), getAllowedAngles(position, dimensions))

        // bottom left
        position = Position(-1.0, 11.0)
        assertEquals((280..350).toSet(), getAllowedAngles(position, dimensions))

        // bottom right
        position = Position(20.0, 10.0)
        assertEquals((190..260).toSet(), getAllowedAngles(position, dimensions))

        // top left
        position = Position(20.0, 0.0)
        assertEquals((100..170).toSet(), getAllowedAngles(position, dimensions))
    }
}
