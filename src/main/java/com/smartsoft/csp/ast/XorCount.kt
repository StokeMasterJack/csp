package com.smartsoft.csp.ast

class XorCount(val xor: Xor) : Comparable<XorCount> {

    val prefix: String = xor.prefix

    var count: Int = 0
        private set

    fun increment(amount: Int = 1) {
        count += amount
    }


    override fun compareTo(that: XorCount): Int {
        return this.count.compareTo(that.count)
    }

    override fun toString(): String {
        return "$prefix:$count"
    }
}
