package xyz.lbres.badselectorsv2.ext.string

import android.text.style.UnderlineSpan
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StringExtTest {
    @Test
    fun underlined() {
        // must be tested in espresso tests to avoid compile errors from not mocking SpannableString
        val string = "Hello world"
        val underlined = string.underlined()
        val spans = underlined.getSpans(0, string.length, UnderlineSpan::class.java)
        assertEquals(1, spans.size)

        val span = spans[0]
        assertEquals(0, underlined.getSpanStart(span))
        assertEquals(string.length, underlined.getSpanEnd(span))
    }
}
