package com.tms.csp.ast

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.tms.csp.ast.formula.KFormula

import java.util.ArrayList
import java.util.Collections

import com.tms.csp.ssutil.Strings.rpad

class XorCounts private constructor(constraints: Iterable<Exp>) {

    private val map: Map<String, XorCount>

    val countsSorted: List<XorCount>
        get() {
            if (map.isEmpty()) return ImmutableList.of()
            val a = ArrayList(map.values)
            Collections.sort(a)
            return ImmutableList.copyOf(a)
        }


    val max: XorCount?
        get() = if (map.isEmpty()) null else Collections.max(map.values)


    init {
        val xors = Csp.getXorConstraints(constraints)
        if (xors.isEmpty()) {
            map = ImmutableMap.of()
        } else {
            map = initMap(xors)
            countInternal(constraints)
        }
    }


    private fun countInternal(args: Iterable<Exp>) {
        for (arg in args) {
            countInternal(arg)
        }
    }

    private fun countInternal(e: Exp) {
        if (e.isLit) {
            val p = e.prefix
            val xorCount = map[p]
            xorCount?.increment()
        } else if (e.isComplex) {
            countInternal(e.argIt)
        } else if (e.isConstant) {
            //ignore
        } else {
            throw IllegalStateException(e.javaClass.name)
        }
    }

    fun print() {
        for (xorCount in countsSorted) {
            System.err.println(rpad(xorCount.prefix, ' ', 10) + ": " + xorCount.count)
        }
        System.err.println("Max: " + max!!)
    }

    companion object {

        fun count(csp: Csp): XorCounts {
            return count(csp.complexIt)
        }

        fun count(formula: KFormula): XorCounts {
            return count(formula.argIt)
        }

        fun count(constraints: Iterable<Exp>): XorCounts {
            return XorCounts(constraints)
        }

        fun getBestXor(constraints: Iterable<Exp>): Xor? {
            val count = count(constraints)
            val max = count.max ?: return null
            return max.xor.asXor
        }


        fun getMax(formula: KFormula): Xor? {
            return getBestXor(formula.argIt)
        }

        fun getMax(csp: Csp): Xor? {
            return getBestXor(csp.complexIt)
        }

        private fun initMap(xors: List<Exp>): Map<String, XorCount> {
            val b2 = ImmutableMap.builder<String, XorCount>()
            for (xor in xors) {
                b2.put(xor.prefix, XorCount(xor))
            }
            return b2.build()
        }
    }
}
