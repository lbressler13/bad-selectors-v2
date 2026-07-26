package xyz.lbres.customview.movingview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.spyk
import xyz.lbres.customview.R
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import xyz.lbres.customview.testutils.createMockTypedArray

/**
 * Typealias for function to create moving view
 */
internal typealias CreateMoving = (Context) -> MovingView

/**
 * Create mock context object which returns the given movement values in its attributes
 */
internal fun createMockContext(motionType: Int?, paused: Boolean = false, movementSize: Int = 0): Context {
    mockkStatic(TypedArray::getIntOrNull)
    val mockArray = createMockTypedArray(
        setOf(
            R.styleable.Movement_motionType,
            R.styleable.Movement_movementSize,
            R.styleable.Movement_paused,
        ),
    )
    every { mockArray.getIntOrNull(R.styleable.Movement_motionType) } returns motionType
    every { mockArray.getInt(R.styleable.Movement_movementSize, any()) } returns movementSize
    every { mockArray.getBoolean(R.styleable.Movement_paused, any()) } returns paused

    val context: Context = spyk(ApplicationProvider.getApplicationContext())
    every {
        context.obtainStyledAttributes(any<AttributeSet>(), R.styleable.Movement, any(), any())
    } returns mockArray
    return context
}
