package xyz.lbres.customview.circlelayout

import android.content.res.TypedArray
import io.mockk.every
import io.mockk.mockkStatic
import xyz.lbres.customview.R
import xyz.lbres.customview.circlelayout.CircleLayout.RadiusMode
import xyz.lbres.customview.ext.typedarray.getDimensionPixelSizeOrNull
import xyz.lbres.customview.ext.typedarray.getFloatOrNull
import kotlin.test.assertEquals

// set radius to FIT_CHILDREN, no radius properties
fun runFitChildrenTest() {
    val mockArray = createMockTypedArray(0, 0, setOf(R.styleable.CircleLayout_radiusMode))
    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

// set radius to FIXED, no radius properties
fun runFixedRadiusWithoutSizeTest() {
    val mockArray = createMockTypedArray(1, 0, setOf(R.styleable.CircleLayout_radiusMode))
    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

// set radius to PERCENT, no radius properties
fun runPercentRadiusWithoutPercentTest() {
    val mockArray = createMockTypedArray(2, 0, setOf(R.styleable.CircleLayout_radiusMode))
    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

// set radius to FIXED, with radiusSize
fun runFixedRadiusWithSizeTest() {
    mockkStatic(TypedArray::getDimensionPixelSizeOrNull)
    val attr = setOf(R.styleable.CircleLayout_radiusMode, R.styleable.CircleLayout_radiusSize)
    val mockArray = createMockTypedArray(1, 0, attr)
    every { mockArray.getDimensionPixelSizeOrNull(R.styleable.CircleLayout_radiusSize) } returns 50

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaultsExcept(layout, setOf("radiusMode", "radiusSize"))
    assertEquals(RadiusMode.FIXED, layout.radiusMode)
    assertEquals(50, layout.radiusSize)
}

// set radius to PERCENT, with radiusPercent
fun runPercentRadiusWithPercentTest() {
    mockkStatic(TypedArray::getFloatOrNull)
    val attr = setOf(R.styleable.CircleLayout_radiusMode, R.styleable.CircleLayout_radiusPercent)
    val mockArray = createMockTypedArray(2, 0, attr)
    every { mockArray.getFloatOrNull(R.styleable.CircleLayout_radiusPercent) } returns 0.3f

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaultsExcept(layout, setOf("radiusMode", "radiusPercent"))
    assertEquals(RadiusMode.PERCENT, layout.radiusMode)
    assertEquals(0.3f, layout.radiusPercent)
}

// set radiusSize without radiusMode
fun runRadiusSizeWithoutRadiusModeTest() {
    mockkStatic(TypedArray::getDimensionPixelSizeOrNull)
    val mockArray = createMockTypedArray(0, 0, setOf(R.styleable.CircleLayout_radiusSize))
    every { mockArray.getDimensionPixelSizeOrNull(R.styleable.CircleLayout_radiusSize) } returns 50

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

// set radiusPercent without radiusMode
fun runRadiusPercentWithoutRadiusModeTest() {
    mockkStatic(TypedArray::getFloatOrNull)
    val mockArray = createMockTypedArray(0, 0, setOf(R.styleable.CircleLayout_radiusPercent))
    every { mockArray.getFloatOrNull(R.styleable.CircleLayout_radiusPercent) } returns 0.3f

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

// set radiusPercent with incorrect radius mode
fun runRadiusPercentWithIncorrectModeTest() {
    mockkStatic(TypedArray::getFloatOrNull)
    val attr = setOf(R.styleable.CircleLayout_radiusMode, R.styleable.CircleLayout_radiusPercent)
    val mockArray = createMockTypedArray(1, 0, attr)
    every { mockArray.getFloatOrNull(R.styleable.CircleLayout_radiusPercent) } returns 0.3f

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

// set radiusSize with incorrect radius mode
fun runRadiusSizeWithIncorrectModeTest() {
    mockkStatic(TypedArray::getDimensionPixelSizeOrNull)
    val attr = setOf(R.styleable.CircleLayout_radiusMode, R.styleable.CircleLayout_radiusSize)
    val mockArray = createMockTypedArray(2, 0, attr)
    every { mockArray.getDimensionPixelSizeOrNull(R.styleable.CircleLayout_radiusSize) } returns 50

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}
