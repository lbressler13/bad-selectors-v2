package xyz.lbres.badselectorsv2.utils

import xyz.lbres.kotlinutils.list.IntList
import java.util.Date
import kotlin.random.Random

fun createRandom() = Random(Date().time)

/**
 * Get random value using common app random
 *
 * @return [Int]: random value from range
 */
fun IntRange.seededRandom(): Int = random(createRandom())

/**
 * Shuffle range using common app random
 *
 * @return [IntList]: shuffled values
 */
fun IntRange.seededShuffled(): IntList = shuffled(createRandom())
