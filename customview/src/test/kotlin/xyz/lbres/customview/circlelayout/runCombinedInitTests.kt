package xyz.lbres.customview.circlelayout

import android.content.res.TypedArray
import io.mockk.every
import io.mockk.mockkStatic
import xyz.lbres.customview.R
import xyz.lbres.customview.circlelayout.CircleLayout.AngleMode
import xyz.lbres.customview.circlelayout.CircleLayout.RadiusMode
import xyz.lbres.customview.ext.typedarray.getDimensionPixelSizeOrNull
import xyz.lbres.customview.ext.typedarray.getFloatOrNull
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNull

fun runPropertiesWithoutModesTest() {
    mockkStatic(TypedArray::getDimensionPixelSizeOrNull)
    mockkStatic(TypedArray::getIntOrNull)
    val attr = setOf(R.styleable.CircleLayout_radiusSize, R.styleable.CircleLayout_separationAngle)
    val mockArray = createMockTypedArray(0, 0, attr)
    every { mockArray.getDimensionPixelSizeOrNull(R.styleable.CircleLayout_radiusSize) } returns 50
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 120

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

fun runRadiusPropertiesWithAngleModeTest() {
    mockkStatic(TypedArray::getFloatOrNull)
    val attr = setOf(R.styleable.CircleLayout_radiusPercent, R.styleable.CircleLayout_angleMode)
    val mockArray = createMockTypedArray(0, 1, attr)
    every { mockArray.getFloatOrNull(R.styleable.CircleLayout_radiusPercent) } returns 0.3f

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

fun runAnglePropertiesWithRadiusModeTest() {
    mockkStatic(TypedArray::getIntOrNull)
    val attr = setOf(R.styleable.CircleLayout_radiusMode, R.styleable.CircleLayout_separationAngle)
    val mockArray = createMockTypedArray(2, 0, attr)
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 90

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaults(layout)
}

fun runPercentRadiusWithSeparationAngleTest() {
    mockkStatic(TypedArray::getFloatOrNull)
    mockkStatic(TypedArray::getIntOrNull)
    val attr =
        setOf(
            R.styleable.CircleLayout_radiusMode,
            R.styleable.CircleLayout_radiusPercent,
            R.styleable.CircleLayout_separationAngle,
        )
    val mockArray = createMockTypedArray(2, 0, attr)
    every { mockArray.getFloatOrNull(R.styleable.CircleLayout_radiusPercent) } returns 0.3f
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 90

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaultsExcept(layout, setOf("radiusMode", "radiusPercent"))
    assertEquals(RadiusMode.PERCENT, layout.radiusMode)
    assertEquals(0.3f, layout.radiusPercent)
}

fun runFixedAngleWithRadiusSizeTest() {
    mockkStatic(TypedArray::getDimensionPixelSizeOrNull)
    mockkStatic(TypedArray::getIntOrNull)
    val attr =
        setOf(
            R.styleable.CircleLayout_radiusSize,
            R.styleable.CircleLayout_angleMode,
            R.styleable.CircleLayout_separationAngle,
        )
    val mockArray = createMockTypedArray(0, 1, attr)
    every { mockArray.getDimensionPixelSizeOrNull(R.styleable.CircleLayout_radiusSize) } returns 100
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 90

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaultsExcept(layout, setOf("angleMode", "separationAngle"))
    assertEquals(AngleMode.FIXED, layout.angleMode)
    assertEquals(1.5707963267948966, layout.separationAngle, doubleDelta)
}

fun runStartAngleAndSeparationAngleTest() {
    mockkStatic(TypedArray::getIntOrNull)
    val attr =
        setOf(
            R.styleable.CircleLayout_angleMode,
            R.styleable.CircleLayout_separationAngle,
            R.styleable.CircleLayout_startAngle,
        )
    val mockArray = createMockTypedArray(0, 1, attr)
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 75
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_startAngle) } returns 290

    val layout = CircleLayout(createMockContext(mockArray))
    checkMatchesDefaultsExcept(layout, setOf("angleMode", "separationAngle", "startAngle"))
    assertEquals(AngleMode.FIXED, layout.angleMode)
    assertEquals(1.308996938995747, layout.separationAngle, doubleDelta)
    assertEquals(5.061454830783556, layout.startAngle, doubleDelta)
}

fun runFixedRadiusAndFixedAngleTest() {
    mockkStatic(TypedArray::getDimensionPixelSizeOrNull)
    mockkStatic(TypedArray::getIntOrNull)
    val attr =
        setOf(
            R.styleable.CircleLayout_radiusMode,
            R.styleable.CircleLayout_radiusSize,
            R.styleable.CircleLayout_angleMode,
            R.styleable.CircleLayout_separationAngle,
        )
    val mockArray = createMockTypedArray(1, 1, attr)
    every { mockArray.getDimensionPixelSizeOrNull(R.styleable.CircleLayout_radiusSize) } returns 100
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 90

    val layout = CircleLayout(createMockContext(mockArray))
    assertEquals(RadiusMode.FIXED, layout.radiusMode)
    assertEquals(100, layout.radiusSize)
    assertNull(layout.radiusPercent)
    assertEquals(AngleMode.FIXED, layout.angleMode)
    assertEquals(1.5707963267948966, layout.separationAngle, doubleDelta)
    assertEquals(0.0, layout.startAngle, doubleDelta)
}

fun runAllPropertiesSetTest() {
    mockkStatic(TypedArray::getDimensionPixelSizeOrNull)
    mockkStatic(TypedArray::getIntOrNull)
    val attr =
        setOf(
            R.styleable.CircleLayout_radiusMode,
            R.styleable.CircleLayout_radiusSize,
            R.styleable.CircleLayout_angleMode,
            R.styleable.CircleLayout_separationAngle,
            R.styleable.CircleLayout_startAngle,
        )
    val mockArray = createMockTypedArray(1, 1, attr)
    every { mockArray.getDimensionPixelSizeOrNull(R.styleable.CircleLayout_radiusSize) } returns 100
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_separationAngle) } returns 90
    every { mockArray.getIntOrNull(R.styleable.CircleLayout_startAngle) } returns 290

    val layout = CircleLayout(createMockContext(mockArray))
    assertEquals(RadiusMode.FIXED, layout.radiusMode)
    assertEquals(100, layout.radiusSize)
    assertNull(layout.radiusPercent)
    assertEquals(AngleMode.FIXED, layout.angleMode)
    assertEquals(1.5707963267948966, layout.separationAngle, doubleDelta)
    assertEquals(5.061454830783556, layout.startAngle, doubleDelta)
}
