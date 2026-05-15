package xyz.lbres.badselectorsv2.phone.shuffledigits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.databinding.FragmentShuffledDigitsBinding
import xyz.lbres.badselectorsv2.phone.BasePhoneFragment
import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel

class ShuffledDigitsFragment : BasePhoneFragment() {
    private lateinit var viewModel: ShuffledDigitsViewModel
    override val phoneViewModel: BasePhoneViewModel
        get() = viewModel

    private lateinit var binding: FragmentShuffledDigitsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[ShuffledDigitsViewModel::class.java]
        binding = FragmentShuffledDigitsBinding.inflate(layoutInflater)

        updateUi()

        return binding.root
    }

    private fun updateUi() {
        displayPhoneNumber()
    }
}
