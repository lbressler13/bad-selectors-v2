package xyz.lbres.customview.interfaces.singlechildlayout

import android.content.res.TypedArray
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import xyz.lbres.customview.circlelayout.AbstractSingleChildCircleLayout
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import xyz.lbres.customview.ext.typedarray.getResourceIdOrNull
import xyz.lbres.testutils.assertFailsWithMessage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SingleChildLayoutManagerTest {

    @BeforeTest
    fun setupMockk() {
        mockkStatic(TypedArray::getResourceIdOrNull)
        mockkStatic(TypedArray::getIntOrNull)
    }

    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    @Test
    fun testAllAttributesPassed() {
        val mockContext = createMockContext(0, true)
        val manager = SingleChildLayoutManager(mockContext, null)
        assertFalse(manager.childrenInitialized)
    }

    @Test
    fun testNoChildLayout() {
        val mockContext = createMockContext(0, false)
        val expectedError = "SingleChildLayout requires numChildren and childLayout"
        assertFailsWithMessage<IllegalStateException>(expectedError) {
            SingleChildLayoutManager(mockContext, null)
        }
    }

    @Test
    fun testNoNumChildren() {
        val mockContext = createMockContext(null, true)
        val expectedError = "SingleChildLayout requires numChildren and childLayout"
        assertFailsWithMessage<IllegalStateException>(expectedError) {
            SingleChildLayoutManager(mockContext, null)
        }
    }

    @Test
    fun testInitializeChildrenWithExistingChildren() {
        val layout = mockk<AbstractSingleChildCircleLayout> {
            every { addView(any()) } returns Unit
            every { childCount } returns 1
        }
        val manager = SingleChildLayoutManager(createMockContext(numChildren = 5, true), null)

        val expectedError = "SingleChildLayout cannot be created with children"
        assertFailsWithMessage<IllegalStateException>(expectedError) { manager.initializeChildren(layout) }
    }

    @Test
    fun testInitializeChildren() {
        val context = createMockContext(numChildren = 5, true)
        val layout = createMockLayout(context)
        val layoutInflater = createMockLayoutInflater(layout)

        var manager = SingleChildLayoutManager(context, null)
        manager.initializeChildren(layout)
        assertTrue(manager.childrenInitialized)

        verify(exactly = 5) { layoutInflater.inflate(childResId, layout, false) }
        verify(exactly = 5) { layout.addView(allAny()) }

        manager = SingleChildLayoutManager(createMockContext(0, true), null)
        manager.initializeChildren(layout)
        assertTrue(manager.childrenInitialized)

        verify(exactly = 5) { layoutInflater.inflate(childResId, layout, false) }
        verify(exactly = 5) { layout.addView(allAny()) }

        manager = SingleChildLayoutManager(createMockContext(9, true), null)
        manager.initializeChildren(layout)
        assertTrue(manager.childrenInitialized)

        verify(exactly = 14) { layoutInflater.inflate(childResId, layout, false) }
        verify(exactly = 14) { layout.addView(allAny()) }
    }

    @Test
    fun testModifyChildrenAfterInit() {
        val context = createMockContext(5, true)
        val layout = createMockLayout(context)
        createMockLayoutInflater(layout)

        val manager = SingleChildLayoutManager(context, null)
        manager.initializeChildren(layout)
        val expectedError = "Cannot modify children of SingleChildLayout"

        assertFailsWithMessage<UnsupportedOperationException>(expectedError) {
            manager.modifyChildren {}
        }

        // throws correct exception
        assertFailsWithMessage<UnsupportedOperationException>(expectedError) {
            fun div(x: Int, y: Int) = x / y
            manager.modifyChildren { div(1, 0) }
        }

        var modified = false
        assertFailsWithMessage<UnsupportedOperationException>(expectedError) {
            manager.modifyChildren { modified = true }
        }
        assertFalse(modified)
    }
}
