package com.tms.csp.util

import com.google.common.collect.ImmutableSet

object Prof {
    private val functionsCalled = mutableSetOf<String>()
    @JvmStatic
    fun cal(functionName: String) {
        functionsCalled.add(functionName)
    }

    fun copyAndClear(): Set<String> {
        val copy = ImmutableSet.copyOf(functionsCalled)
        functionsCalled.clear()
        return copy
    }

    fun compare(that: Set<String>) {
        println("thisMinusThat: ${functionsCalled.minus(that)}")
        println("thatMinusThis: ${that.minus(functionsCalled)}")
    }
}