package xyz.lbres.badselectorsv2.ext.viewgroup

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

/**
 * Set onClickListener for all children of ViewGroup
 *
 * @param callback (View, Int) -> Unit: onClick function for children, which takes child view and index of child as parameters
 */
fun ViewGroup.setChildOnClickListener(callback: (view: View, index: Int) -> Unit) {
    children.forEachIndexed { index, view ->
        view.setOnClickListener { callback(view, index) }
    }
}
