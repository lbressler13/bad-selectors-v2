package xyz.lbres.customview.movingview.manager

import io.mockk.unmockkAll
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.movingview.checkPositionHistory
import xyz.lbres.customview.movingview.mockNextDouble
import xyz.lbres.customview.testutils.mockLog
import xyz.lbres.customview.testutils.runWithFailMessage
import kotlin.math.max
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NonContinuousMovementManagerTest {
    private val parentWidth = 100.0
    private val parentHeight = 200.0
    private val parentDimens = Dimensions(parentWidth.toInt(), parentHeight.toInt())

    private val positions = listOf(
        Position(1.0, 3.0),
        Position(0.6, 12.0),
        Position(100.0, 194.00000045),
        Position(0.0, 0.05),
    )

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
        var manager = NonContinuousMovementManager(true)
        assertTrue(manager.paused)
        checkManagerPosition(manager, Position(0.0, 0.0))

        manager = NonContinuousMovementManager(false)
        assertFalse(manager.paused)
        checkManagerPosition(manager, Position(0.0, 0.0))
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
        mockNextDouble(parentWidth, parentHeight, positions)
        val manager = NonContinuousMovementManager(true)
        val history: MutableList<Position<Int>> = mutableListOf()
        manager.setOnMoveCallback { x, y -> history.add(Position(x, y)) }

        val forcedPositions = listOf(Position(99.99, 0.05), Position(2.0, 65.2))
        // val expectedHistory = positions.subList(0, 2) + forcedPositions[0] + positions[3] + forcedPositions[1]
        val expectedHistory: MutableList<Position<Double>> = mutableListOf()

        fun updateAndCheck(index: Int, addToHistory: Boolean = true) {
            manager.updatePosition(parentDimens)
            checkManagerPosition(manager, positions[index])
            if (addToHistory) {
                expectedHistory.add(positions[index])
            }
            checkPositionHistory(expectedHistory, history)
        }
        fun forceAndCheck(index: Int, addToHistory: Boolean = true) {
            manager.updatePosition(parentDimens, forcedPositions[index])
            checkManagerPosition(manager, forcedPositions[index])
            if (addToHistory) {
                expectedHistory.add(forcedPositions[index])
            }
            checkPositionHistory(expectedHistory, history)
        }

        // paused
        manager.updatePosition(parentDimens)
        checkManagerPosition(manager, Position(0.0, 0.0))

        // not paused
        manager.paused = false
        updateAndCheck(0)
        updateAndCheck(1)
        updateAndCheck(2)

        // re-paused
        manager.paused = true
        updateAndCheck(2, addToHistory = false) // don't add because it's paused

        // forced while paused
        forceAndCheck(0)

        // unpaused
        manager.paused = false
        updateAndCheck(3)

        // repeat value
        updateAndCheck(3, addToHistory = false) // don't add because it's repeated

        // forced while unpaused
        forceAndCheck(1)

        // forced repeat value
        forceAndCheck(1, addToHistory = false) // don't add because it's repeated
    }

    @Test
    fun testSetOnMoveCallback() {
        val mockPositions = listOf(
            Position(1.0, 2.0),
            Position(3.0, 2.1),
            Position(0.7, 1.0),
            Position(0.7, 1.0), // repeat value
            Position(5.0, 1.0),
        )
        mockNextDouble(parentWidth, parentHeight, mockPositions)
        val manager = NonContinuousMovementManager(false)

        // regular movement
        var total = 0
        manager.setOnMoveCallback { x, y -> total += x * y }
        repeat(3) { manager.updatePosition(parentDimens) }
        assertEquals(8, total)

        // repeat value
        manager.updatePosition(parentDimens)
        assertEquals(8, total)

        // forced change
        total = 0
        manager.setOnMoveCallback { x, y -> total += max(x, y) }
        repeat(3) { manager.updatePosition(parentDimens, mockPositions[it]) }
        assertEquals(6, total)

        // no error on null
        manager.setOnMoveCallback(null)
        manager.updatePosition(parentDimens, Position(0.0, 0.0))

        manager.setOnMoveCallback(null)
        manager.updatePosition(parentDimens)
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
