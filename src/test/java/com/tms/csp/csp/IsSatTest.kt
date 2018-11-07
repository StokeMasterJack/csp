package com.tms.csp.csp

import com.tms.csp.ast.Csp
import com.tms.csp.ast.Parser
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Assert.assertTrue
import org.junit.Test

class IsSatTest : CspBaseTest2() {

    @Test
    @Throws(Exception::class)
    fun testTiny() {
        val csp = loadCsp(CspSample.Tiny)
        assertTrue(csp.isSat())
    }

    @Test
    @Throws(Exception::class)
    fun testTrim() {
        val csp = loadCsp(CspSample.Trim)
        assertTrue(csp.isSat())
    }

    @Test
    @Throws(Exception::class)
    fun testTrimColor() {
        val csp = loadCsp(CspSample.TrimColor)
        assertTrue(csp.isSat())
    }

    @Test
    @Throws(Exception::class)
    fun testTrimColorOptions() {
        val csp = Csp.parse(CspSample.TrimColorOptions)
        val xors = csp.getXorConstraints()
        for (xor in xors) {
            System.err.println(xor)
        }
        assertTrue(csp.isSat())
    }

    @Test
    @Throws(Exception::class)
    fun testCamry2011() {
        val csp = loadCsp(CspSample.Camry2011, true)
        assertTrue(csp.isSat())
    }

    //591 millis
    @Test
    @Throws(Exception::class)
    fun testEfc1() {
        val csp = loadCsp(efcOriginal)
        val t1 = System.currentTimeMillis()
        assertTrue(csp.isSat())
        val t2 = System.currentTimeMillis()
        System.err.println("efc1.isSat Delta: " + (t2 - t1))
    }

    //859 millis
    @Test
    @Throws(Exception::class)
    fun testEfc2() {
        val csp = Csp.parse(CspSample.EfcProdFactoryRules)
        val t1 = System.currentTimeMillis()
        assertTrue(csp.isSat())
        val t2 = System.currentTimeMillis()
        System.err.println("efc2.isSat Delta: " + (t2 - t1))
    }

}