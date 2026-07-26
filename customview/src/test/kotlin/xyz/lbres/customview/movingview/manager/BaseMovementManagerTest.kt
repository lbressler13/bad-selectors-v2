package xyz.lbres.customview.movingview.manager

import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.movingview.MovingView
import xyz.lbres.customview.testutils.checkPositionHistory
import xyz.lbres.customview.testutils.withMockedDegrees
import xyz.lbres.customview.testutils.withMockedNextDouble
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BaseMovementManagerTest {
    private val parentWidth = 10.0
    private val parentHeight = 15.0
    private val parentDimens = Dimensions(parentWidth.toInt(), parentHeight.toInt())

    private val initialPosition = Position(5.0, 4.0)

    @Test
    fun testCreateNonContinuous() {
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
        var previous: BaseMovementManager = NonContinuousMovementManager(true)
        previous.updatePosition(parentDimens, forcedPosition = initialPosition)
        val (pausedHistory, positionHistory) = addListeners(previous)

        val positions = listOf(Position(1.0, 3.0), Position(0.6, 12.0))
        withMockedNextDouble(parentWidth, parentHeight, positions) {
            manager = BaseMovementManager.create(motionType, false, previous = previous)
            checkManagerState<NonContinuousMovementManager>(manager, true)

            manager.paused = false
            manager.updatePosition(parentDimens)
            manager.updatePosition(parentDimens)
            manager.paused = true
            assertEquals(listOf(false, true), pausedHistory)
            checkPositionHistory(positions, positionHistory)

            previous = LinearMovementManager(false, 5)
            previous.updatePosition(parentDimens, forcedPosition = Position(0.0, 9.9))
            manager = BaseMovementManager.create(motionType, true, previous = previous)
            checkManagerState<NonContinuousMovementManager>(manager, false, Position(0.0, 9.9))
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
        previous.updatePosition(parentDimens, forcedPosition = initialPosition)
        manager = BaseMovementManager.create(motionType, false, 1, previous)

        checkManagerState<LinearMovementManager>(manager, true)
        manager as LinearMovementManager
        assertEquals(10, manager.movementSize) // overrides movement size parameter

        previous = NonContinuousMovementManager(false)
        previous.updatePosition(parentDimens, forcedPosition = initialPosition)
        val (pausedHistory, positionHistory) = addListeners(previous)

        withMockedDegrees(listOf(90)) {
            manager = BaseMovementManager.create(motionType, true, 5, previous)

            checkManagerState<LinearMovementManager>(manager, false)
            assertEquals(5, (manager as LinearMovementManager).movementSize)

            manager.paused = true
            manager.updatePosition(parentDimens, forceUpdate = true)
            manager.updatePosition(parentDimens, forceUpdate = true)
            manager.paused = false
            assertEquals(listOf(true, false), pausedHistory)
            checkPositionHistory(listOf(Position(5, 9), Position(5, 14)), positionHistory)
        }
    }

    // check manager type, paused status, and position
    private inline fun <reified T> checkManagerState(
        manager: BaseMovementManager,
        paused: Boolean,
        position: Position<Double> = initialPosition,
    ) {
        assertIs<T>(manager)
        assertEquals(paused, manager.paused)
        assertEquals(position.x, manager.x)
        assertEquals(position.y, manager.y)
    }

    // add on move and on pause changed callbacks, and return paused history and position history lists
    private fun addListeners(manager: BaseMovementManager): Pair<List<Boolean>, List<Position<Int>>> {
        val positionHistory: MutableList<Position<Int>> = mutableListOf()
        val pausedHistory: MutableList<Boolean> = mutableListOf()

        manager.setOnMoveCallback { x, y -> positionHistory.add(Position(x, y)) }
        manager.setOnPauseChangedCallback { pausedHistory.add(it) }
        return Pair(pausedHistory, positionHistory)
    }
}
