package xyz.lbres.customview.interfaces.singlechildlayout

import android.content.res.TypedArray
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import xyz.lbres.customview.circlelayout.SingleChildCircleLayout
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import xyz.lbres.customview.ext.typedarray.getResourceIdOrNull
import xyz.lbres.customview.interfaces.singlechildlayout.SingleChildLayoutManager.ChildInitializationState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        val manager = SingleChildLayoutManager({ createMockLayout() }, mockContext, null)
        assertEquals(ChildInitializationState.NOT_STARTED, manager.childInitializationState)
    }

    @Test
    fun testNoChildLayout() {
        val mockContext = createMockContext(0, false)
        val expectedError = "SingleChildLayout requires numChildren and childLayout"
        val exception = assertFailsWith<IllegalStateException> {
            SingleChildLayoutManager({ createMockLayout() }, mockContext, null)
        }
        assertEquals(expectedError, exception.message)
    }

    @Test
    fun testNoNumChildren() {
        val mockContext = createMockContext(null, true)
        val expectedError = "SingleChildLayout requires numChildren and childLayout"
        val exception = assertFailsWith<IllegalStateException> {
            SingleChildLayoutManager({ createMockLayout() }, mockContext, null)
        }
        assertEquals(expectedError, exception.message)
    }

    @Test
    fun testInitializeChildrenWithExistingChildren() {
        val layout = mockk<SingleChildCircleLayout> {
            every { addView(any()) } returns Unit
            every { childCount } returns 1
        }
        val manager = SingleChildLayoutManager({ layout }, createMockContext(numChildren = 5, true), null)

        val expectedError = "SingleChildLayout cannot be created with children"
        val exception = assertFailsWith<IllegalStateException> { manager.initializeChildren() }
        assertEquals(expectedError, exception.message)
    }

    @Test
    fun testInitializeChildren() {
        val layout = createMockLayout()
        val layoutInflater = createMockLayoutInflater(layout)

        var manager = SingleChildLayoutManager({ layout }, createMockContext(numChildren = 5, true), null)
        manager.initializeChildren()
        assertEquals(ChildInitializationState.COMPLETE, manager.childInitializationState)

        verify(exactly = 5) { layoutInflater.inflate(childResId, layout, false) }
        verify(exactly = 5) { layout.addView(allAny()) }

        manager = SingleChildLayoutManager({ layout }, createMockContext(0, true), null)
        manager.initializeChildren()
        assertEquals(ChildInitializationState.COMPLETE, manager.childInitializationState)

        verify(exactly = 5) { layoutInflater.inflate(childResId, layout, false) }
        verify(exactly = 5) { layout.addView(allAny()) }

        manager = SingleChildLayoutManager({ layout }, createMockContext(9, true), null)
        manager.initializeChildren()
        assertEquals(ChildInitializationState.COMPLETE, manager.childInitializationState)

        verify(exactly = 14) { layoutInflater.inflate(childResId, layout, false) }
        verify(exactly = 14) { layout.addView(allAny()) }
    }

    @Test
    fun testModifyChildrenBeforeInit() {
        val manager = SingleChildLayoutManager(mockk(), createMockContext(5, true), null)

        var modified = false
        manager.modifyChildren { modified = true }
        assertTrue(modified)

        val result = manager.modifyChildren { "hello world" }
        assertEquals("hello world", result)

        // throws correct exception
        assertFailsWith<ArithmeticException> { manager.modifyChildren { 1 / 0 } }
    }

    @Test
    fun testModifyChildrenAfterInit() {
        val layout = createMockLayout()
        createMockLayoutInflater(layout)

        val manager = SingleChildLayoutManager({ layout }, createMockContext(5, true), null)
        manager.initializeChildren()
        val expectedError = "Cannot modify children of SingleChildLayout"

        var exception = assertFailsWith<UnsupportedOperationException> {
            manager.modifyChildren {}
        }
        assertEquals(expectedError, exception.message)

        // throws correct exception
        exception = assertFailsWith<UnsupportedOperationException> {
            manager.modifyChildren { 1 / 0 }
        }
        assertEquals(expectedError, exception.message)

        var modified = false
        exception = assertFailsWith<UnsupportedOperationException> {
            manager.modifyChildren { modified = true }
        }
        assertEquals(expectedError, exception.message)
        assertFalse(modified)
    }
}
