package xyz.lbres.customview.movingview.manager

import io.mockk.unmockkAll
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.movingview.checkPositionHistory
import xyz.lbres.customview.movingview.mockRandom
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
        mockRandom(parentWidth, parentHeight, positions)
        val manager = NonContinuousMovementManager(true)
        val history: MutableList<Position<Int>> = mutableListOf()
        manager.setOnMoveCallback { x, y -> history.add(Position(x, y)) }

        val updateAndCheck: (Int) -> Unit = {
            val result = manager.updatePosition(parentDimens)
            checkPosition(manager, positions[it])
            assertEquals(positions[it], result)
            checkPositionHistory(positions, history, it + 1)
        }

        // paused
        val result = manager.updatePosition(parentDimens)
        checkPosition(manager, 0.0, 0.0)
        assertEquals(Position(0.0, 0.0), result)

        // not paused
        manager.paused = false
        updateAndCheck(0)
        updateAndCheck(1)
        updateAndCheck(2)

        // re-paused
        manager.paused = true
        updateAndCheck(2)

        // unpaused
        manager.paused = false
        updateAndCheck(3)

        // repeat value
        updateAndCheck(3)
    }

    @Test
    fun testForcePosition() {
        val manager = NonContinuousMovementManager(false)
        val history: MutableList<Position<Int>> = mutableListOf()
        manager.setOnMoveCallback { x, y -> history.add(Position(x, y)) }

        val forceAndCheck: (Int) -> Unit = {
            val result = manager.forcePosition(parentDimens, positions[it])
            checkPosition(manager, positions[it])
            // TODO why does this return?
            assertTrue(result)
            checkPositionHistory(positions, history, it + 1)
        }

        // valid position
        forceAndCheck(0)
        forceAndCheck(1)

        // invalid position
        val invalidPositions = listOf(
            Position(1.0, 201.0),
            Position(100.1, 2.0),
            Position(-0.5, 2.0),
            Position(1.0, -2.0),
        )
        invalidPositions.forEach {
            runWithFailMessage("Checking invalid position $it") {
                val result = manager.forcePosition(parentDimens, it)
                checkPosition(manager, positions[1])
                assertFalse(result)
            }
        }

        // paused
        manager.paused = true
        forceAndCheck(2)

        // repeat
        forceAndCheck(2)
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
        mockRandom(parentWidth, parentHeight, mockPositions)
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
        repeat(3) { manager.forcePosition(parentDimens, mockPositions[it]) }
        assertEquals(6, total)

        // no error on null
        manager.setOnMoveCallback(null)
        manager.forcePosition(parentDimens, Position(0.0, 0.0))
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
