package com.smartsoft.csp.ssutil

class BitSetJ @JvmOverloads constructor(private var word: Long = 0L) {

    internal fun removeAll(wordToRemove: Long) {
        word = word and wordToRemove.inv()
    }
}
