package xyz.lbres.badselectorsv2.ui.calculator.randomenabled

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.calculator.randomenabled.RandomEnabler
import xyz.lbres.badselectorsv2.ui.calculator.backspaceButton
import xyz.lbres.badselectorsv2.ui.calculator.clearButton
import xyz.lbres.badselectorsv2.ui.calculator.equalsButton
import xyz.lbres.badselectorsv2.ui.calculator.mainText
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.calculator.operatorButtons
import xyz.lbres.badselectorsv2.ui.calculator.operators
import xyz.lbres.badselectorsv2.ui.testutils.isDisabled
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.kotlinutils.general.simpleIf

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class RandomEnabledFragmentTest {
    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        val numberValues = listOf(
            listOf(true, true, false, false, true, false, true, false, false, true),
        )
        val opValues = listOf(listOf(false, false, true, true))
        mockRandomEnabler(numberValues.flatten(), opValues.flatten())

        launchFragment()
        mainText.check(matches(withText("")))
        checkEnabledButtons(numberValues[0], opValues[0])
    }

    private fun mockRandomEnabler(numberValues: List<Boolean>, operatorValues: List<Boolean>) {
        mockkConstructor(RandomEnabler::class)
        every { constructedWith<RandomEnabler>().isDigitEnabled(any()) } returnsMany numberValues
        every { constructedWith<RandomEnabler>().isOperatorEnabled(any()) } returnsMany operatorValues
        justRun { constructedWith<RandomEnabler>().update() }
    }

    // cannot launch scenario in before block due to mocking requirements
    private fun launchFragment(): ActivityScenario<BaseActivity> {
        val scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Calculator", "Random Enabled")
        return scenario
    }

    private fun checkEnabledButtons(
        enabledNumbers: List<Boolean>,
        enabledOperators: List<Boolean>,
        equalsEnabled: Boolean = true,
        backspaceEnabled: Boolean = true,
    ) {
        val enabledMatcher = isEnabled()
        val disabledMatcher = isDisabled()
        val buttonMatcher = { enabled: Boolean -> simpleIf(enabled, enabledMatcher, disabledMatcher) }

        numberButtons.forEachIndexed { index, button ->
            button.check(matches(buttonMatcher(enabledNumbers[index])))
        }

        operators.forEachIndexed { index, operator ->
            val button = operatorButtons[operator]!!
            button.check(matches(buttonMatcher(enabledOperators[index])))
        }

        equalsButton.check(matches(buttonMatcher(equalsEnabled)))
        backspaceButton.check(matches(buttonMatcher(backspaceEnabled)))
        clearButton.check(matches(buttonMatcher(true)))
    }
}
