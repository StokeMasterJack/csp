package com.smartsoft.csp.dnnf

import com.smartsoft.csp.TestConfig
import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.data.CspSample
import com.smartsoft.csp.ssutil.millis
import com.smartsoft.csp.util.SpaceJvm.loadResource
import org.junit.Assert.assertEquals
import org.junit.Test

class CompareBB {

    internal var sienna = "SiennaFullRules.txt"
    internal var tundra = "tundra_2014_fullrules.txt"

    @Test
    fun compareBB_Sienna() {
        val clob = loadResource(this, sienna)
        compareBB("sienna", clob)
    }

    @Test
    fun compareBB_Tundra() {
        val clob = loadResource(this, tundra)
        compareBB("tundra", clob)
    }

    //  4/28/2014  56m 51s
    //  11/14/2018 3m 14s
    //  5m 35s for just csp.BB:
    //  4m 10s
    //  all: 3m 44s
    //     just csp.BB: 3.4m
    //     just csp.BB: 4.9m
    //
    @Test
    fun compareBB_efcOriginal() {
        if (TestConfig.runSlowTests) {
            val clob = CspSample.EfcOriginal.loadText()
            compareBB("EfcOriginal", clob)
        }


    }

    private fun compareBB(name: String, clob: String) {
        println("CompareBB $name")
        val t0 = millis

        println("  Parse..")
        val csp = Csp.parse(clob)
        val t1 = millis
        println("    parse: ${t1 - t0}")

        println("  Propagate..")
        val t2 = millis
        println("    propagate: ${t2 - t1}")

        println("  IsSat..")
        val sat = csp.isSat()
        val t3 = millis
        println("    isSat: ${t3 - t2}")

        assert(sat)

        println("  Computing csp.BB..")
        val cspBb = csp.getBB()
        val t4 = millis
        println("    csp.BB: ${t4 - t3}")

        println("  DNNF compile..")
        val n = csp.toDnnf()
        val t5 = millis
        println("    dnnf compile: ${t5 - t4}")

        println("  Computing dnnf.getBB1..")
        val dnnfBb1 = n.bB1
        val t6 = millis
        println("    dnnfBb1: ${t6 - t5}")

        println("  Computing dnnf.getBB2..")
        val dnnfBb2 = n.bB2
        val t7 = millis
        println("    dnnfBb2: ${t7 - t6}")

        assertEquals(cspBb, dnnfBb1)
        assertEquals(cspBb, dnnfBb2)

        println()
        println("  bb: $dnnfBb1")
        println()
        println()


    }


}
