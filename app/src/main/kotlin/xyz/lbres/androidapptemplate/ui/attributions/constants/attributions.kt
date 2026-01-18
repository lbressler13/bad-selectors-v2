package xyz.lbres.androidapptemplate.ui.attributions.constants

import xyz.lbres.androidapptemplate.R
import xyz.lbres.androidapptemplate.ui.attributions.AuthorAttribution
import xyz.lbres.androidapptemplate.ui.attributions.ImageAttribution

/**
 * Constant lists of image attributions
 */

private val freepikImages = listOf(
    ImageAttribution(R.drawable.ic_chevron_down, R.string.chevron_down_cd, chevronDownUrl),
    ImageAttribution(R.drawable.ic_chevron_left, R.string.chevron_left_cd, chevronLeftUrl),
    ImageAttribution(R.drawable.ic_chevron_right, R.string.chevron_right_cd, chevronRightUrl),
    ImageAttribution(R.drawable.ic_chevron_up, R.string.chevron_up_cd, chevronUpUrl),
    ImageAttribution(R.drawable.ic_info, R.string.info_cd, infoUrl),
    ImageAttribution(R.drawable.ic_settings, R.string.settings_cd, settingsUrl),
)

private val ilhamFitrotulHayatImages = listOf(
    ImageAttribution(R.drawable.ic_close, R.string.close_cd, closeUrl),
)

/**
 * Constant list of author attributions, including the above image attributions
 */

val authorAttributions = listOf(
    AuthorAttribution("Freepik", freepikUrl, freepikImages),
    AuthorAttribution("Ilham Fitrotul Hayat", ilhamFitrotulHayatUrl, ilhamFitrotulHayatImages),
)
