package xyz.lbres.badselectorsv2.date

import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.databinding.ComponentDateNumbersBinding
import xyz.lbres.badselectorsv2.ext.string.underlined

/**
 * Fragment containing functionality that is used by all date selectors
 */
abstract class BaseDateFragment : Fragment() {
    protected abstract val dateViewModel: BaseDateViewModel
    protected abstract val dateNumbersLayout: ComponentDateNumbersBinding

    /**
     * Add the current day to the date numbers layout
     */
    protected open fun displayDay() {
        val views = listOf(dateNumbersLayout.day0, dateNumbersLayout.day1)
        val dayToDisplay = if (dateViewModel.day == null) {
            dateViewModel.day
        } else {
            dateViewModel.day!! + 1
        }
        addNumberToViews(dayToDisplay, views)
    }

    /**
     * Add the current month to the date numbers layout
     */
    protected open fun displayMonth() {
        val views = listOf(dateNumbersLayout.month0, dateNumbersLayout.month1)
        val monthToDisplay = if (dateViewModel.month == null) {
            dateViewModel.month
        } else {
            dateViewModel.month!! + 1
        }
        addNumberToViews(monthToDisplay, views)
    }

    /**
     * Add the current year to the date numbers layout
     */
    protected open fun displayYear() {
        val views =
            listOf(dateNumbersLayout.year0, dateNumbersLayout.year1, dateNumbersLayout.year2, dateNumbersLayout.year3)
        addNumberToViews(dateViewModel.year, views)
    }

    /**
     * Pad a number with zeroes and add each digit to the textview, or display empty spots if the number is null
     *
     * @param number [Int]?: number to display, can be null
     * @param views List<[TextView]>: list of views to add number to
     */
    protected fun addNumberToViews(number: Int?, views: List<TextView>) {
        val length = views.size
        val text: String = number?.toString()?.padStart(length, '0') ?: "_".repeat(length)

        views.forEachIndexed { index, view ->
            view.text = text[index].toString().underlined()
        }
    }

    /**
     * Add all components of the current date to the date numbers layout
     */
    protected fun displayDate() {
        displayMonth()
        displayDay()
        displayYear()
    }
}
