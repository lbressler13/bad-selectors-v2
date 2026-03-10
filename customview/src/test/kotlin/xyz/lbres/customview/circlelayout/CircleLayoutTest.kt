package xyz.lbres.customview.circlelayout

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.customview.R
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class CircleLayoutTest {
    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun createDefaultLayout() {
        val context = createMockContext(createEmptyTypeArray())
        val layout = CircleLayout(context)
        checkMatchesDefaults(layout)

        assertEquals(0, layout.childCount)
        assertEquals(0, layout.width)
        assertEquals(0, layout.height)
    }

    private fun createMockContext(typedArray: TypedArray): Context {
        val context: Context = spyk(ApplicationProvider.getApplicationContext())
        every {
            context.obtainStyledAttributes(any<AttributeSet>(), R.styleable.CircleLayout, any(), any())
        } returns typedArray
        return context
    }

    private fun createEmptyTypeArray(): TypedArray = mockk<TypedArray>(relaxUnitFun = true) {
        every { hasValue(any()) } returns false
        every { getInt(R.styleable.CircleLayout_radiusMode, 0) } returns 0
        every { getInt(R.styleable.CircleLayout_angleMode, 0) } returns 0
    }
}
