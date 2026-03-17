package xyz.lbres.badselectorsv2.date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.lbres.badselectors.date.guessrange.GuessRangeFragment
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.TabFragmentBinding

class DateTabFragment : TabFragment() {
    override var metadata = DateTabFragment.metadata

    override var actionBarTitleResId: Int = R.string.appbar_title_date
    override var navToHomeResId: Int? = R.id.navigateDateToHome
    override var navToPhoneResId: Int? = R.id.navigateDateToPhone
    override var navToCalcResId: Int? = R.id.navigateDateToCalc
    override var navToOtpResId: Int? = R.id.navigateDateToOtp

    override lateinit var binding: TabFragmentBinding

    private val guessRangeFragment: GuessRangeFragment by lazy { GuessRangeFragment() }

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
            0 -> guessRangeFragment
            else -> guessRangeFragment
        }
    }

    companion object {
        val metadata = Metadata(
            R.string.title_date,
            listOf(R.string.title_guess_range),
            R.id.navigateHomeToDate,
        )
    }
}
