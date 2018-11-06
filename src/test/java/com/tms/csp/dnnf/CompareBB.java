package com.tms.csp.dnnf;

import com.tms.csp.TestConfig;
import com.tms.csp.ast.Csp;
import com.tms.csp.ast.DynCube;
import com.tms.csp.ast.Exp;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CompareBB extends CspBaseTest2 {

    String sienna = "SiennaFullRules.txt";
    String tundra = "tundra_2014_fullrules.txt";

    @Test
    public void compareBB_Sienna() throws Exception {
        String clob = loadResource(this, sienna);
        compareBB(clob);
    }

    @Test
    public void compareBB_Tundra() throws Exception {
        String clob = loadResource(this, tundra);
        compareBB(clob);
    }

    //  4/28/2014 56m 51s bb[]
    @Test
    public void compareBB_efcOriginal() throws Exception {
        if (TestConfig.getRunSlowTests()) {
            String clob = loadResource(efcOriginal);
            compareBB(clob);
        }


    }

    private void compareBB(String clob) throws Exception {
        Csp csp = Csp.parse(clob);
        csp.propagate();
        csp.simplifyAlwaysTrueVars();
        csp.propagate();
        assert csp.isSat();

        DynCube cspBb = csp.getBB();

        Exp n = csp.toDnnf();
        assert n.checkDnnf();

        DynCube dnnfBb1 = n.getBB1();
        DynCube dnnfBb2 = n.getBB2();


        System.err.println("csp bb:      " + cspBb);
        System.err.println("dnnf bb1:    " + dnnfBb1);
        System.err.println("dnnf bb2:    " + dnnfBb2);

        assertEquals(cspBb, dnnfBb1);
        assertEquals(cspBb, dnnfBb2);


    }


}
