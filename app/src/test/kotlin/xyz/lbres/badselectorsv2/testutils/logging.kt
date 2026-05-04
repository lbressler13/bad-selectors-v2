package xyz.lbres.badselectorsv2.testutils

private const val red = "\u001b[31m"
private const val nc = "\u001b[0m" // no color

/**
 * Print an error message
 */
fun printErr(message: Any?) = println("$red$message$nc")
