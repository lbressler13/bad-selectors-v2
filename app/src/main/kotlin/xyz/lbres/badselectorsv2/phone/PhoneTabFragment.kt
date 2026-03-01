package xyz.lbres.badselectorsv2.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.FragmentPhoneTabBinding

class PhoneTabFragment : TabFragment() {
    override var metadata = PhoneTabFragment.metadata

    override var actionBarTitleResId: Int = R.string.appbar_title_phone
    override var navToHomeResId: Int? = R.id.navigatePhoneToHome

    private lateinit var binding: FragmentPhoneTabBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPhoneTabBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        val metadata = Metadata(
            R.string.title_phone,
            listOf(R.string.title_shuffle_circle),
            R.id.navigateHomeToPhone,
        )
    }
}
