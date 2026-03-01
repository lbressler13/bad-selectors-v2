package xyz.lbres.customview.ext.typedarray

import android.content.res.TypedArray
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getFloatOrThrow
import androidx.core.content.res.getIntOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TypedArrayExtTest {

    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testGetIntOrNullWithValue() {
        val index = 12345

        mockkStatic(TypedArray::getIntOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getIntOrThrow(index) } returns 15
        }

        assertEquals(15, mockArray.getIntOrNull(index))
    }

    @Test
    fun testGetIntOrNullWithoutValue() {
        val index = 12345
        val mockArray = mockk<TypedArray> {
            every { hasValue(any()) } returns false
        }

        assertNull(mockArray.getIntOrNull(index))
    }

    @Test
    fun testGetDimensionPixelSizeOrNullWithValue() {
        val index = 4567890

        mockkStatic(TypedArray::getDimensionPixelSizeOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getDimensionPixelSizeOrThrow(index) } returns 1234
        }

        assertEquals(1234, mockArray.getDimensionPixelSizeOrNull(index))
    }

    @Test
    fun testGetDimensionPixelSizeOrNullWithoutValue() {
        val index = 4567890
        val mockArray = mockk<TypedArray> {
            every { hasValue(any()) } returns false
        }

        assertNull(mockArray.getDimensionPixelSizeOrNull(index))
    }

    @Test
    fun testGetFloatOrNullWithValue() {
        val index = 1000

        mockkStatic(TypedArray::getFloatOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getFloatOrThrow(index) } returns 100.3f
        }

        assertEquals(100.3f, mockArray.getFloatOrNull(index))
    }

    @Test
    fun testGetFloatOrNullWithoutValue() {
        val index = 1000
        val mockArray = mockk<TypedArray> {
            every { hasValue(any()) } returns false
        }

        assertNull(mockArray.getFloatOrNull(index))
    }

    @Test
    fun testGetRadiansOrNullWithValue() {
        val index = 123456789

        mockkStatic(TypedArray::getIntOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getIntOrThrow(index) } returns 90
        }

        assertEquals(1.5707963267948966, mockArray.getRadiansOrNull(index)!!, 0.000000000001)
    }

    @Test
    fun testGetRadiansOrNullWithoutValue() {
        val index = 123456789
        val mockArray = mockk<TypedArray> {
            every { hasValue(any()) } returns false
        }

        assertNull(mockArray.getRadiansOrNull(index))
    }

    @Test
    fun testGetResourceIdOrNullWithValue() {
        val index = 1

        mockkStatic(TypedArray::getResourceIdOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getResourceIdOrThrow(index) } returns 99999999
        }

        assertEquals(99999999, mockArray.getResourceIdOrNull(index))
    }

    @Test
    fun testGetResourceIdOrNullWithoutValue() {
        val index = 1
        val mockArray = mockk<TypedArray> {
            every { hasValue(any()) } returns false
        }

        assertNull(mockArray.getResourceIdOrNull(index))
    }
}
