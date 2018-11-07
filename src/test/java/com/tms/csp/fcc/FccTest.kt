package com.tms.csp.fcc

import com.tms.csp.ast.Csp
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Assert.*
import org.junit.Test

class FccTest : CspBaseTest2() {

    @Test
    fun testFccsWithOnTundra1() {
        val csp = Csp.parse(CspSample.Tundra)
        csp.propagate()

        val formula = csp.mkFormula().asFormula()
        val fccs = formula.computeComplexFccs2() ?: throw IllegalStateException()


        assertEquals(2, fccs.size.toLong())

        System.err.println("fccs: ")
        for (fcc in fccs) {
            System.err.println("fcc: $fcc")
        }
    }

    @Test
    fun testFccsWithOnEfc() {


        val csp = Csp.parse(CspSample.EfcOriginal)
        csp.propagate()

        val formula = csp.mkFormula().asFormula()
        val fccs = formula.computeComplexFccs2()

        assertNull(fccs)

    }

    @Test
    fun testFccsWithOnTundra() {
        val csp = Csp.parse(CspSample.Tundra)
        csp.propagate()

        val formula = csp.mkFormula().asFormula()
        val fccs = formula.complexFccs ?: throw IllegalStateException()

        assertTrue(fccs.isDAnd)

        System.err.println("fccs.op1    [" + fccs.op() + "]")
        System.err.println("fccs.getCls[" + fccs.javaClass + "]")

        System.err.println("fccs[" + fccs.size() + "]")

        assertEquals(2, fccs.size().toLong())

    }

    @Test
    fun testFccsWithOnCamry() {
        val csp = Csp.parse(CspSample.Camry2011)
        csp.propagate()

        val formula = csp.mkFormula().asFormula()
        val fccs = formula.complexFccs ?: throw IllegalStateException()

        assertTrue(fccs.isDAnd)

        System.err.println("fccs.op1    [" + fccs.op() + "]")
        System.err.println("fccs.getCls[" + fccs.javaClass + "]")

        System.err.println("fccs[" + fccs.size() + "]")

        assertEquals(2, fccs.size().toLong())

    }

    @Test
    fun testFccsWithDrillDownOnCamry() {
        val csp = Csp.parse(CspSample.Camry2011)

        csp.propagate()

        val formula = csp.mkFormula().asFormula()
        val fccs = formula.complexFccs ?: throw IllegalStateException()

        assertTrue(fccs.isDAnd)

        assertEquals(2, fccs.size().toLong())

        val se = fccs.getFirstConjunctContaining("SE")

        println(se.simpleName)

        System.err.println(se)
        System.err.println("se[" + se.javaClass.name + "]")




//        csp = se.refine("SE");
//        assert csp.isStable();
//
//
//        formula = csp.getFormula();
//        fccs = formula.getComplexFccs();
//
//        assertEquals(4, fccs.size());
//
//        Formula v6 = fccs.getFirstConjunctContaining("V6").asFormula();
//        csp = v6.refine("V6");
//
//        formula = csp.getFormula();
//
//        formula.print();
//
//        fccs = formula.getComplexFccs();
//
//        assertEquals(2, fccs.size());

    }


}
