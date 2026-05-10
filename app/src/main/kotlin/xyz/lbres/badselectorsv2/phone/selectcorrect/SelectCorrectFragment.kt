package xyz.lbres.badselectorsv2.phone.selectcorrect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.databinding.FragmentSelectCorrectBinding
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.badselectorsv2.phone.BasePhoneFragment
import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.utils.getColorOnBackground
import xyz.lbres.badselectorsv2.utils.getColorPrimary
import xyz.lbres.kotlinutils.general.simpleIf

/**
 * Fragment that displays a randomized phone number and allows users to select correct digits and re-shuffle remaining
 */
class SelectCorrectFragment : BasePhoneFragment() {
    private lateinit var viewModel: SelectCorrectViewModel
    override lateinit var phoneViewModel: BasePhoneViewModel
    private lateinit var binding: FragmentSelectCorrectBinding
    override var underlineDigits: Boolean = false

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SelectCorrectViewModel::class.java]
        phoneViewModel = viewModel
        binding = FragmentSelectCorrectBinding.inflate(layoutInflater, container, false)

        initDigitViews(binding.digitsLayout)
        initOnClicks()
        updateUi()

        return binding.root
    }

    /**
     * Update the UI to display the correct digits, and display restart UI if needed
     */
    private fun updateUi() {
        // display empty number
        if (viewModel.initialized) {
            digitViews.indices.forEach { updateSingleDigitUi(it) }
        } else {
            digitViews.forEach {
                it.text = emptyDigit
                it.setTextColor(standardColor())
            }
        }

        // show restart UI if needed
        if (digitsRange.none { viewModel.getDigitAt(it) == null}) {
            showRestartUi()
        }
    }

    /**
     * Set the UI for a single digit, including the text displayed and the color of the view
     *
     * @param index [Int]: of view to update
     */
    private fun updateSingleDigitUi(index: Int) {
        val view = digitViews[index]
        val textColor = if (viewModel.getDigitAt(index) == null) {
            standardColor()
        } else {
            selectedColor()
        }
        view.setTextColor(textColor)

        val proposed = viewModel.generatedNumber[index]
        view.text = if (proposed == -1) emptyDigit else proposed.toString()
    }

    /**
     * Show restart button and hide main body
     */
    private fun showRestartUi() {
        binding.digitsLayout.phoneDivider0.setTextColor(selectedColor())
        binding.digitsLayout.phoneDivider1.setTextColor(selectedColor())

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

        binding.digitsLayout.phoneDivider0.setTextColor(standardColor())
        binding.digitsLayout.phoneDivider1.setTextColor(standardColor())
        updateUi()
    }

    /**
     * Initialize onClick listeners for all views on screen
     */
    private fun initOnClicks() {
        // generate button
        binding.generateNumberButton.setOnClickListener {
            viewModel.generateNumber()
            updateUi()
        }

        // digits
        digitViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                viewModel.setDigitAt(index)
                updateUi()
            }
        }

        // restart button
        binding.restartButton.root.setOnClickListener { reset() }
    }

    private fun selectedColor(): Int = getColorPrimary(requireContext())
    private fun standardColor(): Int = getColorOnBackground(requireContext())
}
