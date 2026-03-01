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
    private val index = 12345

    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testGetIntOrNullWithValue() {
        mockkStatic(TypedArray::getIntOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getIntOrThrow(index) } returns 15
        }

        assertEquals(15, mockArray.getIntOrNull(index))
    }

    @Test
    fun testGetIntOrNullWithoutValue() {
        assertNull(emptyMockArray().getIntOrNull(index))
    }

    @Test
    fun testGetDimensionPixelSizeOrNullWithValue() {
        mockkStatic(TypedArray::getDimensionPixelSizeOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getDimensionPixelSizeOrThrow(index) } returns 1234
        }

        assertEquals(1234, mockArray.getDimensionPixelSizeOrNull(index))
    }

    @Test
    fun testGetDimensionPixelSizeOrNullWithoutValue() {
        assertNull(emptyMockArray().getDimensionPixelSizeOrNull(index))
    }

    @Test
    fun testGetFloatOrNullWithValue() {
        mockkStatic(TypedArray::getFloatOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getFloatOrThrow(index) } returns 100.3f
        }

        assertEquals(100.3f, mockArray.getFloatOrNull(index))
    }

    @Test
    fun testGetFloatOrNullWithoutValue() {
        assertNull(emptyMockArray().getFloatOrNull(index))
    }

    @Test
    fun testGetRadiansOrNullWithValue() {
        mockkStatic(TypedArray::getIntOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getIntOrThrow(index) } returns 90
        }

        assertEquals(1.5707963267948966, mockArray.getRadiansOrNull(index)!!, 0.000000000001)
    }

    @Test
    fun testGetRadiansOrNullWithoutValue() {
        assertNull(emptyMockArray().getRadiansOrNull(index))
    }

    @Test
    fun testGetResourceIdOrNullWithValue() {
        mockkStatic(TypedArray::getResourceIdOrThrow)
        val mockArray = mockk<TypedArray> {
            every { hasValue(index) } returns true
            every { getResourceIdOrThrow(index) } returns 99999999
        }

        assertEquals(99999999, mockArray.getResourceIdOrNull(index))
    }

    @Test
    fun testGetResourceIdOrNullWithoutValue() {
        assertNull(emptyMockArray().getResourceIdOrNull(index))
    }

    private fun emptyMockArray() = mockk<TypedArray> {
        every { hasValue(any()) } returns false
    }
}
