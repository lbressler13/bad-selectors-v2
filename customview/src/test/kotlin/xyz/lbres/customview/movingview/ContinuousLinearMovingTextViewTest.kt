package xyz.lbres.customview.movingview

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class ContinuousLinearMovingTextViewTest {
    private val parentWidth = 100.0
    private val parentHeight = 200.0
    private val viewWidth = 10
    private val viewHeight = 5

    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        // TODO
    }

    @Test
    fun testUpdatePaused() {
        // TODO
    }

    @Test
    fun testUpdateMovementSize() {
        // TODO
    }

    @Test
    fun testUpdatePosition() {
        // TODO
    }

    @Test
    fun testForcePosition() {
        // TODO
    }

    @Test
    fun testSetInitialPosition() {
        // TODO
    }

    @Test
    fun testSetOnMoveListener() {
        // TODO
    }

    @Test
    fun testSetOnPauseChangedListener() {
        // TODO
    }
}
