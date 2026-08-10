package xyz.lbres.badselectorsv2.date.utils

enum class DateComponent(val label: String) {
    DAY("day"),
    MONTH("month"),
    YEAR("year"),
    FIRST_HALF_YEAR("first 2 digits of the year"),
    SECOND_HALF_YEAR("last 2 digits of the year"),
}
