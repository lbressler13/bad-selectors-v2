package xyz.lbres.badselectorsv2.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.TabFragmentBinding

class CalculatorTabFragment : TabFragment() {
    override var metadata = CalculatorTabFragment.metadata

    override var actionBarTitleResId: Int = R.string.appbar_title_calc
    override var navToHomeResId: Int? = R.id.navigateCalcToHome
    override var navToPhoneResId: Int? = R.id.navigateCalcToPhone
    override var navToDateResId: Int? = R.id.navigateCalcToDate
    override var navToOtpResId: Int? = R.id.navigateCalcToOtp

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
        throw NotImplementedError("CalculatorTabFragment has no tabs")
    }

    companion object {
        val metadata = Metadata(
            R.string.title_calc,
            emptyList(),
            R.id.navigateHomeToCalc,
        )
    }
}
