package com.tms.csp.dnnf

import com.tms.csp.ast.Exp
import com.tms.csp.data.CspSample
import kotlin.test.Test
import kotlin.test.assertEquals

class ParseDnnfTest {

    @Test
    fun testCamry() {

        val expectedSatCount = 520128.toBigInteger()

        val csp = CspSample.Camry2011NoDc.parseCsp()
        val d1 = csp.toDnnfSmooth()

        assert(d1.isDnnf)
        assert(d1.checkDnnf())

        val tinyDnnf1 = d1.serializeTinyDnnfSpace()

        val tinyDnnf2 = CspSample.CamryDnnf.loadText()


        val d2: Exp = Exp.parseTinyDnnf(tinyDnnf1)
        val d3: Exp = Exp.parseTinyDnnf(tinyDnnf2)

        println("d1.satCount = ${d1.satCount}")
        println("d2.satCount = ${d2.satCount}")
        println("d3.satCount = ${d3.satCount}")

        assertEquals(expectedSatCount, d1.satCount)
        assertEquals(expectedSatCount, d2.satCount)
        assertEquals(expectedSatCount, d3.satCount)


    }
}