package xyz.lbres.badselectorsv2.utils

import xyz.lbres.kotlinutils.collection.list.IntList
import java.util.Date
import kotlin.random.Random

/**
 * Seeded random instance
 */
internal val random = Random(Date().time)

/**
 * Get random value using common app random
 *
 * @return [Int]: random value from range
 */
fun IntRange.seededRandom(): Int = random(random)

/**
 * Shuffle range using common app random
 *
 * @return [IntList]: shuffled values
 */
fun IntRange.seededShuffled(): IntList = shuffled(random)
