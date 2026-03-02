package xyz.lbres.badselectorsv2.ext.viewgroup

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEachIndexed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.testutils.getContextEspresso
import xyz.lbres.kotlinutils.general.simpleIf

@RunWith(AndroidJUnit4::class)
class ViewGroupExtTest {
    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(BaseActivity::class.java)

    @Test
    fun setChildOnClickListener() {
        val viewGroup: ViewGroup = LinearLayout(getContextEspresso(activityRule))

        val clickAt: (Int) -> Unit = { viewGroup.getChildAt(it).callOnClick() }

        val expectedValues = Array(4) { "_" }
        val checkValues = {
            viewGroup.forEachIndexed { index, view ->
                view as TextView
                assertEquals(expectedValues[index], view.text)
            }
        }

        repeat(4) {
            val view = TextView(getContextEspresso(activityRule))
            view.text = "_"
            viewGroup.addView(view)
        }

        checkValues()

        viewGroup.setChildOnClickListener { view, index ->
            view as TextView
            view.text = simpleIf(index < 2, 9 - index, index - 2).toString()
        }

        clickAt(1)
        expectedValues[1] = "8"
        checkValues()

        clickAt(3)
        expectedValues[3] = "1"
        checkValues()

        var total = 0
        viewGroup.setChildOnClickListener { _, index -> total += index }
        clickAt(1)
        assertEquals(1, total)
        clickAt(3)
        clickAt(2)
        assertEquals(6, total)
        clickAt(0)
        clickAt(0)
        clickAt(1)
        assertEquals(7, total)
    }
}
