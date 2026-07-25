package xyz.lbres.customview.movingview.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.movingview.MovingView
import xyz.lbres.customview.movingview.createMockContext
import xyz.lbres.customview.movingview.manager.LinearMovementManager
import xyz.lbres.customview.movingview.manager.NonContinuousMovementManager
import xyz.lbres.customview.movingview.manager.checkDefaultManagerState
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
            val context = createMockContext(0, paused, 0)
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
            val context = createMockContext(1, paused, movementSize)
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
        var message = "motionType is required to construct a MovingView"
        assertFailsWithMessage<IllegalStateException>(message) {
            val context = createMockContext(null, false, 0)
            parseAttributes(context, null)
        }

        message = "Valid motionType is required to construct a MovingView"
        assertFailsWithMessage<IllegalStateException>(message) {
            val context = createMockContext(-1, false, 0)
            parseAttributes(context, null)
        }

        assertFailsWithMessage<IllegalStateException>(message) {
            val context = createMockContext(2, false, 0)
            parseAttributes(context, null)
        }
    }
}
