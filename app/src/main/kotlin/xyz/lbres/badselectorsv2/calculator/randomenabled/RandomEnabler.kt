package xyz.lbres.badselectorsv2.calculator.randomenabled

import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.kotlinutils.booleanarray.booleanArrayOfValue

class RandomEnabler {
    private val enabledNumbersRange = 3..7
    private val enabledOperatorsRange = 2..3
    private val operators = listOf("+", "-", "x", "/")

    /**
     * Boolean array where the value at each index indicates if the digit with that index should be enabled
     */
    private var enabledNumbers: BooleanArray = booleanArrayOfValue(10, true)

    /**
     * Boolean array where the value at each index indicates if the operator at the same index in [operators] should be enabled
     */
    private var enabledOperators: BooleanArray = booleanArrayOfValue(operators.size, true)

    init {
        update()
    }

    /**
     * Determine if a given digit should be enabled
     *
     * @param digit [Int]: digit to check
     * @return [Boolean]: `true` if the digit should be enabled, `false` otherwise
     */
    fun isDigitEnabled(digit: Int): Boolean = digit in enabledNumbers.indices && enabledNumbers[digit]

    /**
     * Determine if a given operator should be enabled
     *
     * @param operator [String]: operator to check
     * @return [Boolean]: `true` if the operator should be enabled, `false` otherwise
     */
    fun isOperatorEnabled(operator: String): Boolean {
        return when (operator) {
            "+" -> enabledOperators[0]
            "-" -> enabledOperators[1]
            "x" -> enabledOperators[2]
            "/" -> enabledOperators[3]
            else -> false
        }
    }

    /**
     * Update enabled digits and operators. Guarantees that at least one digit and at least one operator will change
     */
    fun update() {
        val previousNumbers = enabledNumbers.indices.filter { enabledNumbers[it] }
        val previousOperators = enabledOperators.indices.filter { enabledOperators[it] }

        var updatedNumbers = previousNumbers
        var updatedOperators = previousOperators

        // validate that both numbers and operators are changed
        while (updatedNumbers == previousNumbers || updatedOperators == previousOperators) {
            val numEnabledDigits = enabledNumbersRange.seededRandom()
            val numEnabledOperators = enabledOperatorsRange.seededRandom()

            val shuffledNumberIndices = enabledNumbers.indices.seededShuffled()
            val shuffledOperatorsIndices = enabledOperators.indices.seededShuffled()

            shuffledNumberIndices.forEachIndexed { index, numberIndex ->
                enabledNumbers[numberIndex] = index < numEnabledDigits
            }

            shuffledOperatorsIndices.forEachIndexed { index, operatorIndex ->
                enabledOperators[operatorIndex] = index < numEnabledOperators
            }

            updatedNumbers = enabledNumbers.indices.filter { enabledNumbers[it] }
            updatedOperators = enabledOperators.indices.filter { enabledOperators[it] }
        }
    }
}
