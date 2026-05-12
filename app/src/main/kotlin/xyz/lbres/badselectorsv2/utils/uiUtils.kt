package xyz.lbres.badselectorsv2.utils

import android.content.Context
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import androidx.core.widget.ImageViewCompat
import xyz.lbres.badselectorsv2.R

/**
 * Underline an entire piece of text
 *
 * @param text [String]: text to underline
 * @return [SpannableString]: the underlined text
 */
fun createUnderlineText(text: String): SpannableString {
    val spannableString = SpannableString(text)
    spannableString.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    return spannableString
}

/**
 * Set the tint color on [button], if it is an [ImageButton].
 *
 * @param button [View]
 * @param color [Int]
 */
fun setImageButtonTint(button: View, color: Int) {
    if (button is ImageButton) {
        ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(color))
    }
}

/**
 * Get theme colorPrimary color value.
 *
 * @param context [Context]
 * @return [Int]: the color value
 */
fun getColorPrimary(context: Context) = getThemeColor(context, com.google.android.material.R.attr.colorPrimary)

/**
 * Get theme colorOnPrimary color value.
 *
 * @param context [Context]
 * @return [Int]: the color value
 */
fun getColorOnPrimary(context: Context) = getThemeColor(context, com.google.android.material.R.attr.colorOnPrimary)

/**
 * Get theme colorOnBackground color value.
 *
 * @param context [Context]
 * @return [Int]: the color value
 */
fun getColorOnBackground(context: Context) = getThemeColor(
    context,
    com.google.android.material.R.attr.colorOnBackground,
)

/**
 * Get theme disabledForeground color value.
 *
 * @param context [Context]
 * @return [Int]: the color value
 */
fun getDisabledForeground(context: Context) = getThemeColor(context, R.attr.disabledForeground)

/**
 * Get a theme color value
 *
 * @param context [Context]
 * @param attrResId [Int]: id of resource
 * @return [Int] the color value
 */
private fun getThemeColor(context: Context, attrResId: Int): Int {
    val color = TypedValue()
    context.theme.resolveAttribute(attrResId, color, true)
    return color.data
}
