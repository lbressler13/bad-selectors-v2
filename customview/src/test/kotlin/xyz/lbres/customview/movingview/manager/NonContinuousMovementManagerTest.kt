package xyz.lbres.customview.movingview.manager

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.utils.createRandom
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NonContinuousMovementManagerTest {
    private val parentWidth = 100.0
    private val parentHeight = 200.0
    private val parentDimens = Dimensions(parentWidth.toInt(), parentHeight.toInt())

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        var manager = NonContinuousMovementManager(true)
        assertTrue(manager.paused)
        checkPosition(manager, 0.0, 0.0)

        manager = NonContinuousMovementManager(false)
        assertFalse(manager.paused)
        checkPosition(manager, 0.0, 0.0)
    }

    @Test
    fun testUpdatePaused() {
        val manager = NonContinuousMovementManager(true)
        val calls: MutableList<Boolean> = mutableListOf()
        manager.setOnPauseChangedCallback { calls.add(it) }

        // invoked on change
        manager.paused = false
        manager.paused = true
        var expected = listOf(false, true)
        assertEquals(expected, calls)

        manager.paused = false
        expected = listOf(false, true, false)
        assertEquals(expected, calls)

        // not invoked when it stays the same
        manager.paused = false
        expected = listOf(false, true, false)
        assertEquals(expected, calls)

        manager.paused = true
        manager.paused = true
        expected = listOf(false, true, false, true)
        assertEquals(expected, calls)

        // no error when null
        manager.setOnPauseChangedCallback(null)
        manager.paused = false
        manager.paused = true
    }

    @Test
    fun testUpdatePosition() {
        val positions = listOf(
            Position(1.0, 3.0),
            Position(0.6, 12.0),
            Position(100.0, 194.00000045),
            Position(0.0, 0.05),
            Position(0.12345, 0.54321),
        )

        mockkStatic(::createRandom)
        every { createRandom() } returns mockk<Random> {
            every { nextDouble(0.0, parentWidth) } returnsMany positions.map { it.x }
            every { nextDouble(0.0, parentHeight) } returnsMany positions.map { it.y }
        }
        val manager = NonContinuousMovementManager(true)

        // paused
        var result = manager.updatePosition(parentDimens)
        checkPosition(manager, 0.0, 0.0)
        assertEquals(Position(0.0, 0.0), result)

        // not paused
        manager.paused = false
        result = manager.updatePosition(parentDimens)
        checkPosition(manager, positions[0])
        assertEquals(positions[0], result)

        result = manager.updatePosition(parentDimens)
        checkPosition(manager, positions[1])
        assertEquals(positions[1], result)

        result = manager.updatePosition(parentDimens)
        checkPosition(manager, positions[2])
        assertEquals(positions[2], result)

        // re-paused
        manager.paused = true
        result = manager.updatePosition(parentDimens)
        checkPosition(manager, positions[2])
        assertEquals(positions[2], result)

        // unpaused
        manager.paused = false
        result = manager.updatePosition(parentDimens)
        checkPosition(manager, positions[3])
        assertEquals(positions[3], result)
    }

    @Test
    fun testForcePosition() {
        // valid position

        // invalid position

        // paused

        // TODO
    }

    @Test
    fun testSetOnMoveCallback() {
        // TODO
    }

    @Test
    fun testSetOnPauseChangedCallback() {
        val manager = NonContinuousMovementManager(false)

        var counter = 0
        manager.setOnPauseChangedCallback { counter++ }
        repeat(4) { manager.paused = !manager.paused }
        assertEquals(4, counter)

        manager.setOnPauseChangedCallback(null)
        repeat(4) { manager.paused = !manager.paused }
        assertEquals(4, counter)

        val calls: MutableList<Boolean> = mutableListOf()
        manager.setOnPauseChangedCallback { calls.add(it) }
        repeat(5) { manager.paused = !manager.paused }
        assertEquals(listOf(true, false, true, false, true), calls)
    }
}
