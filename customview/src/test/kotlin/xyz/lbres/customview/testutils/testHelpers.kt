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
internal fun checkViewPosition(view: View, position: Position<Int>) {
    assertEquals(position.x, view.left)
    assertEquals(position.y, view.top)
}

@JvmName("checkViewPosition::Double")
internal fun checkViewPosition(view: View, position: Position<Double>) {
    checkViewPosition(view, position.toIntPosition())
}

/**
 * Set width and height of view by configuring left, right, top, and bottom
 */
fun setViewSize(view: View, width: Int, height: Int) {
    view.right = 100
    view.left = 100 - width
    view.bottom = 100
    view.top = 100 - height
}

/**
 * Check that the position history matches the list of positions up to the given index
 */
internal fun checkPositionHistory(expectedHistory: List<Position<Int>>, history: List<Position<Int>>) {
    assertEquals(expectedHistory, history)
}

@JvmName("checkViewPosition::Double")
internal fun checkPositionHistory(expectedHistory: List<Position<Double>>, history: List<Position<Int>>) {
    checkPositionHistory(expectedHistory.map(Position<Double>::toIntPosition), history)
}
