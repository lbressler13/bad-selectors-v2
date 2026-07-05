package xyz.lbres.badselectorsv2.testutils

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Assert that a test fails with a specific exception of type [T] being thrown and a specific error message
 *
 * @param message [String]: expected error message
 * @param block: test to run
 * @return An exception of the expected exception type [T] that was successfully caught
 */
inline fun <reified T : Throwable> assertFailsWithMessage(message: String, block: () -> Unit): T {
    val exception = assertFailsWith<T> { block() }
    assertEquals(message, exception.message)
    return exception
}

/**
 * Run a block of code and print a specific message if an [AssertionError] is thrown
 *
 * @param failureMessage [String]: message to print in case of failure
 * @param block: code to run
 */
fun runWithFailMessage(failureMessage: String, block: () -> Unit) {
    try {
        block()
    } catch (e: AssertionError) {
        printErr(failureMessage)
        throw e
    }
}

/**
 * Run a test multiple times, and only throw AssertionError if thrown on all repetitions
 *
 * @param retries [Int]: number of retries
 * @param block: code to run
 */
fun runWithRetries(retries: Int, block: () -> Unit) {
    var error: AssertionError? = null
    for (i in 0 until retries) {
        try {
            block()
            error = null
            break
        } catch (e: AssertionError) {
            val remainingRetries = retries - i - 1
            printWarn("AssertionError thrown. $remainingRetries retries remaining.")
            error = e
        }
    }
    if (error != null) {
        throw error
    }
}
