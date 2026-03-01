package xyz.lbres.badselectorsv2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.databinding.FragmentHomeBinding
import xyz.lbres.badselectorsv2.ui.BaseFragment

/**
 * Initial fragment in the app
 */
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding

    /**
     * Initialize fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.infoButton.setOnClickListener {
            requireBaseActivity().runNavAction(R.id.navigateHomeToAttributions)
        }

        return binding.root
    }
}
