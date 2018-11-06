package com.tms.csp.fcc;

import com.tms.csp.data.CspSample;
import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.formula.Formula;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FccTest extends CspBaseTest2 {

    @Test
    public void testFccsWithOnTundra1() throws Exception {
        Space space = load(CspSample.Tundra);
        Csp csp = space.getCsp();
        csp.propagate();

        Formula formula = csp.getFormula().asFormula();
        List<List<Exp>> fccs = formula.computeComplexFccs2();

        assertEquals(2, fccs.size());

        System.err.println("fccs: ");
        for (List<Exp> fcc : fccs) {
            System.err.println("fcc: " + fcc);
        }
    }

    @Test
    public void testFccsWithOnEfc() throws Exception {


        Csp csp = Csp.parse(CspSample.EfcOriginal);
        csp.propagate();

        Formula formula = csp.getFormula().asFormula();
        List<List<Exp>> fccs = formula.computeComplexFccs2();

        assertNull(fccs);

    }

    @Test
    public void testFccsWithOnTundra() throws Exception {
        Csp csp = Csp.parse(CspSample.Tundra);
        csp.propagate();

        Formula formula = csp.getFormula().asFormula();
        Exp fccs = formula.getComplexFccs();

        assertTrue(fccs.isDAnd());

        System.err.println("fccs.op1    [" + fccs.op() + "]");
        System.err.println("fccs.getCls[" + fccs.getClass() + "]");

        System.err.println("fccs[" + fccs.size() + "]");

        assertEquals(2, fccs.size());

    }

    @Test
    public void testFccsWithOnCamry() throws Exception {
        Csp csp = Csp.parse(CspSample.Camry2011);
        csp.propagate();

        Formula formula = csp.getFormula().asFormula();
        Exp fccs = formula.getComplexFccs();

        assertTrue(fccs.isDAnd());

        System.err.println("fccs.op1    [" + fccs.op() + "]");
        System.err.println("fccs.getCls[" + fccs.getClass() + "]");

        System.err.println("fccs[" + fccs.size() + "]");

        assertEquals(2, fccs.size());

    }

    @Test
    public void testFccsWithDrillDownOnCamry() throws Exception {
        Csp csp = Csp.parse(CspSample.Camry2011);

        csp.propagate();

        Formula formula = csp.getFormula().asFormula();
        Exp fccs = formula.getComplexFccs();

        assertTrue(fccs.isDAnd());

        assertEquals(2, fccs.size());

        Exp se = fccs.getFirstConjunctContaining("SE");

        System.err.println(se);
        System.err.println("se[" + se.getClass().getName() + "]");
        if (true) {
            return;
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
