package com.tms.csp.fcc

import com.tms.csp.ast.Csp
import com.tms.csp.ast.formula.Fcc
import com.tms.csp.ast.formula.FccState
import com.tms.csp.ast.formula.Fccs
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Assert.*
import org.junit.Test

class FccTest : CspBaseTest2() {

    @Test
    fun testFccsWithOnTundra1() {
        val csp = Csp.parse(CspSample.Tundra)

        val formula = csp.mkFormula().asFormula
        val fcc: FccState = formula.computeFccs()


        if (fcc is Fccs) {

            val args = fcc.args
            assertEquals(2, args.size)

            System.err.println("fccs: ")
            for (fcc in args) {
                System.err.println("fcc: $fcc")
            }

        }else{
            fail()
        }
    }

    @Test
    fun testFccsWithOnEfc() {


        val csp = Csp.parse(CspSample.EfcOriginal)

        val formula = csp.mkFormula().asFormula
        val fcc: FccState = formula.computeFccs()



        assertTrue(fcc is Fcc)

    }

    @Test
    fun testFccsWithOnTundra() {
        val csp = Csp.parse(CspSample.Tundra)

        val formula = csp.mkFormula().asFormula
        val fcc: FccState = formula.computeFccs()

        assertTrue(fcc is Fccs)

        if(fcc is Fccs) {
            val args = fcc.args
            assertEquals(2, args.size)
        }else{
            fail()
        }

    }

    @Test
    fun testFccsWithOnCamry() {
        val csp = Csp.parse(CspSample.Camry2011Dc)

        val formula = csp.mkFormula().asFormula
        val fcc: FccState = formula.computeFccs()

        assertTrue(fcc is Fccs)

        if(fcc is Fccs) {

            val args = fcc.args


            assertEquals(2, args.size)

        }

    }

    @Test
    fun testFccsWithDrillDownOnCamry() {
        val csp = Csp.parse(CspSample.Camry2011Dc)


        val formula = csp.mkFormula().asFormula
        val fcc: FccState = formula.computeFccs()

        assertTrue(fcc is Fccs)

        if(fcc is Fccs) {

            val args = fcc.args

            assertEquals(2, args.size)

//            val se = args.getFirstConjunctContaining("SE")

//        println(se!!.simpleName)

//            System.err.println(se)
//            System.err.println("se[" + se!!.simpleName + "]")

        }


//        csp = se.refine("SE");
//        assert csp.isStable();
//
//
//        formula = csp.getFormula();
//        fccs = formula.getComplexFccs();
//
//        assertEquals(4, fccs.size());
//
//        Formula v6 = fccs.getFirstConjunctContaining("V6").asFormula;
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
