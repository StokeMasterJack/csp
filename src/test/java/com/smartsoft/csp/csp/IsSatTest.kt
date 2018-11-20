package com.smartsoft.csp.csp

import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.ast.HasIsSat
import com.smartsoft.csp.data.CspSample
import org.junit.Assert.assertTrue
import org.junit.Test

class IsSatTest {

    fun parse(s: CspSample): HasIsSat {
        return Csp.parse(s)
    }

    @Test
    fun testTiny() {
        val csp = parse(CspSample.TinyDc)
        assertTrue(csp.isSat())
    }

    @Test
    fun testTrim() {
        val csp = parse(CspSample.TrimNoDc)
        assertTrue(csp.isSat())
    }

    @Test
    fun testTrimColor() {
        val csp = parse(CspSample.TrimColorNoDc)
        assertTrue(csp.isSat())
    }

    @Test
    fun testTrimColorOptions() {
        val csp = parse(CspSample.TrimColorOptionsDc)
        assertTrue(csp.isSat())
    }

    @Test
    fun testCamry2011() {
        val csp = parse(CspSample.Camry2011Dc)
        assertTrue(csp.isSat())
    }

    //591 millis
    @Test
    fun testEfc1() {
        val csp = parse(CspSample.EfcOriginal)
        val t1 = System.currentTimeMillis()
        assertTrue(csp.isSat())
        val t2 = System.currentTimeMillis()
        System.err.println("efc1.isSat Delta: " + (t2 - t1))
    }

    //859 millis
    @Test
    fun testEfc2() {
        val csp = parse(CspSample.EfcProdFactoryRules)
        val t1 = System.currentTimeMillis()
        assertTrue(csp.isSat())
        val t2 = System.currentTimeMillis()
        System.err.println("efc2.isSat Delta: " + (t2 - t1))
    }

}