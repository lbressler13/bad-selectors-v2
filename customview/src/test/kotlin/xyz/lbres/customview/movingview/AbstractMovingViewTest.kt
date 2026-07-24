package xyz.lbres.customview.movingview

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
abstract class AbstractMovingViewTest(private val createView: (Context) -> MovingView) {
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
        // TODO
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
