package xyz.lbres.customview.circlelayout

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CircleLayoutTest {
    @Test
    fun createLayout() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val layout = CircleLayout(context)
    }
}
