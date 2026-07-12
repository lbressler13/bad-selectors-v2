package xyz.lbres.customview.movingview.manager

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.testutils.checkPositionHistory
import xyz.lbres.customview.testutils.withMockedNextDouble
import xyz.lbres.customview.utils.seededRandom
import xyz.lbres.testutils.mockLog
import kotlin.math.max
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContinuousLinearMovementManagerTest {
    private val parentWidth = 100.0
    private val parentHeight = 200.0
    private val parentDimens = Dimensions(parentWidth.toInt(), parentHeight.toInt())

    @BeforeTest
    fun setupTest() {
        mockLog()
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        var manager = ContinuousLinearMovementManager(true, 5)
        assertTrue(manager.paused)
        assertEquals(manager.movementSize, 5)
        checkManagerPosition(manager, Position(0.0, 0.0))

        manager = ContinuousLinearMovementManager(true, -12)
        assertTrue(manager.paused)
        assertEquals(manager.movementSize, -12)
        checkManagerPosition(manager, Position(0.0, 0.0))
    }

    @Test
    fun testUpdatePaused() {
        var manager = ContinuousLinearMovementManager(true, 5)
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
    fun testUpdateMovementSize() {
        // TODO
    }

    @Test
    fun testUpdatePosition() {
        // TODO
    }

    @Test
    fun testSetInitialPosition() {
        val positions = listOf(Position(1.0, 5.0), Position(10.0, 4.0))
        // withMockedDegrees(listOf(100)) {
            withMockedNextDouble(parentWidth, parentHeight, positions) {
                // not paused
                var manager = ContinuousLinearMovementManager(false, 10)
                manager.setInitialPosition(parentDimens)
                checkManagerPosition(manager, positions[0])

                // paused
                manager = ContinuousLinearMovementManager(true, 10)
                manager.setInitialPosition(parentDimens)
                checkManagerPosition(manager, positions[1])
//            }
        }
    }

    @Test
    fun testSetOnMoveCallback() {
        // TODO
    }

    @Test
    fun testSetOnPauseChangedCallback() {
        // TODO
    }

    private fun withMockedDegrees(mockDegrees: List<Int>, test: () -> Unit) {
        mockkStatic(IntRange::seededRandom)
        with(mockk<IntRange>()) {
            every { IntRange(0, 360).seededRandom() } returnsMany mockDegrees
            test()
        }
    }
}
