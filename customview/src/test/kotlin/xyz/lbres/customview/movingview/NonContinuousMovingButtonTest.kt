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
import xyz.lbres.customview.testutils.runWithFailMessage
import xyz.lbres.customview.testutils.withMockedNextDouble
import kotlin.math.min
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class NonContinuousMovingButtonTest {
    private val parentWidth = 100.0
    private val parentHeight = 200.0
    private val viewWidth = 10
    private val viewHeight = 5

    private val positions = listOf(
        Position(1.0, 3.0),
        Position(0.6, 12.0),
        Position(100.0, 194.00000045),
        Position(0.0, 0.05),
    )

    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        // paused
        var view = NonContinuousMovingButton(createMockContext(true))
        assertTrue(view.paused)

        // not paused
        view = NonContinuousMovingButton(createMockContext(false))
        assertFalse(view.paused)
    }

    @Test
    fun testUpdatePaused() {
        var view = NonContinuousMovingButton(createMockContext(true))
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
        view = NonContinuousMovingButton(createMockContext(false))
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
    fun testUpdatePosition() {
        val view = NonContinuousMovingButton(createMockContext(true))
        val history: MutableList<Position<Int>> = mutableListOf()
        view.setOnMoveListener { _, x, y -> history.add(Position(x, y)) }

        setViewPosition(view)
        val width = parentWidth.toInt() + viewWidth
        val height = parentHeight.toInt() + viewHeight

        val updateAndCheck: (Int) -> Unit = {
            setPositionAndUpdate(view)
            checkViewPosition(view, positions[it])
            checkPositionHistory(positions.subList(0, it + 1), history)
        }

        withMockedNextDouble(parentWidth, parentHeight, positions) {
            // paused
            view.updatePosition(width, height)
            checkViewPosition(view, Position(0.0, 0.0))

            // valid position
            view.paused = false
            updateAndCheck(0)
            updateAndCheck(1)
            updateAndCheck(2)

            // re-paused
            view.paused = true
            updateAndCheck(2)

            // unpaused
            view.paused = false
            updateAndCheck(3)

            // repeat value
            updateAndCheck(3)
        }
    }

    @Test
    fun testForcePosition() {
        val view = NonContinuousMovingButton(createMockContext(false))
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
    fun testSetOnMoveListener() {
        val view = NonContinuousMovingButton(createMockContext())
        val width = parentWidth.toInt() + viewWidth
        val height = parentHeight.toInt() + viewHeight

        // callback
        var mockPositions = listOf(
            Position(1.0, 2.0),
            Position(3.0, 2.1),
            Position(0.7, 1.0),
            Position(0.7, 1.0), // repeat value
        )
        withMockedNextDouble(parentWidth, parentHeight, mockPositions) {
            var total = 0
            view.setOnMoveListener { view, x, y -> total += x * y }
            repeat(3) { setPositionAndUpdate(view) }
            assertEquals(8, total)

            // repeat value
            setViewPosition(view)
            view.updatePosition(width, height)
            assertEquals(8, total)
        }

        // object
        mockPositions = listOf(
            Position(5.0, 1.2),
            Position(2.0, 4.5),
            Position(3.000056, 7.0),
        )
        withMockedNextDouble(parentWidth, parentHeight, mockPositions) {
            var total = 0
            view.setOnMoveListener(object : MovingView.OnMoveListener {
                override fun onMove(view: View, x: Int, y: Int) {
                    total += min(x, y)
                }
            })
            repeat(3) { setPositionAndUpdate(view) }
            assertEquals(6, total)
        }

        // null
        mockPositions = listOf(
            Position(3.0, 2.1),
            Position(0.7, 1.0),
        )
        withMockedNextDouble(parentWidth, parentHeight, mockPositions) {
            val total = 0
            view.setOnMoveListener(null)
            repeat(2) { setPositionAndUpdate(view) }
            assertEquals(0, total)
        }
    }

    @Test
    fun testSetOnPauseChangedListener() {
        val view = NonContinuousMovingButton(createMockContext())

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
    private fun createMockContext(paused: Boolean = false): Context {
        val mockArray = createMockTypedArray(setOf(R.styleable.Movement_paused))
        every { mockArray.getBoolean(R.styleable.Movement_paused, false) } returns paused
        val context: Context = spyk(ApplicationProvider.getApplicationContext())
        every {
            context.obtainStyledAttributes(any<AttributeSet>(), R.styleable.Movement, any(), any())
        } returns mockArray
        return context
    }

    private fun setPositionAndUpdate(view: NonContinuousMovingButton) {
        val width = parentWidth.toInt() + viewWidth
        val height = parentHeight.toInt() + viewHeight
        setViewPosition(view)
        view.updatePosition(width, height)
    }

    private fun setViewPosition(view: NonContinuousMovingButton) {
        view.right = 100
        view.left = 100 - viewWidth
        view.bottom = 60
        view.top = 60 - viewHeight
    }
}
