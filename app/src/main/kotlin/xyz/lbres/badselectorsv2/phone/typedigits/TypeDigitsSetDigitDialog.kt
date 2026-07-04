package xyz.lbres.badselectorsv2.phone.typedigits

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.SelectorDialog
import xyz.lbres.badselectorsv2.databinding.DialogPhoneTypeDigitsSetDigitBinding
import xyz.lbres.badselectorsv2.phone.utils.digitsRange

class TypeDigitsSetDigitDialog(private val index: Int) : SelectorDialog<DialogPhoneTypeDigitsSetDigitBinding>() {
    override val titleResId: Int = R.string.title_type_digits_dialog
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

    private lateinit var viewModel: TypeDigitsViewModel

    override fun inflateLayout() = DialogPhoneTypeDigitsSetDigitBinding.inflate(layoutInflater)

    override fun setInitialUi() {
        if (index !in digitsRange) {
            Log.e(null, "Invalid index for set digit dialog: $index")
            dismiss()
        }
        viewModel = ViewModelProvider(requireActivity())[TypeDigitsViewModel::class.java]
        val initialValue = viewModel.digits[index]
        if (initialValue != null) {
            binding.buttonsGroup.check(buttonIds[initialValue])
        }
    }

    override fun saveChanges() {
        if (index in digitsRange) {
            val checkedId = binding.buttonsGroup.checkedRadioButtonId
            viewModel.selectValue(index, buttonIds.indexOf(checkedId))
        } else {
            Log.w(null, "Index not set, closing dialog without saving changes")
        }
    }

    companion object {
        val TAG = "TypeDigitsSetDigitDialog"
        val CLOSED_KEY = "${TAG}_Closed"
    }
}
