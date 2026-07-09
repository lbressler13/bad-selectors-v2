package xyz.lbres.customview.utils

import java.util.Date
import kotlin.random.Random

/**
 * Create a seeded [Random]
 */
internal fun createRandom() = Random(Date().time)
