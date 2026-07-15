package xyz.lbres.customview.movingview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.R
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.testutils.checkPositionHistory
import xyz.lbres.customview.testutils.checkViewPosition
import xyz.lbres.customview.testutils.createMockTypedArray
import xyz.lbres.customview.testutils.withMockedDegrees
import xyz.lbres.customview.testutils.withMockedNextDouble
import xyz.lbres.testutils.runWithFailMessage
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        // paused
        var view = ContinuousLinearMovingTextView(createMockContext(true, 10))
        assertTrue(view.paused)
        assertEquals(10, view.movementSize)

        // not paused
        view = ContinuousLinearMovingTextView(createMockContext(false, -12))
        assertFalse(view.paused)
        assertEquals(0, view.movementSize)
    }

    @Test
    fun testUpdatePaused() {
        var view = ContinuousLinearMovingTextView(createMockContext(true, 5))
        val calls: MutableList<Boolean> = mutableListOf()
        view.setOnPauseChangedListener { view, paused ->
            calls.add(paused)
            view.isEnabled = paused
        }

        // invoked on change
        view.paused = false
        view.paused = true
        assertEquals(listOf(false, true), calls)
        assertTrue(view.isEnabled)

        view.paused = false
        assertEquals(listOf(false, true, false), calls)
        assertFalse(view.isEnabled)

        // not invoked when it stays the same
        view.paused = false
        assertEquals(listOf(false, true, false), calls)
        assertFalse(view.isEnabled)

        view.paused = true
        view.paused = true
        assertEquals(listOf(false, true, false, true), calls)
        assertTrue(view.isEnabled)

        // no error when null
        view.setOnPauseChangedListener(null)
        view.paused = false
        view.paused = true

        // start unpaused
        view = ContinuousLinearMovingTextView(createMockContext(false, 5))
        calls.clear()
        view.setOnPauseChangedListener { view, paused ->
            calls.add(paused)
            view.isClickable = paused
        }
        view.paused = true
        view.paused = false
        assertEquals(listOf(true, false), calls)
        assertFalse(view.isClickable)
    }

    @Test
    fun testUpdateMovementSize() {
        withMockedDegrees(listOf(90)) {
            val view = ContinuousLinearMovingTextView(createMockContext(false, 10))
            assertEquals(10, view.movementSize)

            fun updateAndCheck(y: Double) {
                view.updatePosition(parentWidth.toInt(), parentHeight.toInt())
                checkViewPosition(view, Position(0.0, y))
            }

            updateAndCheck(10.0)
            updateAndCheck(20.0)
            updateAndCheck(30.0)

            view.movementSize = 2
            updateAndCheck(32.0)

            view.movementSize = -1
            assertEquals(2, view.movementSize)
            updateAndCheck(34.0)

            // repeat position
            view.movementSize = 0
            assertEquals(0, view.movementSize)
            updateAndCheck(34.0)
        }
    }

    @Test
    fun testUpdatePosition() {
        val history: MutableList<Position<Int>> = mutableListOf()
        val expectedHistory: MutableList<Position<Double>> = mutableListOf()

        val initialPosition = Position(40.0, 50.0)
        val angles = listOf(90, -45)

        withMockedDegrees(angles) {
            val view = ContinuousLinearMovingTextView(createMockContext(true, 5))
            view.setOnMoveListener { _, x, y -> history.add(Position(x, y)) }

            setViewPosition(view)
            val width = parentWidth.toInt() + viewWidth
            val height = parentHeight.toInt() + viewHeight

            // paused

            // force update

            // valid position

            // re-paused

            // unpaused

            // repeat value
        }
        // TODO
    }

    @Test
    fun testForcePosition() {
        val positions = listOf(
            Position(1.0, 3.0),
            Position(0.6, 12.0),
            Position(100.0, 194.00000045),
            Position(0.0, 0.05),
            Position(3.14, 15.0),
        )
        val view = ContinuousLinearMovingTextView(createMockContext(false, 5))
        val history: MutableList<Position<Int>> = mutableListOf()
        view.setOnMoveListener { _, x, y -> history.add(Position(x, y)) }

        setViewPosition(view)
        val width = parentWidth.toInt() + viewWidth
        val height = parentHeight.toInt() + viewHeight

        val forceAndCheck: (Int) -> Unit = {
            setViewPosition(view)
            val position = positions[it]
            view.forcePosition(width, height, position.x, position.y)
            checkViewPosition(view, positions[it])
            checkPositionHistory(positions.subList(0, it + 1), history)
        }

        // valid position
        forceAndCheck(0)
        forceAndCheck(1)

        // invalid position
        val invalidPositions = listOf(
            Position(1.0, 201.0),
            Position(100.1, 2.0),
            Position(-0.5, 2.0),
            Position(1.0, -2.0),
        )

        invalidPositions.forEach {
            runWithFailMessage("Checking invalid position $it") {
                view.forcePosition(width, height, it.x, it.y)
                checkViewPosition(view, positions[1])
                checkPositionHistory(positions.subList(0, 2), history)
            }
        }

        // paused
        view.paused = true
        forceAndCheck(2)

        // repeat
        forceAndCheck(2)
    }

    @Test
    fun testSetInitialPosition() {
        val positions = listOf(Position(1.0, 3.0), Position(0.6, 12.0))
        withMockedDegrees(listOf(90)) {
            withMockedNextDouble(parentWidth, parentHeight, positions) {
                // not paused
                var view = ContinuousLinearMovingTextView(createMockContext(false, 5))
                view.setInitialPosition(parentWidth.toInt(), parentHeight.toInt())
                checkViewPosition(view, positions[0])

                // paused
                view = ContinuousLinearMovingTextView(createMockContext(false, 5))
                view.setInitialPosition(parentWidth.toInt(), parentHeight.toInt())
                checkViewPosition(view, positions[1])
            }
        }
    }

    @Test
    fun testSetOnMoveListener() {
        // TODO
    }

    @Test
    fun testSetOnPauseChangedListener() {
        val view = ContinuousLinearMovingTextView(createMockContext(false, 10))

        // callback
        var counter = 0
        view.setOnPauseChangedListener { _, _ -> counter++ }
        repeat(4) { view.paused = !view.paused }
        assertEquals(4, counter)

        // object
        counter = 0
        view.setOnPauseChangedListener(object : MovingView.OnPausedChangedListener {
            override fun onChange(view: View, paused: Boolean) {
                counter++
            }
        })
        repeat(4) { view.paused = !view.paused }
        assertEquals(4, counter)

        // null
        counter = 0
        view.setOnPauseChangedListener(null)
        repeat(4) { view.paused = !view.paused }
        assertEquals(0, counter)
    }

    /**
     * Create mock context object which returns the given paused value in its attributes
     */
    private fun createMockContext(paused: Boolean, movementSize: Int): Context {
        val mockArray =
            createMockTypedArray(setOf(R.styleable.Movement_paused, R.styleable.ContinuousMovement_movementSize))
        every { mockArray.getBoolean(R.styleable.Movement_paused, any()) } returns paused
        every { mockArray.getInt(R.styleable.ContinuousMovement_movementSize, any()) } returns movementSize

        val context: Context = spyk(ApplicationProvider.getApplicationContext())
        listOf(R.styleable.Movement, R.styleable.ContinuousMovement).forEach {
            every { context.obtainStyledAttributes(any<AttributeSet>(), it, any(), any()) } returns mockArray
        }
        return context
    }

    private fun setPositionAndUpdate(view: MovingView, forced: Boolean = false) {
        val width = parentWidth.toInt() + viewWidth
        val height = parentHeight.toInt() + viewHeight
        setViewPosition(view as View)
        view.updatePosition(width, height, forceUpdate = forced)
    }

    private fun setViewPosition(view: View) {
        view.right = 100
        view.left = 100 - viewWidth
        view.bottom = 60
        view.top = 60 - viewHeight
    }
}
