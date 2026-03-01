package xyz.lbres.customview.circlelayout

import android.content.res.TypedArray
import io.mockk.every
import io.mockk.mockkStatic
import xyz.lbres.customview.R
import xyz.lbres.customview.circlelayout.CircleLayout.AngleMode
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import kotlin.test.assertEquals

private const val distributed = 0
private const val fixed = 1

fun runDistributedAngleTest() {
    val mockArray = createMockTypedArray(0, distributed, setOf(R.styleable.CircleLayout_angleMode))
    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

fun runFixedAngleWithoutSeparationAngleTest() {
    val mockArray = createMockTypedArray(0, fixed, setOf(R.styleable.CircleLayout_angleMode))
    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

fun runFixedAngleWithSeparationAngleTest() {
    mockkStatic(TypedArray::getIntOrNull)
    val attr = setOf(R.styleable.CircleLayout_angleMode, R.styleable.CircleLayout_separationAngle)
    val mockArray = createMockTypedArray(0, fixed, attr)
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 90

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaultsExcept(layout, setOf("angleMode", "separationAngle"))
    assertEquals(AngleMode.FIXED, layout.angleMode)
    assertEquals(1.5707963267948966, layout.separationAngle, doubleDelta)
}

fun runSeparationAngleWithoutAngleModeTest() {
    mockkStatic(TypedArray::getIntOrNull)
    val mockArray = createMockTypedArray(0, distributed, setOf(R.styleable.CircleLayout_separationAngle))
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 90

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

// set angle property with incorrect angle mode
fun runSeparationAngleWithIncorrectModeTest() {
    mockkStatic(TypedArray::getIntOrNull)
    val attr = setOf(R.styleable.CircleLayout_angleMode, R.styleable.CircleLayout_separationAngle)
    val mockArray = createMockTypedArray(0, distributed, attr)
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 90

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

fun runStartAngleTest() {
    mockkStatic(TypedArray::getIntOrNull)
    val mockArray = createMockTypedArray(0, distributed, setOf(R.styleable.CircleLayout_startAngle))
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_startAngle) } returns 90

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaultsExcept(layout, setOf("startAngle"))
    assertEquals(1.5707963267948966, layout.startAngle, doubleDelta)
}
