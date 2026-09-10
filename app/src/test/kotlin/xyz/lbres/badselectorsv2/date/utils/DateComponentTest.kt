package xyz.lbres.badselectorsv2.date.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DateComponentTest {
    @Test
    fun testNext() {
        // month
        var component = DateComponent.MONTH
        assertEquals(DateComponent.DAY, component.next())

        // day
        component = DateComponent.DAY
        assertEquals(DateComponent.YEAR, component.next())

        // year
        component = DateComponent.YEAR
        assertNull(component.next())

        // first half year
        component = DateComponent.FIRST_HALF_YEAR
        assertEquals(DateComponent.SECOND_HALF_YEAR, component.next())

        // second half year
        component = DateComponent.SECOND_HALF_YEAR
        assertNull(component.next())
    }
}
