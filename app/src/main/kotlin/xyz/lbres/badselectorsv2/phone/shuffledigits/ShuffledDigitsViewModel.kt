package xyz.lbres.badselectorsv2.phone.shuffledigits

import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.kotlinutils.list.IntList

class ShuffledDigitsViewModel : BasePhoneViewModel() {
    private val digitOrders: Array<IntList> = Array(10) { digitsRange.toList() }

    init {
        setAllDigitOrders()
    }

    private fun setAllDigitOrders() {
        digitOrders.indices.forEach { digitOrders[it] = digitsRange.seededShuffled() }
    }

    fun setDigitAt(index: Int, value: Int) {
        _digits[index] = digitOrders[index][value]
    }

    override fun resetData() {
        super.resetData()
        setAllDigitOrders()
    }
}
