package xyz.lbres.badselectorsv2.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.TabFragmentBinding
import xyz.lbres.badselectorsv2.phone.shufflecircle.ShuffleCircleFragment

class PhoneTabFragment : TabFragment() {
    override var metadata = PhoneTabFragment.metadata

    override var actionBarTitleResId: Int = R.string.appbar_title_phone
    override var navToHomeResId: Int? = R.id.navigatePhoneToHome

    private val shuffleCircleFragment: ShuffleCircleFragment by lazy { ShuffleCircleFragment() }

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
            0 -> shuffleCircleFragment
            else -> shuffleCircleFragment
        }
    }

    companion object {
        val metadata = Metadata(
            R.string.title_phone,
            listOf(R.string.title_shuffle_circle),
            R.id.navigateHomeToPhone,
        )
    }
}
