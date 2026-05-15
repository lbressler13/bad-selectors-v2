package xyz.lbres.badselectorsv2.phone

import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.databinding.ComponentPhoneNumberBinding
import xyz.lbres.badselectorsv2.ext.string.underlined
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.kotlinutils.general.simpleIf

/**
 * Fragment containing functionality that is used by all phone selectors
 */
abstract class BasePhoneFragment : Fragment() {
    protected abstract val phoneViewModel: BasePhoneViewModel
    protected abstract val digitsLayout: ComponentPhoneNumberBinding

    protected open var underlineDigits: Boolean = true

    protected val emptyDigit = "_"

    /**
     * Display the digit at a specific index in its digit view
     *
     * @param index [Int]: index of digit to display
     */
    protected fun displayDigitAtIndex(
        index: Int,
        phoneNumber: List<Int?> = phoneViewModel.digits,
        digitViews: List<TextView> = getDigitViews(),
        overrideUnderline: Boolean = underlineDigits,
    ) {
        val digit = phoneNumber[index]
        val view = digitViews[index]
        val text = if (digit == null || digit !in digitsRange) {
            emptyDigit
        } else {
            digit.toString()
        }
        view.text = simpleIf(overrideUnderline, text.underlined(), text)
    }

    protected fun getDigitViews(layout: ComponentPhoneNumberBinding = digitsLayout): List<TextView> {
        return listOf(
            layout.digit0,
            layout.digit1,
            layout.digit2,
            layout.digit3,
            layout.digit4,
            layout.digit5,
            layout.digit6,
            layout.digit7,
            layout.digit8,
            layout.digit9,
        )
    }

    /**
     * Display full phone number in the digit views
     */
    protected fun displayPhoneNumber(
        phoneNumber: List<Int?> = phoneViewModel.digits,
        layout: ComponentPhoneNumberBinding = digitsLayout,
        overrideUnderline: Boolean = underlineDigits,
    ) {
        val views = getDigitViews(layout)
        digitsRange.forEach { displayDigitAtIndex(it, phoneNumber, views, overrideUnderline) }
    }
}
