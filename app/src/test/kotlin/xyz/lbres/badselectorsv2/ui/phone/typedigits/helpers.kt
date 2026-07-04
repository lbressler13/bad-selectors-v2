package xyz.lbres.badselectorsv2.ui.phone.typedigits

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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

fun withMockedDigitsOrder(digitsOrder: List<Int>, test: () -> Unit) {
    mockkStatic(IntRange::seededShuffled)
    with(mockk<IntRange>()) {
        every { IntRange(0, 9).seededShuffled() } returns digitsOrder
        test()
    }
}

// cannot launch scenario in before block due to mocking requirements
fun launchTypeDigitsFragment(): ActivityScenario<BaseActivity> {
    val scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
    navigateToSelector("Phone", "Type Digits")
    return scenario
}

fun clickRadioButton(index: Int) {
    radioButtons[index].perform(scrollTo(), click())
}
