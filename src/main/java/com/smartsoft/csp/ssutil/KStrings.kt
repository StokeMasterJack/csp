package com.smartsoft.csp.ssutil

fun String.lpad(desiredFinalLength: Int, padChar: Char = ' '): String {
    val padCount = desiredFinalLength - this.length
    val sb = StringBuilder()
    for (i in 0 until padCount) {
        sb.append(padChar)
    }
    sb.append(this)
    return sb.toString()
}

fun String.rpad(desiredFinalLength: Int, padChar: Char = ' '): String {
    val padCount = desiredFinalLength - this.length
    val sb = StringBuilder()
    sb.append(this)
    for (i in 0 until padCount) {
        sb.append(padChar)
    }
    return sb.toString()
}

fun Number.lpad(desiredFinalLength: Int, padChar: Char = ' '): String {
    return toString().lpad(desiredFinalLength, padChar)
}

fun Number.rpad(desiredFinalLength: Int, padChar: Char = ' '): String {
    return toString().rpad(desiredFinalLength, padChar)
}