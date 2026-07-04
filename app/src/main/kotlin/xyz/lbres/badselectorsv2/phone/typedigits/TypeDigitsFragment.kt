package xyz.lbres.badselectorsv2.phone.typedigits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.databinding.FragmentPhoneTypeDigitsBinding
import xyz.lbres.badselectorsv2.phone.BasePhoneFragment
import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel

class TypeDigitsFragment : BasePhoneFragment() {
    private lateinit var viewModel: TypeDigitsViewModel
    override val phoneViewModel: BasePhoneViewModel
        get() = viewModel

    private lateinit var binding: FragmentPhoneTypeDigitsBinding

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(requireActivity())[TypeDigitsViewModel::class.java]
        binding = FragmentPhoneTypeDigitsBinding.inflate(inflater)
        return binding.root
    }
}
