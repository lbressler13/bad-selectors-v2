package xyz.lbres.customview.testutils

import android.content.res.TypedArray
import android.view.View
import io.mockk.every
import io.mockk.mockk
import xyz.lbres.customview.data.Position
import kotlin.test.assertEquals

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
 * Check that the position of a view matches the given position
 */
internal fun checkViewPosition(view: View, position: Position<Double>) {
    assertEquals(position.x.toInt(), view.left)
    assertEquals(position.y.toInt(), view.top)
}

/**
 * Check that the position history matches the list of positions up to the given index
 */
internal fun checkPositionHistory(positions: List<Position<Double>>, history: List<Position<Int>>) {
    val expectedHistory = positions.map { Position(it.x.toInt(), it.y.toInt()) }
    assertEquals(expectedHistory, history)
}
