package xyz.lbres.customview.movingview

// TODO update tests for switching mode

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class MovingButtonTest {
    private val create: (Context) -> MovingView = { MovingButton(it) }

    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        runNonContinuousInitTests(create)
        runLinearInitTests(create)
    }

    @Test
    fun testUpdateMotionType() {
        // TODO
    }

    @Test
    fun testUpdatePaused() {
        runNonContinuousUpdatePausedTests(create)
        runLinearUpdatePausedTests(create)
    }

    @Test
    fun testUpdateMovementSize() {
        // no noncontinuous equivalent
        runLinearUpdateMovementSizeTests(create)
    }

    @Test
    fun testUpdatePosition() {
        runNonContinuousUpdatePositionTests(create)
        runLinearUpdatePositionTests(create)
    }

    @Test
    fun testForcePosition() {
        runNonContinuousForcePositionTests(create)
        runLinearForcePositionTests(create)
    }

    @Test
    fun testSetInitialPosition() {
        runNonContinuousForcePositionTests(create)
        runLinearForcePositionTests(create)
    }

    @Test
    fun testSetOnMoveListener() {
        runNonContinuousSetOnMoveListerTests(create)
        runLinearSetOnMoveListenerTests(create)
    }

    @Test
    fun testSetOnPauseChangedListener() {
        runNonContinuousSetOnPauseChangedListenerTests(create)
        runLinearSetOnPauseChangedListenerTests(create)
    }
}
