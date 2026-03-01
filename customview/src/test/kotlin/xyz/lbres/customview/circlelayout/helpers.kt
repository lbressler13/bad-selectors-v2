package xyz.lbres.customview.circlelayout

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import io.mockk.every
import io.mockk.mockk
import xyz.lbres.customview.R
import kotlin.test.assertEquals
import kotlin.test.assertNull
import xyz.lbres.customview.testutils.createMockTypedArray as createArrayHelper

const val doubleDelta = 0.000000000001

/**
 * Verify that all properties of a layout match the defaults that would be set without any custom properties
 *
 * @param layout [CircleLayout]: layout to verify
 */
fun checkMatchesDefaults(layout: CircleLayout) = checkMatchesDefaultsExcept(layout, setOf())

/**
 * Verify that all properties of a layout match the defaults that would be set without any custom properties,
 * except for a specific set of properties
 *
 * @param layout [CircleLayout]: layout to verify
 * @param exceptions [Set]<String>: names of properties to skip when verifying
 */
fun checkMatchesDefaultsExcept(layout: CircleLayout, exceptions: Set<String>) {
    if ("radiusMode" !in exceptions) {
        assertEquals(CircleLayout.RadiusMode.FIT_CHILDREN, layout.radiusMode)
    }
    if ("radiusSize" !in exceptions) {
        assertNull(layout.radiusSize)
    }
    if ("radiusPercent" !in exceptions) {
        assertNull(layout.radiusPercent)
    }
    if ("angleMode" !in exceptions) {
        assertEquals(CircleLayout.AngleMode.DISTRIBUTED, layout.angleMode)
    }
    if ("separationAngle" !in exceptions) {
        assertEquals(0.0, layout.separationAngle, doubleDelta)
    }
    if ("startAngle" !in exceptions) {
        assertEquals(0.0, layout.startAngle, doubleDelta)
    }
}

/**
 * Create a mock typed array object, with values for radiusMode and angleMode.
 * Values for other properties must be set after array is created.
 *
 * @param radiusMode [Int]: value to return from getInt(radiusMode, 0)
 * @param angleMode [Int]: value to return from getInt(angleMode, 0)
 * @param attributes [Set]<Int>: IDs of attributes that should be present in the array
 * @return [TypedArray]: mock using the provided values
 */
fun createMockTypedArray(radiusMode: Int, angleMode: Int, attributes: Set<Int>): TypedArray {
    val mockArray = createArrayHelper(attributes)
    every { mockArray.getInt(R.styleable.CircleLayout_radiusMode, 0) } returns radiusMode
    every { mockArray.getInt(R.styleable.CircleLayout_angleMode, 0) } returns angleMode

    return mockArray
}

/**
 * Create a mock context object
 *
 * @param typedArray [TypedArray]: array of attributes for the layout
 * @return [Context]: mock using the provided array
 */
fun createMockContext(typedArray: TypedArray): Context {
    return mockk {
        every { obtainStyledAttributes(any<AttributeSet>(), R.styleable.CircleLayout, any(), any()) } returns typedArray
        every { obtainStyledAttributes(null, R.styleable.CircleLayout, any(), any()) } returns typedArray
    }
}
