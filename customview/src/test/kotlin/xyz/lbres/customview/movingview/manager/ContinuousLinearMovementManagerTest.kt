package xyz.lbres.customview.movingview.manager

import io.mockk.unmockkAll
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.testutils.checkPositionHistory
import xyz.lbres.customview.testutils.mockkStaticIntRange
import xyz.lbres.customview.testutils.mockkStaticIntSet
import xyz.lbres.customview.testutils.mockkStaticRandom
import xyz.lbres.customview.testutils.withMockedDegrees
import xyz.lbres.customview.testutils.withMockedNextDouble
import xyz.lbres.testutils.mockLog
import kotlin.math.max
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val manager = ContinuousLinearMovementManager(true, 5)
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
        mockkStaticIntRange()
        mockkStaticIntSet()

        val history: MutableList<Position<Int>> = mutableListOf()
        val expectedHistory: MutableList<Position<Double>> = mutableListOf()

        withMockedDegrees(listOf(90)) {
            val manager = ContinuousLinearMovementManager(false, 10)
            assertEquals(10, manager.movementSize)
            manager.setOnMoveCallback { x, y -> history.add(Position(x, y)) }

            manager.updatePosition(parentDimens)
            expectedHistory.add(Position(0.0, 10.0))
            manager.updatePosition(parentDimens)
            expectedHistory.add(Position(0.0, 20.0))
            manager.updatePosition(parentDimens)
            expectedHistory.add(Position(0.0, 30.0))

            manager.movementSize = 2
            assertEquals(2, manager.movementSize)
            manager.updatePosition(parentDimens)
            expectedHistory.add(Position(0.0, 32.0))

            manager.movementSize = -1
            assertEquals(2, manager.movementSize)
            manager.updatePosition(parentDimens)
            expectedHistory.add(Position(0.0, 34.0))

            // do not add repeat position
            manager.movementSize = 0
            assertEquals(0, manager.movementSize)
            manager.updatePosition(parentDimens)

            checkPositionHistory(expectedHistory, history)
        }
    }

    @Test
    fun testUpdatePosition() {
        mockkStaticIntRange()
        mockkStaticIntSet()
        mockkStaticRandom()

        val history: MutableList<Position<Int>> = mutableListOf()
        val expectedHistory: MutableList<Position<Double>> = mutableListOf()

        val initialPosition = Position(40.0, 50.0)
        val angles = listOf(90, -45)

        withMockedDegrees(angles) {
            val manager = ContinuousLinearMovementManager(true, 5)
            manager.setOnMoveCallback { x, y -> history.add(Position(x, y)) }

            var dimens = parentDimens

            // checks after position update
            fun validateUpdate(position: Position<Double>, addToHistory: Boolean) {
                checkManagerPosition(manager, position)
                if (addToHistory) {
                    expectedHistory.add(position)
                }
                checkPositionHistory(expectedHistory, history)
            }

            // non-forced update
            fun updateAndCheck(position: Position<Double>, addToHistory: Boolean = true) {
                manager.updatePosition(dimens)
                validateUpdate(position, addToHistory)
            }

            // forced update
            fun forceAndCheck(position: Position<Double>, addToHistory: Boolean = true) {
                manager.updatePosition(dimens, forceUpdate = true)
                validateUpdate(position, addToHistory)
            }

            // forced update
            fun forcePositionAndCheck(position: Position<Double>, addToHistory: Boolean = true) {
                manager.updatePosition(dimens, position)
                validateUpdate(position, addToHistory)
            }

            // paused
            updateAndCheck(Position(0.0, 0.0), false)

            // forced position
            forcePositionAndCheck(initialPosition)

            // forced while paused
            forceAndCheck(Position(40.0, 55.0))
            forceAndCheck(Position(40.0, 60.0))

            // not paused
            manager.paused = false
            updateAndCheck(Position(40.0, 65.0))

            // reaching edge
            dimens = Dimensions(100, 70)
            updateAndCheck(Position(40.0, 70.0))
            updateAndCheck(Position(40.0, 75.0))

            updateAndCheck(Position(43.53, 71.4644))
            updateAndCheck(Position(47.07, 67.92))

            // re-paused
            manager.paused = true
            updateAndCheck(Position(47.07, 67.92), false)

            // forced while unpaused
            manager.paused = false
            forceAndCheck(Position(50.60, 64.39))

            // forced repeat value
            forcePositionAndCheck(Position(manager.x, manager.y), false)
        }
    }

    @Test
    fun testSetInitialPosition() {
        mockkStaticIntRange()
        mockkStaticIntSet()
        mockkStaticRandom()

        val positions = listOf(Position(1.0, 5.0), Position(10.0, 4.0))
        withMockedDegrees(listOf(100)) {
            withMockedNextDouble(parentWidth, parentHeight, positions) {
                // not paused
                var manager = ContinuousLinearMovementManager(false, 10)
                manager.setInitialPosition(parentDimens)
                checkManagerPosition(manager, positions[0])

                // paused
                manager = ContinuousLinearMovementManager(true, 10)
                manager.setInitialPosition(parentDimens)
                checkManagerPosition(manager, positions[1])
            }
        }
    }

    @Test
    fun testSetOnMoveCallback() {
        mockkStaticIntRange()
        mockkStaticIntSet()

        withMockedDegrees(listOf(90, -45)) {
            val manager = ContinuousLinearMovementManager(false, 12)

            // regular movement
            var total = 0
            manager.setOnMoveCallback { x, y -> total += max(x, y) }

            manager.updatePosition(parentDimens, forcedPosition = Position(60.0, 180.0))
            repeat(2) { manager.updatePosition(parentDimens) }
            assertEquals(576, total) // 180, 192, 204

            // repeat value
            manager.updatePosition(parentDimens, forcedPosition = Position(manager.x, manager.y))
            assertEquals(576, total)

            // new angle
            manager.updatePosition(parentDimens) // position is 68.485, 195.515
            assertEquals(771, total) // add 195

            // no error on null
            manager.setOnMoveCallback(null)
            manager.updatePosition(parentDimens, Position(0.0, 0.0))

            manager.setOnMoveCallback(null)
            manager.updatePosition(parentDimens)
        }
    }

    @Test
    fun testSetOnPauseChangedCallback() {
        val manager = ContinuousLinearMovementManager(false, 10)

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
