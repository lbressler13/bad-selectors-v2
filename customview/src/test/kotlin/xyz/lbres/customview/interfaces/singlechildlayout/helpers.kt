package xyz.lbres.customview.interfaces.singlechildlayout

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import xyz.lbres.customview.R
import xyz.lbres.customview.circlelayout.SingleChildCircleLayout
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import xyz.lbres.customview.ext.typedarray.getResourceIdOrNull

const val childResId = 123

/**
 * Create a mock context object
 *
 * @param numChildren [Int]?: value for the numChildren attribute. If `null`, the attribute will not be present in the attribute set
 * @param hasChildLayout [Boolean]: if childLayout should be present in the attribute set. If `true`, [childResId] will be used for the layout value
 * @return [Context]: mock using the provided values
 */
fun createMockContext(numChildren: Int?, hasChildLayout: Boolean): Context {
    val mockArray = mockk<TypedArray>(relaxUnitFun = true) {
        every { hasValue(R.styleable.SingleChild_childLayout) } returns hasChildLayout
        every { hasValue(R.styleable.SingleChild_numChildren) } returns (numChildren != null)
    }
    if (numChildren != null) {
        every { mockArray.getIntOrNull(R.styleable.SingleChild_numChildren) } returns numChildren
    }
    if (hasChildLayout) {
        every { mockArray.getResourceIdOrNull(R.styleable.SingleChild_childLayout) } returns childResId
    }
    val mockContext = mockk<Context> {
        every { obtainStyledAttributes(any<AttributeSet>(), R.styleable.SingleChild, 0, 0) } returns mockArray
        every { obtainStyledAttributes(null, R.styleable.SingleChild, 0, 0) } returns mockArray
    }

    return mockContext
}

/**
 * Create a mock SingleChildLayout
 *
 * @return [ViewGroup] mocked layout
 */
internal fun createMockLayout(): ViewGroup {
    val layout = mockk<SingleChildCircleLayout> {
        every { removeAllViews() } returns Unit
        every { addView(any()) } returns Unit
        every { childCount } returns 0
    }

    return layout
}

/**
 * Create a mock layout inflater
 *
 * @param layout [ViewGroup]: layout that will be associated with the manager. If none is provided, any will be used as the layout value.
 * Defaults to `null`.
 * @return [LayoutInflater] mocked inflater
 */
fun createMockLayoutInflater(layout: ViewGroup? = null): LayoutInflater {
    mockkStatic(LayoutInflater::class)

    val layoutInflater = mockk<LayoutInflater> {
        every { inflate(childResId, layout ?: any(), false) } returns mockk()
    }

    every { LayoutInflater.from(any()) } returns layoutInflater
    return layoutInflater
}
