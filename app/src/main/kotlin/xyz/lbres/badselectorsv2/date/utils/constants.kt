package xyz.lbres.badselectorsv2.date.utils

/**
 * Number of days based on month
 */
val daysPerMonth: List<Int> = listOf(
    31, // January
    29, // February
    31, // March
    30, // April
    31, // May
    30, // June
    31, // July
    31, // August
    30, // September
    31, // October
    30, // November
    31, // December
)

/**
 * Date components
 */
const val MONTH = "month"
const val DAY = "day"
const val YEAR = "year"

/**
 * Date values
 */
const val maxMonth = 12
const val maxDay = 31
val monthRange: IntRange = 1..maxMonth
val dayRange: IntRange = 1..maxDay
