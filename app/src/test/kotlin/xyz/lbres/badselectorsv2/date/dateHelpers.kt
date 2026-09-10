package xyz.lbres.badselectorsv2.date

import kotlin.test.assertEquals

/**
 * Check the current date set in the viewmodel
 */
fun checkDate(vm: BaseDateViewModel, month: Int? = null, day: Int? = null, year: Int? = null) {
    assertEquals(month, vm.month)
    assertEquals(day, vm.day)
    assertEquals(year, vm.year)
}
