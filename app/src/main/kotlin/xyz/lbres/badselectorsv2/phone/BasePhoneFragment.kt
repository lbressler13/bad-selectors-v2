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
    protected lateinit var digitViews: List<TextView>

    protected open var underlineDigits: Boolean = true

    protected val emptyDigit = "_"

    /**
     * Initialize the TextViews for each digit and display current phone number
     *
     * @param digitsLayout [ComponentPhoneNumberBinding]: layout containing TextViews for all digits
     */
    protected fun initDigitViews(digitsLayout: ComponentPhoneNumberBinding) {
        digitViews = listOf(
            digitsLayout.digit0,
            digitsLayout.digit1,
            digitsLayout.digit2,
            digitsLayout.digit3,
            digitsLayout.digit4,
            digitsLayout.digit5,
            digitsLayout.digit6,
            digitsLayout.digit7,
            digitsLayout.digit8,
            digitsLayout.digit9,
        )

        displayPhoneNumber()
    }

    /**
     * Display the digit at a specific index in its digit view
     *
     * @param index [Int]: index of digit to display
     */
    protected fun displayDigitAtIndex(index: Int) {
        val digit = phoneViewModel.getDigitAt(index)
        val view = digitViews[index]
        val text = digit?.toString() ?: emptyDigit
        view.text = simpleIf(underlineDigits, text.underlined(), text)
    }

    /**
     * Display full phone number in the digit views
     */
    protected fun displayPhoneNumber() {
        digitsRange.forEach { displayDigitAtIndex(it) }
    }
}
