package xyz.lbres.customview.motionlayout

import android.view.View
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.testutils.checkViewPosition
import xyz.lbres.customview.testutils.setViewSize
import xyz.lbres.customview.testutils.withMockedDegrees
import xyz.lbres.customview.testutils.withMockedNextDouble
import xyz.lbres.customview.utils.random
import xyz.lbres.customview.utils.seededRandom
import xyz.lbres.testutils.assertFailsWithMessage
import kotlin.test.AfterTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
    @Ignore
    fun testUpdatePaused() {
        // TODO with shadow looper
    }

    @Test
    @Ignore
    fun testUpdateMotionInterval() {
        // TODO with shadow looper
    }

    @Test
    @Ignore
    fun testIntervalComplete() {
        // TODO with shadow looper
    }

    @Test
    @Ignore
    fun testForceUpdate() {
        mockkStatic(::random)
        mockkStatic(IntRange::seededRandom, Set<Int>::seededRandom)

        val width = 100
        val height = 200
        val positions = mutableListOf(Position(20, 50), Position(91, 191), Position(0, 5))
        val doublePositions = positions.map { Position(it.x.toDouble(), it.y.toDouble()) }
        val views: MutableList<View> = mutableListOf()
        withMockedNextDouble(width.toDouble(), height.toDouble(), doublePositions, mock = false) {
            withMockedDegrees(listOf(90, -90, 180), mock = false) {
                fun createView(paused: Boolean, motionSize: Int): View {
                    val view = getMovingView(1, paused, motionSize)
                    view as View
                    setViewSize(view, 10, 15)
                    view.setInitialPosition(width, height)
                    return view
                }

                views.add(createView(false, 1))
                views.add(createView(true, 3))
                views.add(createView(false, 2))
            }
        }
        logViewPositions(views)

        // paused
        val layout = createLayout(true, 10)
        views.forEach { layout.addView(it) }
        layout.forceUpdate()
        views.forEachIndexed { index, view -> checkViewPosition(view, positions[index]) }

        // unpaused
        layout.paused = false
        layout.forceUpdate()
        positions[0] = Position(20, 51)
        positions[2] = Position(0, 7)
        logViewPositions(views)
        views.forEachIndexed { index, view -> checkViewPosition(view, positions[index]) }

        // force child updates
        layout.paused = true
        layout.forceUpdate(forceChildUpdates = true)
        positions[0] = Position(20, 52)
        positions[1] = Position(91, 188)
        positions[2] = Position(0, 9)
        views.forEachIndexed { index, view -> checkViewPosition(view, positions[index]) }
    }

    @Test
    @Ignore
    fun testRequestLayout() {
        // TODO with shadow looper
    }

    @Test
    @Ignore
    fun testOnAttachedToWindow() {
        // TODO with shadow looper
    }

    @Test
    @Ignore
    fun testOnDetachedFromWindow() {
        // TODO possibly?
    }
}
