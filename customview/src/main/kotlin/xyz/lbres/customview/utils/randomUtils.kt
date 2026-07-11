package xyz.lbres.customview.utils

import java.util.Date
import kotlin.random.Random

/**
 * Create a seeded [Random]
 */
internal fun createRandom() = Random(Date().time)

/**
 * Get random value using common app random
 *
 * @return [Int]: random value from range
 */
fun IntRange.seededRandom(): Int = random(createRandom())

/**
 * Get random value using common app random
 *
 * @return [Int]: random value from set
 */
fun Set<Int>.seededRandom(): Int = random(createRandom())
