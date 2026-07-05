package xyz.lbres.customview.testutils

import android.content.res.TypedArray
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

/**
 * Assert that a test fails with a specific exception of type [T] being thrown and a specific error message
 *
 * @param message [String]: expected error message
 * @param block: test to run
 * @return An exception of the expected exception type [T] that was successfully caught
 */
inline fun <reified T : Throwable> assertFailsWithMessage(message: String, block: () -> Unit): T {
    val exception = assertFailsWith<T> { block() }
    assertEquals(message, exception.message)
    return exception
}
