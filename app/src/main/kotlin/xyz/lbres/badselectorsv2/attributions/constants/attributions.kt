package xyz.lbres.badselectorsv2.attributions.constants

import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.attributions.AuthorAttribution
import xyz.lbres.badselectorsv2.attributions.ImageAttribution

/**
 * Constant lists of image attributions
 */

private val freepikImages = listOf(
    ImageAttribution(R.drawable.ic_calculator, R.string.calculator_cd, calculatorUrl),
    ImageAttribution(R.drawable.ic_chevron_down, R.string.chevron_down_cd, chevronDownUrl),
    ImageAttribution(R.drawable.ic_chevron_left, R.string.chevron_left_cd, chevronLeftUrl),
    ImageAttribution(R.drawable.ic_chevron_right, R.string.chevron_right_cd, chevronRightUrl),
    ImageAttribution(R.drawable.ic_chevron_up, R.string.chevron_up_cd, chevronUpUrl),
    ImageAttribution(R.drawable.ic_home, R.string.home_cd, homeUrl),
    ImageAttribution(R.drawable.ic_info, R.string.info_cd, infoUrl),
    ImageAttribution(R.drawable.ic_restart, R.string.restart_cd, restartUrl),
    ImageAttribution(R.drawable.ic_settings, R.string.settings_cd, settingsUrl),
)

private val ilhamFitrotulHayatImages = listOf(
    ImageAttribution(R.drawable.ic_close, R.string.close_cd, closeUrl),
    ImageAttribution(R.drawable.ic_phone, R.string.phone_cd, phoneUrl),
)

private val prosymbolsPremiumImages = listOf(
    ImageAttribution(R.drawable.ic_calendar, R.string.calendar_cd, calendarUrl),
)

/**
 * Constant list of author attributions, including the above image attributions
 */

val authorAttributions = listOf(
    AuthorAttribution("Freepik", freepikUrl, freepikImages),
    AuthorAttribution("Ilham Fitrotul Hayat", ilhamFitrotulHayatUrl, ilhamFitrotulHayatImages),
    AuthorAttribution("Prosymbols Premium", prosymbolsPremiumUrl, prosymbolsPremiumImages),
)
