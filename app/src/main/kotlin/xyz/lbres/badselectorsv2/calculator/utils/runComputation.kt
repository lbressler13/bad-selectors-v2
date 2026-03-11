package xyz.lbres.badselectorsv2.calculator.utils

import xyz.lbres.kotlinutils.list.StringList
import xyz.lbres.kotlinutils.list.ext.isSingleValue
import xyz.lbres.kotlinutils.string.ext.isInt
import kotlin.NumberFormatException

/**
 * Parse string list and compute as a mathematical expression, if possible.
 * Includes list validation, modifying the list based on parameters, and running the computation itself.
 *
 * @param computeText [StringList]: list of string values to parse, consisting of operators and numbers
 * @param buildMultiDigit [Boolean]: if separate digits should be combined to form multidigit numbers. Defaults to true.
 * @return [Int] result of parsing
 */
fun runComputation(computeText: StringList, buildMultiDigit: Boolean = true): Int {
    val validatedText = buildAndValidateComputeText(computeText, buildMultiDigit)
    if (validatedText.isNullOrEmpty()) {
        throw Exception("Err: Syntax Error")
    }

    return try {
        parseText(validatedText)
    } catch (_: ArithmeticException) {
        throw Exception("Err: Divide by 0")
    } catch (_: NumberFormatException) {
        throw Exception("Err: Overflow value")
    } catch (_: Exception) {
        throw Exception("Err: Parse error")
    }
}

/**
 * Validate syntax of computation text.
 * Validates that text does not start or end with an operator, that all values are operators or numbers,
 * and that there are no adjacent numbers or operators.
 * Builds multi-digit list if indicated by params.
 *
 * @param computeText [StringList]: text to validate and possibly modify
 * @param buildMultiDigit [Boolean]: if adjacent digits should be combined to form multi-digit numbers.
 * If false, adjacent digits will cause validation to fail
 * @return [StringList]?: list where relative position of numbers and digits is unchanged,
 * but digits have been combined to form multi-digit numbers if [buildMultiDigit] is true.
 */
private fun buildAndValidateComputeText(computeText: StringList, buildMultiDigit: Boolean): StringList? {
    if (computeText.isEmpty()) {
        return null
    }

    // must start and end w/ number
    if (isOperator(computeText.first()) || isOperator(computeText.last())) {
        return null
    }

    var currentNumber = ""
    val newText: MutableList<String> = mutableListOf()

    var lastWasOperator = false
    var lastWasNumber = false

    for (element in computeText) {
        when {
            // add current number before adding operator
            isOperator(element) && currentNumber.isNotEmpty() -> {
                newText.add(currentNumber)
                currentNumber = ""
                newText.add(element)
                lastWasOperator = true
                lastWasNumber = false
            }
            // add operator
            isOperator(element) && !lastWasOperator -> {
                newText.add(element)
                lastWasOperator = true
                lastWasNumber = false
            }
            // add current number to existing number if building multi-digit
            element.isInt() && buildMultiDigit -> currentNumber += element
            // add current number
            element.isInt() && !lastWasNumber -> {
                newText.add(element)
                lastWasNumber = true
                lastWasOperator = false
            }
            // doesn't pass validation
            else -> return null
        }
    }

    if (currentNumber.isNotEmpty()) {
        newText.add(currentNumber)
    }

    return newText
}

/**
 * Run calculation by parsing text and performing operations. Assumes validation has succeeded.
 *
 * @param computeText [StringList]: list of string values to parse, consisting of operators and number
 * @return [Int]: the single computed value
 */
private fun parseText(computeText: StringList): Int {
    var currentState: StringList = computeText
    val operatorRounds = listOf(listOf("x", "/"), listOf("+", "-"))

    for (round in operatorRounds) {
        currentState = parseOperatorRound(currentState, round)
    }

    if (!currentState.isSingleValue()) {
        throw Exception("Parsing error")
    }

    return currentState[0].toInt()
}

/**
 * Run a round of operators. Assumes validation has succeeded.
 *
 * @param computeText [StringList]: list of string values to parse, consisting of operators and numbers
 * @param ops [StringList]: list of string operators to be applied in this round
 * @return [StringList]: modified list where each application of the given operators has been reduced to a single int
 */
private fun parseOperatorRound(computeText: StringList, ops: StringList): StringList {
    val simplifiedList: MutableList<String> = mutableListOf()

    var index = 0

    while (index < computeText.size) {
        val element = computeText[index]

        if (element !in ops) {
            simplifiedList.add(element)
            index++
        } else {
            // don't have to worry about out of bounds or parse errors b/c of validation
            val leftVal: Int = simplifiedList.last().toInt()
            val rightVal: Int = computeText[index + 1].toInt()
            val result = applyOperator(leftVal, rightVal, element)

            // replace last element in list
            val lastIndex = simplifiedList.lastIndex
            simplifiedList[lastIndex] = result.toString()

            // skip past next value, which was already used as rightValue
            index += 2
        }
    }

    return simplifiedList
}

/**
 * Apply operator to two values
 *
 * @param leftValue [Int]: left value for operation
 * @param rightValue [Int]: right value for operation
 * @param operator [String]: operator to apply
 * @return [Int]: result of applying operator
 */
private fun applyOperator(leftValue: Int, rightValue: Int, operator: String): Int {
    return when (operator) {
        "+" -> leftValue + rightValue
        "-" -> leftValue - rightValue
        "x" -> leftValue * rightValue
        "/" -> leftValue / rightValue
        else -> throw Exception("Err: Unknown operator")
    }
}

/**
 * Determine if a given string is an operator
 *
 * @param element [String]: value to check
 * @return [Boolean]: true is the element is an operator, false otherwise
 */
private fun isOperator(element: String): Boolean = element in listOf("+", "-", "x", "/")
