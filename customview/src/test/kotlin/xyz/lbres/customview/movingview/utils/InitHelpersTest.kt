package xyz.lbres.customview.movingview.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.R
import xyz.lbres.customview.movingview.MovingView
import xyz.lbres.customview.movingview.manager.LinearMovementManager
import xyz.lbres.customview.movingview.manager.NonContinuousMovementManager
import xyz.lbres.customview.movingview.manager.checkDefaultManagerState
import xyz.lbres.customview.testutils.createMockTypedArray
import xyz.lbres.testutils.assertFailsWithMessage
import xyz.lbres.testutils.mockLog
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunWith(AndroidJUnit4::class)
class InitHelpersTest {

    @BeforeTest
    fun setupTest() {
        mockLog()
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testParseNonContinuous() {
        fun checkNonContinuous(paused: Boolean) {
            val context = createMockContext(paused, 0, 0)
            val (motionType, manager) = parseAttributes(context, null)
            assertEquals(MovingView.MotionType.NONCONTINUOUS, motionType)
            assertIs<NonContinuousMovementManager>(manager)
            checkDefaultManagerState(manager, paused)
        }

        // paused
        checkNonContinuous(true)
        // unpaused
        checkNonContinuous(false)
    }

    @Test
    fun testParseLinear() {
        fun checkLinear(paused: Boolean, movementSize: Int, expectedMovementSize: Int = movementSize) {
            val context = createMockContext(paused, movementSize, 1)
            val (motionType, manager) = parseAttributes(context, null)
            assertEquals(MovingView.MotionType.LINEAR, motionType)
            assertIs<LinearMovementManager>(manager)
            checkDefaultManagerState(manager, paused)
            assertEquals(expectedMovementSize, manager.movementSize)
        }

        // paused
        checkLinear(true, 5)
        // unpaused
        checkLinear(false, 10)
        // negative movement size
        checkLinear(false, -10, 0)
    }

    @Test
    fun testParseInvalidMovementType() {
        val message = "Valid motionType is required to construct a MovingView"
        assertFailsWithMessage<IllegalStateException>(message) {
            val context = createMockContext(false, 0, -1)
            parseAttributes(context, null)
        }

        assertFailsWithMessage<IllegalStateException>(message) {
            val context = createMockContext(false, 0, 2)
            parseAttributes(context, null)
        }
    }
}

/**
 * Create mock context object which returns the given paused value in its attributes
 */
private fun createMockContext(paused: Boolean, movementSize: Int, motionType: Int): Context {
    val mockArray = createMockTypedArray(
        setOf(
            R.styleable.Movement_paused,
            R.styleable.Movement_movementSize,
            R.styleable.Movement_motionType,
        ),
    )
    every { mockArray.getBoolean(R.styleable.Movement_paused, any()) } returns paused
    every { mockArray.getInt(R.styleable.Movement_movementSize, any()) } returns movementSize
    every { mockArray.getInt(R.styleable.Movement_motionType, any()) } returns motionType

    val context: Context = spyk(ApplicationProvider.getApplicationContext())
    listOf(R.styleable.Movement, R.styleable.Movement).forEach {
        every { context.obtainStyledAttributes(any(), it, any(), any()) } returns mockArray
    }
    return context
}
