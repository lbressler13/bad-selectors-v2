package xyz.lbres.badselectorsv2.testutils

private const val red = "\u001b[31m"
private const val reset = "\u001b[0m"

/**
 * Print an error message
 */
fun printErr(message: Any?) = println("$red$message$reset")
