package xyz.lbres.customview.movingview

import android.content.Context
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.testutils.checkViewPosition
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
abstract class AbstractMovingViewTest<T> (private val createView: (Context) -> T) where T : MovingView, T : View {
    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        runNonContinuousInitTests(createView)
        runLinearInitTests(createView)
    }

    @Test
    fun testUpdateMotionType() {
        // TODO test position update too
        // start non cont
        var view = createView(createMockContext(0, true))
        view.forcePosition(100, 100, 5, 10)

        // same type
        view.updateMotionType(MovingView.MotionType.NONCONTINUOUS)
        checkViewPosition(view, Position(5, 10))
        assertTrue(view.paused)
        assertEquals(MovingView.MotionType.NONCONTINUOUS, view.motionType)

        // change type
        view.updateMotionType(MovingView.MotionType.LINEAR)
        checkViewPosition(view, Position(5, 10))
        assertTrue(view.paused)
        assertEquals(MovingView.MotionType.LINEAR, view.motionType)
        assertEquals(0, view.movementSize)

        // update with movement size
        view.updateMotionType(MovingView.MotionType.NONCONTINUOUS)
        view.updateMotionType(MovingView.MotionType.LINEAR, 10)
        assertTrue(view.paused)
        assertEquals(MovingView.MotionType.LINEAR, view.motionType)
        assertEquals(10, view.movementSize)

        // start linear
        view = createView(createMockContext(1, false, 5))
        view.forcePosition(100, 100, 5, 10)

        // update same type
        view.updateMotionType(MovingView.MotionType.LINEAR, 1)
        checkViewPosition(view, Position(5, 10))
        assertFalse(view.paused)
        assertEquals(MovingView.MotionType.LINEAR, view.motionType)
        assertEquals(5, view.movementSize)

        // change type
        view.updateMotionType(MovingView.MotionType.NONCONTINUOUS)
        checkViewPosition(view, Position(5, 10))
        assertFalse(view.paused)
        assertEquals(MovingView.MotionType.NONCONTINUOUS, view.motionType)

        // update resets movement size
        view.updateMotionType(MovingView.MotionType.LINEAR, 1)
        checkViewPosition(view, Position(5, 10))
        assertFalse(view.paused)
        assertEquals(MovingView.MotionType.LINEAR, view.motionType)
        assertEquals(1, view.movementSize)
    }

    @Test
    fun testUpdatePaused() {
        runNonContinuousUpdatePausedTests(createView)
        runLinearUpdatePausedTests(createView)
    }

    @Test
    fun testUpdateMovementSize() {
        // no noncontinuous equivalent
        runLinearUpdateMovementSizeTests(createView)
    }

    @Test
    fun testUpdatePosition() {
        runNonContinuousUpdatePositionTests(createView)
        runLinearUpdatePositionTests(createView)
    }

    @Test
    fun testForcePosition() {
        runNonContinuousForcePositionTests(createView)
        runLinearForcePositionTests(createView)
    }

    @Test
    fun testSetInitialPosition() {
        runNonContinuousForcePositionTests(createView)
        runLinearForcePositionTests(createView)
    }

    @Test
    fun testSetOnMoveListener() {
        runNonContinuousSetOnMoveListerTests(createView)
        runLinearSetOnMoveListenerTests(createView)
    }

    @Test
    fun testSetOnPauseChangedListener() {
        runNonContinuousSetOnPauseChangedListenerTests(createView)
        runLinearSetOnPauseChangedListenerTests(createView)
    }
}
