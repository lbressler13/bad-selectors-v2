package xyz.lbres.customview.movingview

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.runner.RunWith
import xyz.lbres.customview.testutils.createMockTypedArray
import kotlin.test.AfterTest
import kotlin.test.Test
import xyz.lbres.customview.R
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
        var mockArray = createMockTypedArray(setOf(R.styleable.Movement_paused))
        every { mockArray.getBoolean(R.styleable.Movement_paused, false) } returns true
        var view = NonContinuousMovingButton(createMockContext(mockArray))
        assertTrue(view.paused)

        // not paused
        mockArray = createMockTypedArray(setOf(R.styleable.Movement_paused))
        every { mockArray.getBoolean(R.styleable.Movement_paused, false) } returns false
        view = NonContinuousMovingButton(createMockContext(mockArray))
        assertFalse(view.paused)
    }

    @Test
    fun testUpdatePaused() {
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
    fun testSetOnMoveListener() {
        // TODO
    }

    @Test
    fun testSetOnPauseChangedListener() {
        // TODO
    }

    private fun createMockContext(typedArray: TypedArray): Context {
        val context: Context = spyk(ApplicationProvider.getApplicationContext())
        every { context.obtainStyledAttributes(any<AttributeSet>(), R.styleable.Movement, any(), any()) } returns typedArray
        return context
    }
}
