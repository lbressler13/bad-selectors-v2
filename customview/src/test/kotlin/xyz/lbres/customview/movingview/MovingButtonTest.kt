package xyz.lbres.customview.movingview

import android.content.Context

class MovingButtonTest : AbstractMovingViewTest<MovingButton>() {
    override val createView: (Context) -> MovingButton = { MovingButton(it) }
}
