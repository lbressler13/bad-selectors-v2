package xyz.lbres.badselectorsv2.utils

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class UiUtilsTest {
    @Test
    fun testSetImageButtonTint() {
        var view: View = ImageButton(ApplicationProvider.getApplicationContext())
        setImageButtonTint(view, 123)
        view as ImageButton
        assertEquals(123, view.imageTintList!!.defaultColor)

        view = TextView(ApplicationProvider.getApplicationContext())
        setImageButtonTint(view, 123)
        assertNull(view.backgroundTintList)
    }
}
