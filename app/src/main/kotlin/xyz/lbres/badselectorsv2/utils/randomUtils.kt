package xyz.lbres.badselectorsv2.utils

import xyz.lbres.kotlinutils.list.IntList
import java.util.Date
import kotlin.random.Random

val random = Random(Date().time)

/**
 * Get random value using common app random
 *
 * @return [Int]: random value from collection
 */
fun <E> Collection<E>.seededRandom(): E = random(random)

/**
 * Get random value using common app random
 *
 * @return [Int]: random value from range
 */
fun IntRange.seededRandom(): Int = random(random)

/**
 * Shuffle collection using common app random
 *
 * @return [List]<E>: shuffled values
 */
fun <E> Collection<E>.seededShuffled(): List<E> = shuffled(random)

/**
 * Shuffle range using common app random
 *
 * @return [IntList]: shuffled values
 */
fun IntRange.seededShuffled(): IntList = shuffled(random)
