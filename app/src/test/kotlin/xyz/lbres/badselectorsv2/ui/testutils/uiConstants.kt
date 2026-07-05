package xyz.lbres.badselectorsv2.ui.testutils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import xyz.lbres.badselectorsv2.R

val actionBar = onView(withId(R.id.actionBar))
