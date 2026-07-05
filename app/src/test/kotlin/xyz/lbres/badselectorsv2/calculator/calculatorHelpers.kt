package xyz.lbres.badselectorsv2.calculator

/**
 * Split string into a list of one character strings
 *
 * @param text [String]: text to split
 * @return [List]<String>: list of the characters in [text], each represented as a string
 */
fun splitText(text: String): List<String> = text.toList().map(Char::toString)
