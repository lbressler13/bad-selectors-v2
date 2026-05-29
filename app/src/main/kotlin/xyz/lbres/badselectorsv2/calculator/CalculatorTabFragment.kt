package xyz.lbres.badselectorsv2.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.calculator.addones.AddOnesFragment
import xyz.lbres.badselectorsv2.calculator.randomenabled.RandomEnabledFragment
import xyz.lbres.badselectorsv2.calculator.singleoperation.SingleOperationFragment
import xyz.lbres.badselectorsv2.databinding.TabFragmentBinding

class CalculatorTabFragment : TabFragment() {
    override var metadata = CalculatorTabFragment.metadata

    override var actionBarTitleResId: Int = R.string.appbar_title_calc
    override var navToHomeResId: Int? = R.id.navigateCalcToHome
    override var navToPhoneResId: Int? = R.id.navigateCalcToPhone
    override var navToDateResId: Int? = R.id.navigateCalcToDate
    override var navToOtpResId: Int? = R.id.navigateCalcToOtp

    private val addOnesFragment: AddOnesFragment by lazy { AddOnesFragment() }
    private val singleOperationFragment: SingleOperationFragment by lazy { SingleOperationFragment() }
    private val randomEnabledFragment: RandomEnabledFragment by lazy { RandomEnabledFragment() }

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
        return when (position) {
            0 -> addOnesFragment
            1 -> singleOperationFragment
            2 -> randomEnabledFragment
            else -> addOnesFragment
        }
    }

    companion object {
        val metadata = Metadata(
            R.string.title_calc,
            listOf(R.string.title_add_ones, R.string.title_single_operation, R.string.title_random_enabled),
            R.id.navigateHomeToCalc,
        )
    }
}
