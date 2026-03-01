package xyz.lbres.badselectorsv2.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.BaseFragment
import xyz.lbres.badselectorsv2.databinding.FragmentHomeBinding

/**
 * Initial fragment in the app
 */
class HomeFragment : BaseFragment() {
    override var navToPhoneResId: Int? = R.id.navigateHomeToPhone

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    /**
     * Initialize fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        binding.infoButton.root.setOnClickListener {
            requireBaseActivity().runNavAction(R.id.navigateHomeToAttributions)
        }

        return binding.root
    }
}
