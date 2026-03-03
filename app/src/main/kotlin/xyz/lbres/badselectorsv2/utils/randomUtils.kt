package xyz.lbres.badselectorsv2.utils

import xyz.lbres.kotlinutils.list.IntList
import java.util.Date
import kotlin.random.Random

fun random() = Random(Date().time)

/**
 * Get random value using common app random
 *
 * @return [Int]: random value from range
 */
// fun IntRange.seededRandom(): Int = random(random)
fun IntRange.seededRandom(): Int = seededRandomHelper(this)

/**
 * Shuffle range using common app random
 *
 * @return [IntList]: shuffled values
 */
// fun IntRange.seededShuffled(): IntList = shuffled(random)
fun IntRange.seededShuffled(): IntList = seededShuffledHelper(this)

// TODO revert this
fun seededShuffledHelper(range: IntRange): IntList = range.shuffled(random())
fun seededRandomHelper(range: IntRange) = range.random(random())
