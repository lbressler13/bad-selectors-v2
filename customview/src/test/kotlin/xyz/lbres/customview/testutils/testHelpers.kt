package xyz.lbres.customview.testutils

import android.content.res.TypedArray
import io.mockk.every
import io.mockk.mockk

/**
 * Create a mock typed array object with no attribute values.
 *
 * @param attributes [Set]<Int>: IDs of attributes that should be present in the array
 * @return [TypedArray]: mock using the provided values
 */
fun createMockTypedArray(attributes: Set<Int>): TypedArray {
    val mockArray = mockk<TypedArray>(relaxUnitFun = true)
    for (attr in attributes) {
        every { mockArray.hasValue(attr) } returns true
    }
    every { mockArray.hasValue(any()) } returns false

    return mockArray
}
