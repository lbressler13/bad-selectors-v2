package xyz.lbres.customview.movingview

// TODO update tests for linear

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
        runInitTests(create)
    }

    @Test
    fun testUpdatePaused() {
        runUpdatePausedTests(create)
    }

    @Test
    fun testUpdatePosition() {
        runUpdatePositionTests(create)
    }

    @Test
    fun testForcePosition() {
        runForcePositionTests(create)
    }

    @Test
    fun testSetInitialPosition() {
        runSetInitialPositionTests(create)
    }

    @Test
    fun testSetOnMoveListener() {
        runSetOnMoveListerTests(create)
    }

    @Test
    fun testSetOnPauseChangedListener() {
        runSetOnPauseChangedListenerTests(create)
    }
}
