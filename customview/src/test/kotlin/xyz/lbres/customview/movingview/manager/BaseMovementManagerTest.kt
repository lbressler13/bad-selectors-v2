package xyz.lbres.customview.movingview.manager

import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.movingview.MovingView
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BaseMovementManagerTest {
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
        previous.updatePosition(Dimensions(10, 10), forcedPosition = Position(5.0, 4.0))
        manager = BaseMovementManager.create(motionType, false, previous = previous)
        assertIs<NonContinuousMovementManager>(manager)
        assertTrue(manager.paused) // overrides paused parameter
        assertEquals(5.0, manager.x)
        assertEquals(4.0, manager.y)

        previous = LinearMovementManager(false, 5)
        previous.updatePosition(Dimensions(10, 10), forcedPosition = Position(0.0, 9.9))
        manager = BaseMovementManager.create(motionType, true, previous = previous)
        assertIs<NonContinuousMovementManager>(manager)
        assertFalse(manager.paused) // overrides paused parameter
        assertEquals(0.0, manager.x)
        assertEquals(9.9, manager.y)
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
        previous.updatePosition(Dimensions(10, 10), forcedPosition = Position(5.0, 4.0))
        manager = BaseMovementManager.create(motionType, false, 1, previous)

        assertIs<LinearMovementManager>(manager)
        assertTrue(manager.paused) // overrides paused parameter
        assertEquals(5.0, manager.x)
        assertEquals(4.0, manager.y)
        assertEquals(10, manager.movementSize) // overrides movement size parameter

        previous = NonContinuousMovementManager(false)
        previous.updatePosition(Dimensions(10, 10), forcedPosition = Position(5.0, 4.0))
        manager = BaseMovementManager.create(motionType, true, 5, previous)

        assertIs<LinearMovementManager>(manager)
        assertFalse(manager.paused) // overrides paused parameter
        assertEquals(5.0, manager.x)
        assertEquals(4.0, manager.y)
        assertEquals(5, manager.movementSize)
    }
}
