package xyz.lbres.badselectorsv2.ext.view

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewExtTest {

    @Test
    fun visible() {
        val context: Context = ApplicationProvider.getApplicationContext()
        var view = View(context)
        view.visibility = View.INVISIBLE
        view.visible()
        assertEquals(View.VISIBLE, view.visibility)

        view = View(context)
        view.visibility = View.GONE
        view.visible()
        assertEquals(View.VISIBLE, view.visibility)

        view = View(context)
        view.visibility = View.VISIBLE
        view.visible()
        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun invisible() {
        val context: Context = ApplicationProvider.getApplicationContext()
        var view = View(context)
        view.visibility = View.VISIBLE
        view.invisible()
        assertEquals(View.INVISIBLE, view.visibility)

        view = View(context)
        view.visibility = View.GONE
        view.invisible()
        assertEquals(View.INVISIBLE, view.visibility)

        view = View(context)
        view.visibility = View.INVISIBLE
        view.invisible()
        assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun gone() {
        val context: Context = ApplicationProvider.getApplicationContext()
        var view = View(context)
        view.visibility = View.VISIBLE
        view.gone()
        assertEquals(View.GONE, view.visibility)

        view = View(context)
        view.visibility = View.INVISIBLE
        view.gone()
        assertEquals(View.GONE, view.visibility)

        view = View(context)
        view.visibility = View.GONE
        view.gone()
        assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun enable() {
        val context: Context = ApplicationProvider.getApplicationContext()
        var view = View(context)
        view.isEnabled = false
        view.enable()
        assertTrue(view.isEnabled)

        view = View(context)
        view.isEnabled = true
        view.enable()
        assertTrue(view.isEnabled)
    }

    @Test
    fun disable() {
        val context: Context = ApplicationProvider.getApplicationContext()
        var view = View(context)
        view.isEnabled = true
        view.disable()
        assertFalse(view.isEnabled)

        view = View(context)
        view.isEnabled = false
        view.disable()
        assertFalse(view.isEnabled)
    }

    @Test
    fun halfOpacity() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val view = View(context)
        assertEquals(1f, view.alpha)
        view.halfOpacity()
        assertEquals(0.5f, view.alpha)

        view.alpha = 0f
        view.halfOpacity()
        assertEquals(0.5f, view.alpha)

        view.alpha = 0.5f
        view.halfOpacity()
        assertEquals(0.5f, view.alpha)
    }

    @Test
    fun fullOpacity() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val view = View(context)
        assertEquals(1f, view.alpha)
        view.fullOpacity()
        assertEquals(1f, view.alpha)

        view.alpha = 0f
        view.fullOpacity()
        assertEquals(1f, view.alpha)

        view.alpha = 0.5f
        view.fullOpacity()
        assertEquals(1f, view.alpha)
    }
}
