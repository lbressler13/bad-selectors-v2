package xyz.lbres.badselectorsv2.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.TabFragmentBinding
import xyz.lbres.badselectorsv2.phone.randomcircle.RandomCircleFragment
import xyz.lbres.badselectorsv2.phone.selectcorrect.SelectCorrectFragment
import xyz.lbres.badselectorsv2.phone.shuffledigits.ShuffledDigitsFragment

class PhoneTabFragment : TabFragment() {
    override var metadata = PhoneTabFragment.metadata

    override var actionBarTitleResId: Int = R.string.appbar_title_phone
    override var navToHomeResId: Int? = R.id.navigatePhoneToHome
    override var navToCalcResId: Int? = R.id.navigatePhoneToCalc
    override var navToDateResId: Int? = R.id.navigatePhoneToDate
    override var navToOtpResId: Int? = R.id.navigatePhoneToOtp

    private val selectCorrectFragment: SelectCorrectFragment by lazy { SelectCorrectFragment() }
    private val randomCircleFragment: RandomCircleFragment by lazy { RandomCircleFragment() }
    private val shuffledDigitsFragment: ShuffledDigitsFragment by lazy { ShuffledDigitsFragment() }

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
        // TODO figure out why changing the order wrecks the tests
        return when (position) {
            0 -> selectCorrectFragment
            1 -> randomCircleFragment
            2 -> shuffledDigitsFragment
            else -> randomCircleFragment
        }
    }

    companion object {
        val metadata = Metadata(
            R.string.title_phone,
            listOf(R.string.title_select_correct, R.string.title_random_circle, R.string.title_shuffled_digits),
            R.id.navigateHomeToPhone,
        )
    }
}
