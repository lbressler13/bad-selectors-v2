package xyz.lbres.badselectorsv2.calculator.addones

import android.util.Log
import xyz.lbres.badselectorsv2.calculator.BaseCalculatorViewModel
import xyz.lbres.badselectorsv2.calculator.utils.CalcData
import xyz.lbres.kotlinutils.array.ext.setAllValues

/**
 * ViewModel containing values that are specific to the plus-one calculator.
 */
class AddOnesViewModel : BaseCalculatorViewModel() {
    /**
     * Maximum number of values that can be saved
     */
    val maxSavedValues: Int = 2

    /**
     * Values stored after computation
     */
    private val _savedValues: Array<Int?> = arrayOfNulls(maxSavedValues)
    val savedValues: List<Int?>
        get() = _savedValues.toList()

    /**
     * Indices of saved values in computeText, if being used
     */
    private val savedValueIndices: Array<Int?> = arrayOfNulls(maxSavedValues)

    /**
     * Delete last value from list, and updates [savedValueIndices] if the most recent value is a saved value.
     */
    override fun backspaceComputeText() {
        val computeText = calcData.computeText

        if (computeText.isNotEmpty()) {
            // check if a saved value is being removed
            val matchedIndex = savedValueIndices.indexOfFirst { computeText.lastIndex == it }

            if (matchedIndex != -1) {
                savedValueIndices[matchedIndex] = null
            }

            val newText = computeText.subList(0, computeText.lastIndex)
            calcData = CalcData(newText, calcData.computedValue, calcData.error)
        }
    }

    /**
     * Add saved value to compute text
     *
     * @param index [Int]: index of saved value to add
     */
    fun appendSavedValueAtIndex(index: Int) {
        val baseError = "Unable to append saved value at $index"
        when {
            savedValues[index] == null -> Log.e(null, "$baseError, value is null")
            savedValueIndices[index] != null -> Log.e(null, "$baseError, value already added")
            else -> {
                val position = calcData.computeText.size
                savedValueIndices[index] = position
                appendComputeText(savedValues[index].toString())
            }
        }
    }

    /**
     * Delete a saved value
     *
     * @param index [Int]: index of saved value to delete
     */
    fun clearSavedValueAtIndex(index: Int) {
        _savedValues[index] = null
    }

    fun savedValueMetadata(index: Int): Pair<Int?, Boolean> {
        return Pair(savedValues[index], savedValueIndices[index] != null)
    }

    /**
     * Save the computed value to the first open index.
     */
    fun saveComputedValue() {
        val computed = calcData.computedValue
        val index = savedValues.indexOfFirst { it == null }
        if (index == -1) {
            Log.e(null, "Unable to save value $computed, no spots available")
        } else if (computed != null) {
            _savedValues[index] = computed
        }
    }

    /**
     * Reset data, including saved value indices. Saved values are not changed.
     */
    override fun resetComputeData() {
        super.resetComputeData()
        savedValueIndices.setAllValues(null)
    }
}
