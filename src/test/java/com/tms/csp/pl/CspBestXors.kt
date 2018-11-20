package com.tms.csp.pl

import com.google.common.collect.HashMultiset
import com.tms.csp.ast.ExpId
import com.tms.csp.data.CspSample
import kotlin.test.Test

class CspBestXors {

    @Test
    fun test() {
        CspSample.EfcOriginal.let { sample ->
            val csp = sample.parseCsp()
            csp.scoreXors()

            val ff = csp.mkFormula().asFormula
            val xor1 = ff.getBestXorSplit1()
            val xor2 = ff.getBestXorSplit2()

            println("xor1 = ${xor1}")
            println("xor2 = ${xor2}")



        }
    }

}