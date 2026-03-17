package xyz.lbres.badselectorsv2.ext.textview

import android.widget.TextView

fun TextView.textToInt(): Int? = text.toString().trim().toIntOrNull()
