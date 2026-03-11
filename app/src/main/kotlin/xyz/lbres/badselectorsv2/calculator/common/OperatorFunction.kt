package xyz.lbres.badselectorsv2.calculator.common

import xyz.lbres.kotlinutils.list.StringList

/**
 * Function to combine two numbers, based on a given string operator
 */
typealias OperatorFunction = (Int, Int, String) -> Int

/**
 * Generate operator function to perform standard operation on two values
 *
 * @param operators [StringList]: list of allowed operators. Defaults to +, -, x, /
 * @return [OperatorFunction]: function that performs the canonical action of an operator that is in [operators], or throws an exception if it is not
 */
fun standardOperatorFunction(operators: StringList = listOf("+", "-", "x", "/")): OperatorFunction {
    val unknownException = Exception("Err: Unknown character")

    return { leftValue, rightValue, operator ->
        if (operator !in operators) {
            throw unknownException
        }

        when (operator) {
            "+" -> leftValue + rightValue
            "-" -> leftValue - rightValue
            "x" -> leftValue * rightValue
            "/" -> leftValue / rightValue
            else -> throw unknownException
        }
    }
}
