package com.tms.csp.dnnf;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.data.CspSample;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import static org.junit.Assert.*;

public class DnnfCondition extends CspBaseTest2 {

    @Test
    public void testLit() throws Exception {
        Exp n = Csp.compileDnnf("b");

        assertTrue(n.con("b").isConstantTrue());
        assertTrue(n.con("!b").isConstantFalse());

        assertEquals(1, n.getSatCount());

        Exp bt = n.con("b");
        Exp bf = n.con("!b");

        assertEquals(1, bt.getSatCount());
        assertEquals(0, bf.getSatCount());
    }


    @Test
    public void testTrim() throws Exception {
//        Exp nn = loadDnnfTrim();
        Exp nn = Csp.compileDnnf(CspSample.Trim);
        assertEquals(11, nn.getSatCount());
    }

    @Test
    public void testGetVars() throws Exception {

        Exp b = Csp.compileDnnf("b");
        assert b.isPosLit();
        assertEquals(1, b.getVarCount());


        Exp t = b.con("b");
        assertTrue(t.isConstantTrue());
        assertEquals(0, t.getVarCount());


        Exp f = b.con("!b");
        assertTrue(f.isConstantFalse());
        assertEquals(0, f.getVarCount());

    }


    /**
     * Note that the satCounts differ between camryRough and camrySmooth.
     * This is due to to don't cares.
     * <p>
     * smoothSatCount = roughSatCount * 2 ^ (smoothVarCount - roughVarCount)
     */
    @Test
    public void camrySmooth() throws Exception {
        Csp csp = CspSample.Camry2011NoDc.csp();

        Exp n = csp.toDnnf().getSmooth();
        assertTrue(n.isSat());

        assertEquals(520128, n.getSatCount());

        n = n.con("LE");
        assert n.isSmooth();

        assertTrue(n.isSat());
        assertEquals(4800, n.getSatCount());     //4800

        n = n.con("V6");
        assert n.isSmooth();

        assertTrue(n.isSat());
        assertEquals(1920, n.getSatCount());

        n = n.con("Ash");
        assert n.isSmooth();

        assertTrue(n.isSat());
        assertEquals(896, n.getSatCount());

        n = n.con("776").getSmooth();
        assert n.isSmooth();

        assertFalse(n.isSat());
        assertEquals(0, n.getSatCount());


    }


}
