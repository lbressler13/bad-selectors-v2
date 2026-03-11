package xyz.lbres.badselectorsv2.calculator.common

import xyz.lbres.kotlinutils.general.simpleIf
import xyz.lbres.kotlinutils.generic.ext.ifNull
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.list.StringList
import xyz.lbres.kotlinutils.list.ext.isSingleValue
import xyz.lbres.kotlinutils.string.ext.isInt
import kotlin.NumberFormatException

/**
 * Parse string list and compute as a mathematical expression, if possible.
 * Includes list validation, modifying the list based on parameters, and running the computation itself.
 *
 * @param computeText [StringList]: list of string values to parse, consisting of operators and number
 * @param firstRoundOps [StringList]: list of operators to be applied in first round of computation, likely multiplication and division
 * @param secondRoundOps [StringList]: list of operators to be applied in second round of computation, likely addition and subtraction
 * @param performSingleOp [OperatorFunction]: given an operator and 2 numbers, applies the operator to the numbers
 * @param numbersOrder [IntList]?: list of numbers, which can be used to reassign the values of digits
 * @param buildMultiDigit [Boolean]: if separate digits should be combined to form multidigit numbers. Defaults to false.
 * @return [Int] result of parsing
 */
fun runComputation(
    computeText: StringList,
    firstRoundOps: StringList,
    secondRoundOps: StringList, // TODO operator rounds
    performSingleOp: OperatorFunction,
    numbersOrder: IntList? = null,
    buildMultiDigit: Boolean = false,
): Int {
    val order = simpleIf(validateNumbersOrder(numbersOrder), numbersOrder, null)

    val validatedText = buildAndValidateComputeText(
        computeText,
        firstRoundOps + secondRoundOps,
        order,
        buildMultiDigit,
    ).ifNull {
        throw Exception("Err: Syntax Error")
    }

    return try {
        parseText(validatedText, firstRoundOps, secondRoundOps, performSingleOp)
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
 * Applies numbers order if not null, and builds multi-digit list if indicated by params.
 *
 * @param initialText [StringList]: text to validate and possibly modify
 * @param ops [StringList]: list of string values recognized as operators
 * @param numbersOrder [IntList]?: list of numbers, which can be used to reassign the values of digits
 * @param buildMultiDigit [Boolean]: if adjacent digits should be combined to form multi-digit numbers.
 * If false, adjacent digits will cause validation to fail
 * @return [StringList]?: list where relative position of numbers and digits is unchanged,
 * but digits have been combined to form multi-digit numbers if [buildMultiDigit] is true,
 * and have been mapped according to [numbersOrder] if not null. Returns null if text fails validation.
 */
fun buildAndValidateComputeText(
    initialText: StringList,
    ops: StringList,
    numbersOrder: IntList?,
    buildMultiDigit: Boolean,
): StringList? {
    if (initialText.isEmpty()) {
        return initialText
    }

    // must start and end w/ number
    if (isOperator(initialText.first(), ops) || isOperator(initialText.last(), ops)) {
        return null
    }

    var currentNumber = ""
    val newText: MutableList<String> = mutableListOf()

    val lastWasOperator: () -> Boolean = { newText.isNotEmpty() && isOperator(newText.last(), ops) }
    val lastWasNumber: () -> Boolean = { newText.isNotEmpty() && newText.last().isInt() }

    for (element in initialText) {
        when {
            // add current number before adding operator
            isOperator(element, ops) && currentNumber.isNotEmpty() -> {
                newText.add(currentNumber)
                currentNumber = ""
                newText.add(element)
            }
            // add operator
            isOperator(element, ops) && !lastWasOperator() -> newText.add(element)
            // add current number to existing number if building multi-digit
            element.isInt() && buildMultiDigit -> {
                val mappedElement = applyNumbersOrder(element, numbersOrder)
                currentNumber += mappedElement
            }
            // add current number
            element.isInt() && !lastWasNumber() -> {
                val mappedElement = applyNumbersOrder(element, numbersOrder)
                newText.add(mappedElement)
            }
            // doesn't pass validation
            else -> return null
        }
    }

    if (currentNumber != "") {
        newText.add(currentNumber)
    }

    return newText
}

/**
 * Validate that a number order is not null and contains only the numbers 0..9, not in the sorted order
 *
 * @param order [IntList]?: list of numbers, can be null
 * @return [Boolean]: true if validation succeeds, false otherwise
 */
fun validateNumbersOrder(order: IntList?): Boolean {
    return order != null &&
            order.joinToString("") != "0123456789" &&
            order.sorted().joinToString("") == "0123456789"
}

/**
 * Map digits to use values in numbers order.
 *
 * @param number [String]: number to operate on
 * @param numbersOrder IntList?: list of numbers, containing the values 0..9 in any other order
 * @return [String]: string where digits have been replaced such that digit i now has value numbersOrder.get(i)
 */
fun applyNumbersOrder(number: String, numbersOrder: IntList?): String {
    if (numbersOrder == null) {
        return number
    }

    return number.map { c ->
        if (c == '-') {
            c
        } else {
            val index = c.digitToInt()
            numbersOrder[index].toString()
        }
    }.joinToString("")
}

/**
 * Run calculation by parsing text and performing operations.
 * Assumes validation has succeeded and numbers order has been applied if necessary.
 *
 * @param computeText [StringList]: list of string values to parse, consisting of operators, numbers, and parens
 * @param firstRoundOps [StringList]: list of operators to be applied in first round of computation, likely multiplication and division
 * @param secondRoundOps [StringList]: list of operators to be applied in second round of computation, likely addition and subtraction
 * @param performSingleOp [OperatorFunction]: given an operator and 2 numbers, applies the operator to the numbers
 * @return [Int]: the single computed value
 */
fun parseText(
    computeText: StringList,
    firstRoundOps: StringList,
    secondRoundOps: StringList,
    performSingleOp: OperatorFunction,
): Int {
    var currentState: StringList = computeText

    // run first round of operators (most likely multiplication and division)
    currentState = simpleIf(
        firstRoundOps.isEmpty(),
        currentState,
        parseOperatorRound(currentState, firstRoundOps, performSingleOp),
    )

    // run second round of operators (most likely addition and subtraction)
    currentState = simpleIf(
        secondRoundOps.isEmpty(),
        currentState,
        parseOperatorRound(currentState, secondRoundOps, performSingleOp),
    )

    if (!currentState.isSingleValue()) {
        throw Exception("Parsing error")
    }

    return currentState[0].toInt()
}

/**
 * Run a round of operators
 * Assumes validation has succeeded and numbers order has been applied if necessary.
 *
 * @param computeText [StringList]: list of string values to parse, consisting of operators and numbers
 * @param ops [StringList]: list of string operators to be applied
 * @param performSingleOp [OperatorFunction]: given an operator and 2 numbers, applies the operator to the numbers
 * @return [StringList]: modified list where each application of a given operator has been reduced to a single ExactFraction, represented as a EF string
 */
fun parseOperatorRound(computeText: StringList, ops: StringList, performSingleOp: OperatorFunction): StringList {
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
            val result = performSingleOp(leftVal, rightVal, element)
            val lastIndex = simplifiedList.lastIndex

            simplifiedList[lastIndex] = result.toString()

            // skip past next value, which was already used as rightValue
            index += 2
        }
    }

    return simplifiedList
}

/**
 * Determine if a given string is an operator
 *
 * @param element [String]: value to check
 * @param ops [StringList]: list of operators to check against
 * @return [Boolean]: true is the element is a member of the ops list, false otherwise
 */
fun isOperator(element: String, ops: StringList): Boolean = element in ops
