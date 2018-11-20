package com.smartsoft.csp.util

class Count @JvmOverloads constructor(count: Int = 0) {

    var count: Int = 0
        private set

    init {
        this.count = count
    }

    fun increment(amount: Int = 1) {
        count += amount
    }
}
