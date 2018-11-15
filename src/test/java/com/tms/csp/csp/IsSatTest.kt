package com.tms.csp.csp

import com.tms.csp.ast.Csp
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Assert.assertTrue
import org.junit.Test

class IsSatTest : CspBaseTest2() {

    @Test
    fun testTiny() {
        val csp = loadCsp(CspSample.TinyDc)
        assertTrue(csp.isSat())
    }

    @Test
    fun testTrim() {
        val csp = loadCsp(CspSample.TrimNoDc)
        assertTrue(csp.isSat())
    }

    @Test
    fun testTrimColor() {
        val csp = loadCsp(CspSample.TrimColorNoDc)
        assertTrue(csp.isSat())
    }

    @Test
    fun testTrimColorOptions() {
        val csp = Csp.parse(CspSample.TrimColorOptionsDc)
        assertTrue(csp.isSat())
    }

    @Test
    fun testCamry2011() {
        val csp = loadCsp(CspSample.Camry2011Dc, true)
        assertTrue(csp.isSat())
    }

    //591 millis
    @Test
    fun testEfc1() {
        val csp = loadCsp(efcOriginal)
        val t1 = System.currentTimeMillis()
        assertTrue(csp.isSat())
        val t2 = System.currentTimeMillis()
        System.err.println("efc1.isSat Delta: " + (t2 - t1))
    }

    //859 millis
    @Test
    fun testEfc2() {
        val csp = Csp.parse(CspSample.EfcProdFactoryRules)
        val t1 = System.currentTimeMillis()
        assertTrue(csp.isSat())
        val t2 = System.currentTimeMillis()
        System.err.println("efc2.isSat Delta: " + (t2 - t1))
    }

}