package com.smartsoft.csp.pl

import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.data.CspSample
import com.smartsoft.csp.util.CspBaseTest2
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CnfTest : CspBaseTest2() {

    @Test
    fun testCamry() {

        val expectedSatCount = 520128.toBigInteger()
        val csp = Csp.parse(CspSample.Camry2011NoDc)
        val satCountPL1 = csp.satCountPL()
        val d1 = csp.toDnnfSmooth()
        val satCount1 = d1.satCount

        csp.toCnf()

        assertTrue(csp.isCnf)
        val satCountPL2 = csp.satCountPL()
        val d2 = csp.toDnnfSmooth()
        val satCount2 = d2.satCount

        assertEquals(expectedSatCount.toLong(), satCountPL1)
        assertEquals(expectedSatCount, satCount1)
        assertEquals(expectedSatCount.toLong(), satCountPL2)
        assertEquals(expectedSatCount, satCount2)


    }

    //34s
    @Test
    fun testTundra() {

        val expectedSatCount = 1545337914624L

        val csp = CspSample.Tundra.parseCsp()

        val satCountPL1 = csp.satCountPL()
        val d1 = csp.toDnnfSmooth()
        val satCount1 = d1.satCount

        csp.toCnf()

        assertTrue(csp.isCnf)
        val satCountPL2 = csp.satCountPL()
        val d2 = csp.toDnnfSmooth()
        val satCount2 = d2.satCount

        assertEquals(expectedSatCount, satCountPL1)
        assertEquals(expectedSatCount.toBigInteger(), satCount1)
        assertEquals(expectedSatCount, satCountPL2)
        assertEquals(expectedSatCount.toBigInteger(), satCount2)


        val dimacs = csp.serializeDimacs()
        assertTrue(!dimacs.isEmpty())

    }


    /**
     * This is to make something small enough that would actually complete when converting to CNF
     */
    private fun buildEfc2013MinusTundraPL(): Csp {
        val clob = CspSample.EfcOriginal.loadText()
        var csp1 = Csp.parse(clob)
        csp1 = csp1.condition("YR_2013 !YR_2014")
        csp1.simplifySeriesModelAnds()
        csp1 = csp1.condition("!SER_tundra")
        csp1.simple!!.clear()
        return csp1
    }


    @Test
    fun testEfc2013MinusTundraToCnf() {
        val csp1 = buildEfc2013MinusTundraPL()

        csp1.toCnf()
        val dimacs = csp1.serializeDimacs()
        assertTrue(!dimacs.isEmpty())
    }

    @Test
    fun testEfc2013MinusTundraToDnnfPlusSatCount() {
        val csp1 = buildEfc2013MinusTundraPL()

        val exp = csp1.toDnnf().smooth
        val satCount = exp.satCount

//        System.err.println("satCount[$satCount]")
//        System.err.println()
//        System.err.println()
//        System.err.println(exp.serializeTinyDnnfSpace())


    }


}
