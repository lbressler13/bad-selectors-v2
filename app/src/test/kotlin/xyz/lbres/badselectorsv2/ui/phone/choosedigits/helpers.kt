package xyz.lbres.badselectorsv2.ui.phone.choosedigits

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog
import xyz.lbres.badselectorsv2.utils.seededShuffled

val radioButtons = listOf(
    R.id.zeroButton,
    R.id.oneButton,
    R.id.twoButton,
    R.id.threeButton,
    R.id.fourButton,
    R.id.fiveButton,
    R.id.sixButton,
    R.id.sevenButton,
    R.id.eightButton,
    R.id.nineButton,
).map { onViewInDialog(withId(it)) }

/**
 * Mock the digits order returned by [seededShuffled] and run test
 *
 * @param digitsOrder [List]<Int>: order to use in mock
 * @param test () -> Unit: test to run
 */
fun withMockedDigitsOrder(digitsOrder: List<Int>, test: () -> Unit) {
    mockkStatic(IntRange::seededShuffled)
    with(mockk<IntRange>()) {
        every { IntRange(0, 9).seededShuffled() } returns digitsOrder
        test()
    }
    unmockkStatic(IntRange::seededShuffled)
}

/**
 * Click the radio button at the given index
 */
fun clickRadioButton(index: Int) {
    radioButtons[index].perform(scrollTo(), click())
}

/**
 * Launch app and navigate to the Choose Digits selector
 */
fun launchChooseDigitsFragment(): ActivityScenario<BaseActivity> {
    val scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
    navigateToSelector("Phone", "Choose Digits")
    return scenario
}
