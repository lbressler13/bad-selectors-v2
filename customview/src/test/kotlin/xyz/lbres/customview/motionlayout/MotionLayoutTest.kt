package xyz.lbres.customview.motionlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.R
import xyz.lbres.customview.movingview.MovingButton
import xyz.lbres.customview.testutils.createMockTypedArray
import xyz.lbres.kotlinutils.closedrange.rangeOfInt
import xyz.lbres.testutils.assertFailsWithMessage
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import xyz.lbres.customview.movingview.createMockContext as createMovingViewContext

@RunWith(AndroidJUnit4::class)
class MotionLayoutTest {
    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        // paused
        var layout = MotionLayout(createMockContext(true, 3))
        assertTrue(layout.paused)
        assertEquals(3, layout.motionInterval)

        // not paused
        layout = MotionLayout(createMockContext(false, 2))
        assertFalse(layout.paused)
        assertEquals(2, layout.motionInterval)

        // negative interval
        layout = MotionLayout(createMockContext(false, -2))
        assertFalse(layout.paused)
        assertEquals(0, layout.motionInterval)
    }

    @Test
    fun testAddView() {
        val params = ViewGroup.LayoutParams(100, 200)

        // moving view
        val views = getMovingViews(List(5) { false })

        val layout = MotionLayout(createMockContext())
        layout.addView(views[0])
        assertEquals(1, layout.childCount)

        layout.addView(views[1], params)
        assertEquals(2, layout.childCount)

        layout.addView(views[2], 0)
        assertEquals(3, layout.childCount)

        layout.addView(views[3], 100, 200)
        assertEquals(4, layout.childCount)

        layout.addView(views[4], 0, params)
        assertEquals(5, layout.childCount)

        // non-moving view
        val message = "Child of MotionLayout must implement MovingView"
        val view = getNonMovingViews(1)[0]
        assertFailsWithMessage<IllegalStateException>(message) {
            layout.addView(view)
            assertEquals(5, layout.childCount)
        }

        assertFailsWithMessage<IllegalStateException>(message) {
            layout.addView(view, params)
            assertEquals(5, layout.childCount)
        }

        assertFailsWithMessage<IllegalStateException>(message) {
            layout.addView(view, 0)
            assertEquals(5, layout.childCount)
        }

        assertFailsWithMessage<IllegalStateException>(message) {
            layout.addView(view, 100, 200)
            assertEquals(5, layout.childCount)
        }

        assertFailsWithMessage<IllegalStateException>(message) {
            layout.addView(view, 0, params)
            assertEquals(5, layout.childCount)
        }
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

    private fun getMovingViews(pausedValues: List<Boolean>): List<View> {
        return pausedValues.map {
            val context = createMovingViewContext(0, it, 1)
            MovingButton(context)
        }
    }

    private fun getNonMovingViews(size: Int): List<View> {
        val context: Context = spyk(ApplicationProvider.getApplicationContext())
        return rangeOfInt(size).map { View(context) }
    }

    private fun createMockContext(paused: Boolean = false, motionInterval: Int = 0): Context {
        val mockArray = createMockTypedArray(
            setOf(
                R.styleable.Movement_motionInterval,
                R.styleable.Movement_paused,
            ),
        )
        every { mockArray.getInt(R.styleable.Movement_motionInterval, any()) } returns motionInterval
        every { mockArray.getBoolean(R.styleable.Movement_paused, any()) } returns paused

        val context: Context = spyk(ApplicationProvider.getApplicationContext())
        every {
            context.obtainStyledAttributes(any<AttributeSet>(), R.styleable.Movement, any(), any())
        } returns mockArray
        return context
    }
}
