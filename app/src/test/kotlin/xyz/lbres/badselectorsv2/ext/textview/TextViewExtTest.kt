package xyz.lbres.badselectorsv2.ext.textview

import android.content.Context
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class TextViewExtTest {
    @Test
    fun textToInt() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val view = TextView(context)
        assertNull(view.textToInt())

        // int
        view.text = "0"
        assertEquals(0, view.textToInt())

        view.text = "001"
        assertEquals(1, view.textToInt())

        view.text = "  43   "
        assertEquals(43, view.textToInt())

        view.text = "-643"
        assertEquals(-643, view.textToInt())

        // null
        view.text = ""
        assertNull(view.textToInt())

        view.text = "abc"
        assertNull(view.textToInt())

        view.text = "1a"
        assertNull(view.textToInt())

        view.text = "1."
        assertNull(view.textToInt())

        view.text = "1.3"
        assertNull(view.textToInt())
    }
}
