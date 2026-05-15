package xyz.lbres.badselectorsv2.phone.selectincorrect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.databinding.ComponentPhoneNumberBinding
import xyz.lbres.badselectorsv2.databinding.FragmentSelectIncorrectBinding
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.badselectorsv2.phone.BasePhoneFragment
import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel

// TODO remove restart

class SelectIncorrectFragment : BasePhoneFragment() {
    private lateinit var viewModel: SelectIncorrectViewModel
    override val phoneViewModel: BasePhoneViewModel
        get() = viewModel

    private lateinit var binding: FragmentSelectIncorrectBinding
    override val digitsLayout: ComponentPhoneNumberBinding
        get() = binding.digitsLayout

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[SelectIncorrectViewModel::class.java]
        binding = FragmentSelectIncorrectBinding.inflate(layoutInflater)

        initOnClicks()
        updateUi()

        return binding.root
    }

    /**
     * Initialize onClick listeners for all views on screen
     */
    private fun initOnClicks() {
        // generate button
        binding.generateNumberButton.setOnClickListener {
            viewModel.updateNumber()
            updateUi()
        }

        // digits
        getDigitViews(binding.generatedDigitsLayout).forEachIndexed { index, view ->
            view.setOnClickListener {
                viewModel.setDigitAt(index)
                updateUi()
            }
        }

        // restart button
        binding.restartButton.root.setOnClickListener { reset() }
    }

    /**
     * Update the UI to display the correct digits, and display restart UI if needed
     */
    private fun updateUi() {
        getDigitViews(binding.generatedDigitsLayout).forEachIndexed { index, view ->
            view.text = viewModel.generatedNumber[index].toString()
        }
        displayPhoneNumber()
        displayPhoneNumber(viewModel.generatedNumber, binding.generatedDigitsLayout, overrideUnderline = false)

        // show restart UI if needed
        if (viewModel.completedNumber) {
            showRestartUi()
        }
    }

    /**
     * Show restart button and hide main body
     */
    private fun showRestartUi() {
        binding.restartButton.root.visible()
        binding.mainBody.gone()
    }

    /**
     * Reset data and UI
     */
    private fun reset() {
        viewModel.resetData()
        binding.restartButton.root.gone()
        binding.mainBody.visible()
        updateUi()
    }
}
