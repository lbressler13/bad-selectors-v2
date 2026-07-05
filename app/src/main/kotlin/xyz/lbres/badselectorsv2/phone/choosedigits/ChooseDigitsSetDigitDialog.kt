package xyz.lbres.badselectorsv2.phone.choosedigits

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.BaseDialog
import xyz.lbres.badselectorsv2.databinding.DialogPhoneChooseDigitsSetDigitBinding
import xyz.lbres.badselectorsv2.phone.utils.digitsRange

/**
 * Dialog to update value at current index for the [ChooseDigitsFragment] selector
 */
class ChooseDigitsSetDigitDialog() : BaseDialog<DialogPhoneChooseDigitsSetDigitBinding>() {
    override val titleResId: Int = R.string.title_choose_digits_dialog
    override val dialogClosedRequestKey: String? = CLOSED_KEY

    private val buttonIds = listOf(
        R.id.zeroButton,
        R.id.oneButton,
        R.id.twoButton,
        R.id.threeButton,
        R.id.fourButton,
        R.id.fiveButton,
        R.id.sixButton,
        R.id.sevenButton,
        R.id.eightButton,
        R.id.nineButton,
    )

    private lateinit var viewModel: ChooseDigitsViewModel

    override fun inflateLayout() = DialogPhoneChooseDigitsSetDigitBinding.inflate(layoutInflater)

    /**
     * Display initial UI
     */
    override fun setInitialUi() {
        viewModel = ViewModelProvider(requireActivity())[ChooseDigitsViewModel::class.java]
        val index = viewModel.currentIndex
        if (index !in digitsRange) {
            Log.e(null, "Invalid index for set digit dialog: $index")
            dismiss()
            return
        }
        val initialValue = viewModel.digits[index]
        if (initialValue != null && buttonIds.getOrNull(initialValue) != null) {
            binding.buttonsGroup.check(buttonIds[initialValue])
        }
    }

    /**
     * Save updated value
     */
    override fun saveChanges() {
        val checkedId = binding.buttonsGroup.checkedRadioButtonId
        if (viewModel.currentIndex in digitsRange) {
            viewModel.setCurrentDigit(buttonIds.indexOf(checkedId))
        }
    }

    companion object {
        val TAG = "ChooseDigitsSetDigitDialog"
        val CLOSED_KEY = "${TAG}_Closed"
    }
}
