package xyz.lbres.customview.motionlayout

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class MotionLayoutTest {
    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        // TODO
    }

    @Test
    fun testAddView() {
        // TODO
    }

    @Test
    fun testUpdatePaused() {
        // TODO
    }

    @Test
    fun testUpdateMotionInterval() {
        // TODO
    }

    @Test
    fun testIntervalComplete() {
        // TODO
    }

    @Test
    fun testRequestLayout() {
        // TODO
    }

    @Test
    fun testOnAttachedToWindow() {
        // TODO possibly?
    }

    @Test
    fun testOnDetachedFromWindow() {
        // TODO possibly?
    }
}
