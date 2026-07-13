package xyz.lbres.customview.movingview

import android.content.Context
import android.util.AttributeSet
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.R
import xyz.lbres.customview.testutils.createMockTypedArray
import xyz.lbres.customview.utils.createRandom
import xyz.lbres.customview.utils.seededRandom
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
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

    @BeforeTest
    fun setupTest() {
        mockkStatic(::createRandom, IntRange::seededRandom, Set<Int>::seededRandom)
    }

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
}
