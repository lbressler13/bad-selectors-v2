package xyz.lbres.badselectorsv2.phone.randomcircle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.databinding.FragmentRandomCircleBinding
import xyz.lbres.badselectorsv2.ext.view.disable
import xyz.lbres.badselectorsv2.ext.view.enable
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.badselectorsv2.ext.viewgroup.setChildOnClickListener
import xyz.lbres.badselectorsv2.phone.BasePhoneFragment
import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.maxDigit
import xyz.lbres.badselectorsv2.phone.utils.numDigits

/**
 * Fragment that displays an unlabelled button circle layout, where values are shuffled when a user selects a value.
 * Shuffling is not guaranteed to happen every time a button is pressed.
 */
class RandomCircleFragment : BasePhoneFragment() {
    private lateinit var binding: FragmentRandomCircleBinding
    private lateinit var viewModel: RandomCircleViewModel
    override val phoneViewModel: BasePhoneViewModel
        get() = viewModel

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRandomCircleBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[RandomCircleViewModel::class.java]

        initSettingsDialog()
        initDigitViews(binding.digitsLayout)
        initButtonCircle()

        binding.selectButton.setOnClickListener { selectDigit() }
        binding.restartButton.setOnClickListener { reset() }
        if (viewModel.currentIndex > maxDigit) {
            setRestartUi()
        }

        return binding.root
    }

    /**
     * Reset data and UI
     */
    private fun reset() {
        viewModel.resetData()
        binding.selectButton.enable()
        displayPhoneNumber()
        binding.circleLayout.children.forEach { it.enable() }
        binding.restartButton.gone()
    }

    /**
     * Update UI when showing restart button
     */
    private fun setRestartUi() {
        binding.selectButton.disable()
        binding.circleLayout.children.forEach { it.disable() }
        binding.restartButton.visible()
    }

    /**
     * Initialize buttons in circle layout
     */
    private fun initButtonCircle() {
        val initialDigit = viewModel.generatedDigit
        binding.currentDigit.text = if (initialDigit == -1 || initialDigit == null) {
            ""
        } else {
            initialDigit.toString()
        }

        // get digit based on index of button, and update ui
        binding.circleLayout.setChildOnClickListener { _, index ->
            val digit = viewModel.getGeneratedAtIndex(index)

            if (digit == null) {
                binding.currentDigit.text = ""
                reset()
            } else {
                binding.currentDigit.text = digit.toString()
                viewModel.updateDigits()
            }
        }
    }

    /**
     * Assign displayed value to the current digit
     */
    private fun selectDigit() {
        if (viewModel.currentIndex < numDigits && binding.currentDigit.text != "") {
            // get value currently being displayed
            val currentDigit = binding.currentDigit.text.toString().toInt()
            viewModel.setCurrentDigit(currentDigit)
            displayDigitAtIndex(viewModel.currentIndex)
            viewModel.incrementCurrentIndex()

            if (viewModel.currentIndex > maxDigit) {
                setRestartUi()
            }

            binding.currentDigit.text = ""
        }
    }

    /**
     * Initialize dialog to update settings
     */
    private fun initSettingsDialog() {
        binding.settingsButton.root.setOnClickListener {
            val settingsDialog = RandomCircleSettingsDialog()
            settingsDialog.show(childFragmentManager, RandomCircleSettingsDialog.TAG)
        }
    }
}
