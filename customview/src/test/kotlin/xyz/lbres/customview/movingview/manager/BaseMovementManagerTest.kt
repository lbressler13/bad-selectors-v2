package xyz.lbres.customview.movingview.manager

import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.movingview.MovingView
import xyz.lbres.customview.testutils.checkPositionHistory
import xyz.lbres.customview.testutils.withMockedDegrees
import xyz.lbres.customview.testutils.withMockedNextDouble
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BaseMovementManagerTest {
    private val parentWidth = 10.0
    private val parentHeight = 15.0
    private val parentDimens = Dimensions(parentWidth.toInt(), parentHeight.toInt())

    @Test
    fun testCreateNonContinuous() {
        val positions = listOf(Position(1.0, 3.0), Position(0.6, 12.0))
        withMockedNextDouble(parentWidth, parentHeight, positions) {
            val motionType = MovingView.MotionType.NONCONTINUOUS
            // paused
            var manager = BaseMovementManager.create(motionType, true)
            assertIs<NonContinuousMovementManager>(manager)
            checkDefaultManagerState(manager, true)

            // unpaused
            manager = BaseMovementManager.create(motionType, false)
            assertIs<NonContinuousMovementManager>(manager)
            checkDefaultManagerState(manager, false)

            // with previous
            val positionHistory: MutableList<Position<Int>> = mutableListOf()
            val pausedHistory: MutableList<Boolean> = mutableListOf()
            var previous: BaseMovementManager = NonContinuousMovementManager(true)
            previous.updatePosition(parentDimens, forcedPosition = Position(5.0, 4.0))
            previous.setOnMoveCallback { x, y -> positionHistory.add(Position(x, y)) }
            previous.setOnPauseChangedCallback { pausedHistory.add(it) }

            manager = BaseMovementManager.create(motionType, false, previous = previous)
            assertIs<NonContinuousMovementManager>(manager)
            assertTrue(manager.paused) // overrides paused parameter
            assertEquals(5.0, manager.x)
            assertEquals(4.0, manager.y)

            manager.paused = false
            manager.updatePosition(parentDimens)
            manager.updatePosition(parentDimens)
            manager.paused = true
            assertEquals(listOf(false, true), pausedHistory)
            checkPositionHistory(positions, positionHistory)

            previous = LinearMovementManager(false, 5)
            previous.updatePosition(parentDimens, forcedPosition = Position(0.0, 9.9))
            manager = BaseMovementManager.create(motionType, true, previous = previous)
            assertIs<NonContinuousMovementManager>(manager)
            assertFalse(manager.paused) // overrides paused parameter
            assertEquals(0.0, manager.x)
            assertEquals(9.9, manager.y)
        }
    }

    @Test
    fun testCreateLinear() {
        val motionType = MovingView.MotionType.LINEAR
        // paused
        var manager = BaseMovementManager.create(motionType, true, 10)
        assertIs<LinearMovementManager>(manager)
        checkDefaultManagerState(manager, true)
        assertEquals(10, manager.movementSize)

        // unpaused
        manager = BaseMovementManager.create(motionType, false, 1)
        assertIs<LinearMovementManager>(manager)
        checkDefaultManagerState(manager, false)
        assertEquals(1, manager.movementSize)

        // missing movement size
        manager = BaseMovementManager.create(motionType, false)
        assertIs<LinearMovementManager>(manager)
        checkDefaultManagerState(manager, false)
        assertEquals(0, manager.movementSize)

        // with previous
        var previous: BaseMovementManager = LinearMovementManager(true, 10)
        previous.updatePosition(parentDimens, forcedPosition = Position(5.0, 4.0))
        manager = BaseMovementManager.create(motionType, false, 1, previous)

        assertIs<LinearMovementManager>(manager)
        assertTrue(manager.paused) // overrides paused parameter
        assertEquals(5.0, manager.x)
        assertEquals(4.0, manager.y)
        assertEquals(10, manager.movementSize) // overrides movement size parameter

        val positionHistory: MutableList<Position<Int>> = mutableListOf()
        val pausedHistory: MutableList<Boolean> = mutableListOf()
        previous = NonContinuousMovementManager(false)
        previous.updatePosition(parentDimens, forcedPosition = Position(5.0, 4.0))
        previous.setOnMoveCallback { x, y -> positionHistory.add(Position(x, y)) }
        previous.setOnPauseChangedCallback { pausedHistory.add(it) }

        withMockedDegrees(listOf(90)) {
            manager = BaseMovementManager.create(motionType, true, 5, previous)

            assertIs<LinearMovementManager>(manager)
            assertFalse(manager.paused) // overrides paused parameter
            assertEquals(5.0, manager.x)
            assertEquals(4.0, manager.y)
            assertEquals(5, (manager as LinearMovementManager).movementSize)

            manager.paused = true
            manager.updatePosition(Dimensions(50, 100), forceUpdate = true)
            manager.updatePosition(Dimensions(50, 100), forceUpdate = true)
            manager.paused = false
            assertEquals(listOf(true, false), pausedHistory)
            checkPositionHistory(listOf(Position(5, 9), Position(5, 14)), positionHistory)
        }
    }
}
