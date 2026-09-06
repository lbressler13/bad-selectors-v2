package xyz.lbres.badselectorsv2.date.utils

/**
 * Components of a date
 */
enum class DateComponent(val label: String) {
    MONTH("month") {
        override fun next(): DateComponent? = DAY
    },
    DAY("day") {
        override fun next(): DateComponent? = YEAR
    },
    YEAR("year") {
        override fun next(): DateComponent? = null
    },
    FIRST_HALF_YEAR("first 2 digits of the year") {
        override fun next(): DateComponent? = SECOND_HALF_YEAR
    },
    SECOND_HALF_YEAR("last 2 digits of the year") {
        override fun next(): DateComponent? = null
    }, ;

    /**
     * Get the date component following the current one. Can be null if there is no following component
     */
    abstract fun next(): DateComponent?
}
