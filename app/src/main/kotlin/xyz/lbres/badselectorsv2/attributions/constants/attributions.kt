package xyz.lbres.badselectorsv2.attributions.constants

import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.attributions.AuthorAttribution
import xyz.lbres.badselectorsv2.attributions.ImageAttribution

/**
 * Constant lists of image attributions
 */

private val freepikImages = listOf(
    ImageAttribution(R.drawable.ic_calculator, R.string.calculator_cd, calculatorUrl),
    ImageAttribution(R.drawable.ic_cell_phone, R.string.cell_phone_cd, cellPhoneUrl),
    ImageAttribution(R.drawable.ic_chevron_down, R.string.chevron_down_cd, chevronDownUrl),
    ImageAttribution(R.drawable.ic_chevron_left, R.string.chevron_left_cd, chevronLeftUrl),
    ImageAttribution(R.drawable.ic_chevron_right, R.string.chevron_right_cd, chevronRightUrl),
    ImageAttribution(R.drawable.ic_chevron_up, R.string.chevron_up_cd, chevronUpUrl),
    ImageAttribution(R.drawable.ic_equals, R.string.equals_cd, equalsUrl),
    ImageAttribution(R.drawable.ic_home, R.string.home_cd, homeUrl),
    ImageAttribution(R.drawable.ic_info, R.string.info_cd, infoUrl),
    ImageAttribution(R.drawable.ic_minus, R.string.minus_cd, minusUrl),
    ImageAttribution(R.drawable.ic_plus, R.string.plus_cd, plusUrl),
    ImageAttribution(R.drawable.ic_restart, R.string.restart_cd, restartUrl),
    ImageAttribution(R.drawable.ic_settings, R.string.settings_cd, settingsUrl),
)

private val iconSmartImages = listOf(
    ImageAttribution(R.drawable.ic_divide, R.string.divide_cd, divideUrl),
)

private val ilhamFitrotulHayatImages = listOf(
    ImageAttribution(R.drawable.ic_arrow_left, R.string.backspace_cd, arrowLeftUrl),
    ImageAttribution(R.drawable.ic_close, R.string.close_cd, closeUrl),
    ImageAttribution(R.drawable.ic_phone, R.string.phone_cd, phoneUrl),
    ImageAttribution(R.drawable.ic_x, R.string.close_cd, xUrl),
)

private val pixelPerfectImages = listOf(
    ImageAttribution(R.drawable.ic_download, R.string.store_cd, downloadUrl),
    ImageAttribution(R.drawable.ic_minus_circle, R.string.minus_cd, minusCircleUrl),
    ImageAttribution(R.drawable.ic_plus_circle, R.string.plus_cd, plusCircleUrl),
    ImageAttribution(R.drawable.ic_times, R.string.times_cd, timesUrl),
)

private val prosymbolsPremiumImages = listOf(
    ImageAttribution(R.drawable.ic_calendar, R.string.calendar_cd, calendarUrl),
)

/**
 * Constant list of author attributions, including the above image attributions
 */

val authorAttributions = listOf(
    AuthorAttribution("Freepik", freepikUrl, freepikImages),
    AuthorAttribution("Icon Smart", iconSmartUrl, iconSmartImages),
    AuthorAttribution("Ilham Fitrotul Hayat", ilhamFitrotulHayatUrl, ilhamFitrotulHayatImages),
    AuthorAttribution("Pixel perfect", pixelPerfectUrl, pixelPerfectImages),
    AuthorAttribution("Prosymbols Premium", prosymbolsPremiumUrl, prosymbolsPremiumImages),
)
