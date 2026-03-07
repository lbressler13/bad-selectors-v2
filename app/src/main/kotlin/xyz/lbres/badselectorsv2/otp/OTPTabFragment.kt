package xyz.lbres.badselectorsv2.otp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.TabFragmentBinding

class OTPTabFragment : TabFragment() {
    override var metadata = OTPTabFragment.metadata

    override var actionBarTitleResId: Int = R.string.appbar_title_otp
    override var navToHomeResId: Int? = R.id.navigateOtpToHome
    override var navToPhoneResId: Int? = R.id.navigateOtpToPhone
    override var navToCalcResId: Int? = R.id.navigateOtpToCalc
    override var navToDateResId: Int? = R.id.navigateOtpToDate

    override lateinit var binding: TabFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TabFragmentBinding.inflate(layoutInflater)
        setupBaseFragment()
        setUpTabs()
        return binding.root
    }

    /**
     * Get fragment for a tab position
     *
     * @param position [Int]
     * @return [Fragment]
     */
    override fun getFragmentFromPosition(position: Int): Fragment {
        throw NotImplementedError("OTPTabFragment has no tabs")
    }

    companion object {
        val metadata = Metadata(
            R.string.title_otp,
            emptyList(),
            R.id.navigateHomeToOtp,
        )
    }
}
