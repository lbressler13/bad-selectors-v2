package xyz.lbres.badselectorsv2.phone.choosedigits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.databinding.FragmentPhoneChooseDigitsBinding
import xyz.lbres.badselectorsv2.phone.BasePhoneFragment
import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel

class ChooseDigitsFragment : BasePhoneFragment() {
    private lateinit var viewModel: ChooseDigitsViewModel
    override val phoneViewModel: BasePhoneViewModel
        get() = viewModel

    private lateinit var binding: FragmentPhoneChooseDigitsBinding

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(requireActivity())[ChooseDigitsViewModel::class.java]
        binding = FragmentPhoneChooseDigitsBinding.inflate(inflater)
        initDigitViews(binding.digitsLayout)
        initDigitDialogs()
        return binding.root
    }

    private fun initDigitDialogs() {
        digitViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                viewModel.currentIndex = index
                val dialog = ChooseDigitsSetDigitDialog()
                dialog.show(childFragmentManager, ChooseDigitsSetDigitDialog.TAG)
            }
        }

        val requestKey = ChooseDigitsSetDigitDialog.CLOSED_KEY
        childFragmentManager.setFragmentResultListener(requestKey, this) { _, _ ->
            viewModel.currentIndex = -1
            displayPhoneNumber()
        }
    }
}
