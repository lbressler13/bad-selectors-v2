package xyz.lbres.badselectorsv2.calculator.addones

import android.util.Log
import xyz.lbres.badselectorsv2.calculator.BaseCalculatorViewModel
import xyz.lbres.badselectorsv2.calculator.utils.CalcData
import xyz.lbres.kotlinutils.array.setAllValues

/**
 * ViewModel containing values that are specific to the add-ones calculator.
 */
class AddOnesViewModel : BaseCalculatorViewModel() {
    val absoluteMaxSavedValues: Int = 4

    /**
     * Maximum number of values that can be saved
     */
    private var _maxSavedValues: Int = 2
    val maxSavedValues: Int
        get() = _maxSavedValues

    /**
     * Values stored after computation
     */
    private val _savedValues: Array<Int?> = arrayOfNulls(absoluteMaxSavedValues)
    val savedValues: List<Int?>
        get() = _savedValues.toList()

    /**
     * Indices of saved values in computeText, if being used
     */
    private val savedValueIndices: Array<Int?> = arrayOfNulls(absoluteMaxSavedValues)

    /**
     * Delete last value from list, and update [savedValueIndices] if the last value is a saved value.
     */
    override fun backspaceComputeText() {
        val computeText = calcData.computeText

        if (computeText.isNotEmpty()) {
            // check if a saved value is being removed
            val matchedIndex = savedValueIndices.indexOf(computeText.lastIndex)

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
        val baseMessage = "Unable to append saved value at $index"
        when {
            index >= maxSavedValues -> Log.e(null, "$baseMessage, index out of bounds")
            savedValues[index] == null -> Log.w(null, "$baseMessage, value is null")
            savedValueIndices[index] != null -> Log.w(null, "$baseMessage, value already added")
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
        if (index < maxSavedValues) {
            _savedValues[index] = null
        } else {
            Log.e(null, "Unable to delete saved value at $index, index out of bounds")
        }
    }

    /**
     * Get information about saved value at a given index
     *
     * @param index [Int]: index to check
     * @return [Pair]<Int?, Boolean>: pair where first value is the value at the index,
     * and the second value indicates if the value at this index is in use
     */
    fun getSavedValueMetadata(index: Int): Pair<Int?, Boolean> {
        return if (index < maxSavedValues) {
            Pair(savedValues[index], savedValueIndices[index] != null)
        } else {
            Log.e(null, "Unable to fetch metadata at $index, index out of bounds")
            Pair(null, true) // indicates no value/unusable value
        }
    }

    /**
     * Save the computed value to the first open index.
     */
    fun saveComputedValue() {
        val computed = calcData.computedValue
        val index = savedValues.indexOfFirst { it == null }
        var warning: String? = null
        when {
            index == -1 -> warning = "Unable to save computed value $computed, no spots available"
            computed == null -> warning = "Unable to save computed value, no value to save"
            else -> _savedValues[index] = computed
        }

        if (warning != null) {
            Log.w(null, warning)
        }
    }

    fun updateMaxSavedValues(newMax: Int) {
        val oldMax = maxSavedValues
        // move values into blank spaces
        if (newMax < oldMax) {
            val blankIndices = savedValues.indices.filter { it < newMax && savedValues[it] == null }
            val leftoverIndices = savedValues.indices.filter { it >= newMax && savedValues[it] != null }

            var blankIndicesIndex = 0
            for (leftoverIndex in leftoverIndices) {
                val blankIndex = blankIndices[blankIndicesIndex]
                _savedValues[blankIndex] = savedValues[leftoverIndex]
                savedValueIndices[blankIndex] = savedValueIndices[leftoverIndex]
                blankIndicesIndex++
            }

            for (i in newMax until absoluteMaxSavedValues) {
                _savedValues[i] = null
                savedValueIndices[i] = null
            }
        }
        _maxSavedValues = newMax
    }

    /**
     * Update most recent computed value or error
     */
    override fun setResult(computedValue: Int?, error: String?) {
        super.setResult(computedValue, error)
        savedValueIndices.setAllValues(null)
    }

    /**
     * Reset data, including saved value indices. Saved values are not changed.
     */
    override fun resetComputeData() {
        super.resetComputeData()
        savedValueIndices.setAllValues(null)
    }
}
