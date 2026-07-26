package xyz.lbres.customview.movingview

import android.content.Context

class MovingTextViewTest : AbstractMovingViewTest<MovingTextView>() {
    override val createView: (Context) -> MovingTextView = { MovingTextView(it) }
}
