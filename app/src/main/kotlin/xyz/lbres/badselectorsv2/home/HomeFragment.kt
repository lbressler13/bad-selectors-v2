package xyz.lbres.badselectorsv2.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.AppScreenFragment
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.FragmentHomeBinding
import xyz.lbres.badselectorsv2.home.selectorgroup.SelectorGroupAdapter

/**
 * Initial fragment in the app
 */
class HomeFragment : AppScreenFragment() {
    override var navToPhoneResId: Int? = R.id.navigateHomeToPhone
    override var navToCalcResId: Int? = R.id.navigateHomeToCalc
    override var navToDateResId: Int? = R.id.navigateHomeToDate
    override var navToOtpResId: Int? = R.id.navigateHomeToOtp

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    /**
     * Initialize fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val selectorsRecycler: RecyclerView = binding.selectorGroupRecycler
        val metadata = TabFragment.allMetadata

        // create adapter
        val adapter = SelectorGroupAdapter(metadata, requireBaseActivity(), viewModel)
        selectorsRecycler.adapter = adapter
        selectorsRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewModel.initSelectorsExpanded(metadata.size)

        binding.infoButton.root.setOnClickListener {
            requireBaseActivity().runNavAction(R.id.navigateHomeToAttributions)
        }

        return binding.root
    }
}
