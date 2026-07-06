package xyz.lbres.customview.movingview

import android.content.Context
import android.util.AttributeSet
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.R
import xyz.lbres.customview.testutils.createMockTypedArray
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class NonContinuousMovingButtonTest {

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
        // TODO
    }

    @Test
    fun testForcePosition() {
        // TODO
    }

    @Test
    fun testSetOnMoveListener() {
        // callback, object, and null
        // TODO
    }

    @Test
    fun testSetOnPauseChangedListener() {
        // callback, object, and null
        // TODO
    }

    private fun createMockContext(paused: Boolean = false): Context {
        val mockArray = createMockTypedArray(setOf(R.styleable.Movement_paused))
        every { mockArray.getBoolean(R.styleable.Movement_paused, false) } returns paused
        val context: Context = spyk(ApplicationProvider.getApplicationContext())
        every {
            context.obtainStyledAttributes(any<AttributeSet>(), R.styleable.Movement, any(), any())
        } returns mockArray
        return context
    }
}
