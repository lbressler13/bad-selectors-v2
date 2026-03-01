package xyz.lbres.badselectorsv2.phone

import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment

class PhoneTabFragment : TabFragment() {
    override var metadata = PhoneTabFragment.metadata

    override var actionBarTitleResId: Int = R.string.appbar_title_phone
    override var navToHomeResId: Int? = R.id.navigatePhoneToHome

    companion object {
        val metadata = Metadata(
            R.string.title_phone,
            listOf(R.string.title_shuffle_circle),
            R.id.navigateHomeToPhone,
        )
    }
}
