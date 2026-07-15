package xyz.lbres.customview.utils

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
 * Get random value using common app random
 *
 * @return [Int]: random value from set
 */
fun Set<Int>.seededRandom(): Int = random(random)
