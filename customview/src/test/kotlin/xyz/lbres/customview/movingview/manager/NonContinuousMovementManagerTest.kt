package xyz.lbres.customview.movingview.manager

import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.testutils.checkPositionHistory
import xyz.lbres.customview.testutils.withMockedNextDouble
import xyz.lbres.customview.utils.createRandom
import xyz.lbres.testutils.mockLog
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
        Position(3.14, 15.0),
    )

    @BeforeTest
    fun setupTest() {
        mockLog()
        mockkStatic(::createRandom)
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
        val forcedPositions = listOf(Position(99.99, 0.05), Position(2.0, 65.2))
        val history: MutableList<Position<Int>> = mutableListOf()
        val expectedHistory: MutableList<Position<Double>> = mutableListOf()

        withMockedNextDouble(parentWidth, parentHeight, positions) {
            val manager = NonContinuousMovementManager(true)
            manager.setOnMoveCallback { x, y -> history.add(Position(x, y)) }

            // checks after position update
            fun validateUpdate(position: Position<Double>, addToHistory: Boolean) {
                checkManagerPosition(manager, position)
                if (addToHistory) {
                    expectedHistory.add(position)
                }
                checkPositionHistory(expectedHistory, history)
            }

            // non-forced update
            fun updateAndCheck(index: Int, addToHistory: Boolean = true) {
                manager.updatePosition(parentDimens)
                validateUpdate(positions[index], addToHistory)
            }

            // forced update
            fun forceAndCheck(index: Int, addToHistory: Boolean = true) {
                manager.updatePosition(parentDimens, forcedPositions[index])
                validateUpdate(forcedPositions[index], addToHistory)
            }

            // paused
            manager.updatePosition(parentDimens)
            checkManagerPosition(manager, Position(0.0, 0.0))

            // force update
            manager.updatePosition(parentDimens, forceUpdate = true)
            validateUpdate(positions[0], true)

            // not paused
            manager.paused = false
            updateAndCheck(1)
            updateAndCheck(2)
            updateAndCheck(3)

            // re-paused
            manager.paused = true
            updateAndCheck(3, addToHistory = false) // don't add because it's paused

            // forced while paused
            forceAndCheck(0)

            // unpaused
            manager.paused = false
            updateAndCheck(4)

            // repeat value
            updateAndCheck(4, addToHistory = false) // don't add because it's repeated

            // forced while unpaused
            forceAndCheck(1)

            // forced repeat value
            forceAndCheck(1, addToHistory = false) // don't add because it's repeated

            // TODO add forced position
        }
    }

    @Test
    fun testSetInitialPosition() {
        withMockedNextDouble(parentWidth, parentHeight, positions) {
            // not paused
            var manager = NonContinuousMovementManager(false)
            manager.setInitialPosition(parentDimens)
            checkManagerPosition(manager, positions[0])

            // paused
            manager = NonContinuousMovementManager(true)
            manager.setInitialPosition(parentDimens)
            checkManagerPosition(manager, positions[1])
        }
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

        withMockedNextDouble(parentWidth, parentHeight, mockPositions) {
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
