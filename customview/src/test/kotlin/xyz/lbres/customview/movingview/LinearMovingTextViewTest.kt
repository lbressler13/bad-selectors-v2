package xyz.lbres.customview.movingview

import android.content.Context
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
import xyz.lbres.customview.testutils.setViewSize
import xyz.lbres.customview.testutils.withMockedDegrees
import xyz.lbres.customview.testutils.withMockedNextDouble
import xyz.lbres.testutils.runWithFailMessage
import kotlin.math.max
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class LinearMovingTextViewTest {
    private val parentWidth = 100.0
    private val parentHeight = 200.0
    private val viewWidth = 10
    private val viewHeight = 5

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        // paused
        var view = LinearMovingTextView(createMockContext(true, 10))
        assertTrue(view.paused)
        assertEquals(10, view.movementSize)

        // not paused
        view = LinearMovingTextView(createMockContext(false, -12))
        assertFalse(view.paused)
        assertEquals(0, view.movementSize)
    }

    @Test
    fun testUpdatePaused() {
        var view = LinearMovingTextView(createMockContext(true, 5))
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
        view = LinearMovingTextView(createMockContext(false, 5))
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
            val view = LinearMovingTextView(createMockContext(false, 10))
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

            // negative, no change to movement size
            view.movementSize = -1
            assertEquals(2, view.movementSize)
            updateAndCheck(34.0)

            // zero
            view.movementSize = 0
            assertEquals(0, view.movementSize)
            updateAndCheck(34.0)
        }
    }

    @Test
    fun testUpdatePosition() {
        val history: MutableList<Position<Int>> = mutableListOf()
        val expectedHistory: MutableList<Position<Int>> = mutableListOf()

        val initialPosition = Position(40, 50)
        val angles = listOf(90, -45)

        withMockedDegrees(angles) {
            val view = LinearMovingTextView(createMockContext(true, 5))
            setViewSize(view, viewWidth, viewHeight)
            view.setOnMoveListener { _, x, y -> history.add(Position(x, y)) }

            // checks after position update
            fun validateUpdate(position: Position<Int>, addToHistory: Boolean = true) {
                checkViewPosition(view, position)
                if (addToHistory) {
                    expectedHistory.add(position)
                }
                checkPositionHistory(expectedHistory, history)
            }

            var width = parentWidth.toInt() + viewWidth
            var height = parentHeight.toInt() + viewHeight

            view.forcePosition(width, height, initialPosition.x, initialPosition.y)
            expectedHistory.add(initialPosition)

            // paused
            view.updatePosition(width, height)
            validateUpdate(initialPosition, false)

            // force update
            view.updatePosition(width, height, true)
            validateUpdate(Position(40, 55))
            view.updatePosition(width, height, true)
            validateUpdate(Position(40, 60))

            // not paused
            view.paused = false
            view.updatePosition(width, height)
            validateUpdate(Position(40, 65))

            // reaching edge
            width = 100 + viewWidth
            height = 70 + viewHeight
            view.updatePosition(width, height)
            validateUpdate(Position(40, 70))
            view.updatePosition(width, height)
            validateUpdate(Position(40, 75))

            view.updatePosition(width, height)
            validateUpdate(Position(43, 71)) // 43.53, 71.46
            view.updatePosition(width, height)
            validateUpdate(Position(47, 67)) // 47.07, 67.92

            // re-paused
            view.paused = true
            view.updatePosition(width, height)
            validateUpdate(Position(47, 67), false) // 47.07, 67.92

            // unpaused
            view.paused = false
            view.updatePosition(width, height)
            validateUpdate(Position(50, 64)) // 50.60, 64.39
        }
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
        val view = LinearMovingTextView(createMockContext(false, 5))
        setViewSize(view, viewWidth, viewHeight)
        val history: MutableList<Position<Int>> = mutableListOf()
        view.setOnMoveListener { _, x, y -> history.add(Position(x, y)) }

        val width = parentWidth.toInt() + viewWidth
        val height = parentHeight.toInt() + viewHeight

        val forceAndCheck: (Int) -> Unit = {
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
                var view = LinearMovingTextView(createMockContext(false, 5))
                view.setInitialPosition(parentWidth.toInt(), parentHeight.toInt())
                checkViewPosition(view, positions[0])

                // paused
                view = LinearMovingTextView(createMockContext(false, 5))
                view.setInitialPosition(parentWidth.toInt(), parentHeight.toInt())
                checkViewPosition(view, positions[1])
            }
        }
    }

    @Test
    fun testSetOnMoveListener() {
        val width = parentWidth.toInt() + viewWidth
        val height = parentHeight.toInt() + viewHeight
        withMockedDegrees(listOf(90)) {
            val view = LinearMovingTextView(createMockContext(false, 1))
            view.forcePosition(width, height, 0, 0)

            val history: MutableList<Position<Int>> = mutableListOf()
            view.setOnMoveListener { _, x, y -> history.add(Position(x, y)) }

            // callback
            var total = 1 // start at 1 for multiplication
            view.setOnMoveListener { view, x, y ->
                total *= (x - y)
            }
            repeat(3) { view.updatePosition(width, height) }
            assertEquals(-6, total) // 1, 2, 3

            // object
            total = 0
            view.setOnMoveListener(object : MovingView.OnMoveListener {
                override fun onMove(view: View, x: Int, y: Int) {
                    total += max(x, y)
                }
            })
            repeat(3) { view.updatePosition(width, height) }
            assertEquals(15, total) // 4, 5, 6

            // null
            total = 0
            view.setOnMoveListener(null)
            repeat(3) { view.updatePosition(width, height) }
            assertEquals(0, total)
        }
    }

    @Test
    fun testSetOnPauseChangedListener() {
        val view = LinearMovingTextView(createMockContext(false, 10))

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
        val mockArray = createMockTypedArray(
            setOf(
                R.styleable.Movement_paused,
                R.styleable.LinearMovement_movementSize,
            ),
        )
        every { mockArray.getBoolean(R.styleable.Movement_paused, any()) } returns paused
        every { mockArray.getInt(R.styleable.LinearMovement_movementSize, any()) } returns movementSize

        val context: Context = spyk(ApplicationProvider.getApplicationContext())
        listOf(R.styleable.Movement, R.styleable.LinearMovement).forEach {
            every { context.obtainStyledAttributes(any(), it, any(), any()) } returns mockArray
        }
        return context
    }
}
