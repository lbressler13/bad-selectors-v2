package xyz.lbres.customview.movingview

import android.view.View
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.testutils.checkPositionHistory
import xyz.lbres.customview.testutils.checkViewPosition
import xyz.lbres.customview.testutils.setViewSize
import xyz.lbres.customview.testutils.withMockedNextDouble
import xyz.lbres.testutils.runWithFailMessage
import kotlin.math.min
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val parentWidth = 100.0
private const val parentHeight = 200.0
private const val viewWidth = 10
private const val viewHeight = 5

private val positions = listOf(
    Position(1.0, 3.0),
    Position(0.6, 12.0),
    Position(100.0, 194.00000045),
    Position(0.0, 0.05),
    Position(3.14, 15.0),
)

internal fun runNonContinuousInitTests(createView: CreateMoving) {
    // paused
    var view = createView(createMockContext(0, true))
    assertTrue(view.paused)
    assertEquals(MovingView.MotionType.NONCONTINUOUS, view.motionType)

    // not paused
    view = createView(createMockContext(0, false))
    assertFalse(view.paused)
    assertEquals(MovingView.MotionType.NONCONTINUOUS, view.motionType)
}

internal fun runNonContinuousUpdatePausedTests(createView: CreateMoving) {
    var view = createView(createMockContext(0, true))
    view as View

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
    view = createView(createMockContext(0, false))
    view as View

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

internal fun runNonContinuousUpdatePositionTests(createView: CreateMoving) {
    withMockedNextDouble(parentWidth, parentHeight, positions) {
        val view = createView(createMockContext(0, true))
        view as View

        setViewSize(view, viewWidth, viewHeight)
        val history: MutableList<Position<Int>> = mutableListOf()
        view.setOnMoveListener { _, x, y -> history.add(Position(x, y)) }

        val width = parentWidth.toInt() + viewWidth
        val height = parentHeight.toInt() + viewHeight

        val updateAndCheck: (Int) -> Unit = {
            view.updatePosition(width, height)
            checkViewPosition(view, positions[it])
            checkPositionHistory(positions.subList(0, it + 1), history)
        }

        // paused
        view.updatePosition(width, height)
        checkViewPosition(view, Position(0.0, 0.0))

        // force update
        view.updatePosition(width, height, true)
        checkViewPosition(view, positions[0])
        checkPositionHistory(positions.subList(0, 1), history)

        // valid position
        view.paused = false
        updateAndCheck(1)
        updateAndCheck(2)
        updateAndCheck(3)

        // re-paused
        view.paused = true
        updateAndCheck(3)

        // unpaused
        view.paused = false
        updateAndCheck(4)

        // repeat value
        updateAndCheck(4)
    }
}

internal fun runNonContinuousForcePositionTests(createView: CreateMoving) {
    val view = createView(createMockContext(0, false))
    view as View
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

fun runNonContinuousSetInitialPositionTests(createView: CreateMoving) {
    withMockedNextDouble(parentWidth, parentHeight, positions) {
        // not paused
        var view = createView(createMockContext(0, false))
        view as View
        view.setInitialPosition(parentWidth.toInt(), parentHeight.toInt())
        checkViewPosition(view, positions[0])

        // paused
        view = createView(createMockContext(0, false))
        view as View
        view.setInitialPosition(parentWidth.toInt(), parentHeight.toInt())
        checkViewPosition(view, positions[1])
    }
}

fun runNonContinuousSetOnMoveListerTests(createView: CreateMoving) {
    val width = parentWidth.toInt() + viewWidth
    val height = parentHeight.toInt() + viewHeight

    val callbackPositions = listOf(
        Position(1.0, 2.0),
        Position(3.0, 2.1),
        Position(0.7, 1.0),
        Position(0.7, 1.0), // repeat value
    )
    val objectPositions = listOf(
        Position(5.0, 1.2),
        Position(2.0, 4.5),
        Position(3.000056, 7.0),
    )
    val nullPositions = listOf(
        Position(3.0, 2.1),
        Position(0.7, 1.0),
    )
    val mockPositions = callbackPositions + objectPositions + nullPositions

    withMockedNextDouble(parentWidth, parentHeight, mockPositions) {
        val view = createView(createMockContext(0))
        view as View
        setViewSize(view, viewWidth, viewHeight)

        // callback
        var total = 0
        view.setOnMoveListener { view, x, y -> total += x * y }
        repeat(3) { view.updatePosition(width, height) }
        assertEquals(8, total)

        // repeat value
        view.updatePosition(width, height)
        assertEquals(8, total)

        // object
        total = 0
        view.setOnMoveListener(object : MovingView.OnMoveListener {
            override fun onMove(view: View, x: Int, y: Int) {
                total += min(x, y)
            }
        })
        repeat(3) { view.updatePosition(width, height) }
        assertEquals(6, total)

        // null
        total = 0
        view.setOnMoveListener(null)
        repeat(2) { view.updatePosition(width, height) }
        assertEquals(0, total)
    }
}

fun runNonContinuousSetOnPauseChangedListenerTests(createView: CreateMoving) {
    val view = createView(createMockContext(0))

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
