package com.smartsoft.csp.varSet;

import com.smartsoft.csp.util.CspBaseTest2;
import com.smartsoft.csp.data.CspSample;
import com.smartsoft.csp.ast.Csp;
import com.smartsoft.csp.ast.Space;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VarSetTestTiny extends CspBaseTest2 {

    boolean msrpIncluded = false;

    int expectedVarCount1 = 4;

    @Before
    public void setUp() throws Exception {
        if (msrpIncluded) {
            expectedVarCount1 += 32;
        }
    }

    @Test
    public void testRemoveVar() throws Exception {


        Csp csp = Csp.parse(CspSample.TinyDc);

        VarSet vs1 = csp.getSpace().getVars();
        assertEquals(expectedVarCount1, vs1.size());

        assertTrue(vs1.containsVarCode("a"));
        assertTrue(vs1.containsVarCode("b"));
        assertTrue(vs1.containsVarCode("c"));
        assertTrue(vs1.containsVarCode("d"));

        boolean removed = vs1.removeVar("b");
        assertTrue(removed);

        assertTrue(vs1.containsVarCode("a"));
        assertFalse(vs1.containsVarCode("b"));
        assertTrue(vs1.containsVarCode("c"));
        assertTrue(vs1.containsVarCode("d"));

    }


    @Test
    public void testMinus33() throws Exception {



        Csp csp = Csp.parse(CspSample.TinyDc);

        VarSet vs1 = csp.getSpace().getVars();
        assertEquals(expectedVarCount1, vs1.size());

        assertTrue(vs1.containsVarCode("a"));
        assertTrue(vs1.containsVarCode("b"));
        assertTrue(vs1.containsVarCode("c"));
        assertTrue(vs1.containsVarCode("d"));

        VarSetBuilder vs2 = csp.getSpace().newMutableVarSet();
        vs2.addVar("a");
        vs2.addVar("c");
        vs2.addVar("d");
        assertEquals(3, vs2.size());
        assertTrue(vs2.containsVarCode("a"));
        assertFalse(vs2.containsVarCode("b"));
        assertTrue(vs2.containsVarCode("c"));
        assertTrue(vs2.containsVarCode("d"));

        VarSet vs3 = vs1.minus(vs2);


        assertFalse(vs3.containsVarCode("a"));
        assertTrue(vs3.containsVarCode("b"));
        assertFalse(vs3.containsVarCode("c"));
        assertFalse(vs3.containsVarCode("d"));


    }

    @Test
    public void testAnyIntersection22() throws Exception {


        Csp csp = Csp.parse(CspSample.TinyDc);
        Space space = csp.getSpace();

        VarSetBuilder b1 = space.newMutableVarSet();
        b1.addVar("a");
        b1.addVar("d");

        VarSetBuilder b2 = space.newMutableVarSet();
        b2.addVar("b");
        b2.addVar("c");

        VarSetBuilder b3 = space.newMutableVarSet();
        b3.addVar("a");
        b3.addVar("c");


        assertTrue(b1 instanceof VarSetBuilder);
        assertTrue(b2 instanceof VarSetBuilder);
        assertTrue(b3 instanceof VarSetBuilder);

        assertFalse(b1.anyVarOverlap(b2));
        assertTrue(b1.anyVarOverlap(b3));
        assertTrue(b2.anyVarOverlap(b3));

        VarSet vs1 = b1.build();
        VarSet vs2 = b2.build();
        VarSet vs3 = b3.build();

        assertTrue(vs1 instanceof VarPair);
        assertTrue(vs2 instanceof VarPair);
        assertTrue(vs3 instanceof VarPair);

        assertFalse(vs1.anyVarOverlap(b2));
        assertTrue(vs1.anyVarOverlap(b3));
        assertTrue(vs2.anyVarOverlap(b3));

        assertFalse(b1.anyVarOverlap(vs2));
        assertTrue(b1.anyVarOverlap(vs3));
        assertTrue(b2.anyVarOverlap(vs3));

        assertFalse(vs1.anyVarOverlap(vs2));
        assertTrue(vs1.anyVarOverlap(vs3));
        assertTrue(vs2.anyVarOverlap(vs3));


    }

    @Test
    public void testAnyIntersection33() throws Exception {



        Csp csp = Csp.parse(CspSample.TinyDc);

        VarSet vars = csp.getSpace().getVars();
        assertEquals(expectedVarCount1, vars.size());

        assertTrue(vars.containsVarCode("a"));
        assertTrue(vars.containsVarCode("b"));
        assertTrue(vars.containsVarCode("c"));
        assertTrue(vars.containsVarCode("d"));


        VarSetBuilder b1 = csp.newMutableVarSet();
        b1.addVar("a");
        b1.addVar("b");
        b1.addVar("c");
        assertEquals(3, b1.size());
        assertTrue(b1.containsVarCode("a"));
        assertTrue(b1.containsVarCode("b"));
        assertTrue(b1.containsVarCode("c"));
        assertFalse(b1.containsVarCode("d"));

        VarSet v1 = b1.build();
        assertEquals(3, v1.size());
        assertTrue(v1.containsVarCode("a"));
        assertTrue(v1.containsVarCode("b"));
        assertTrue(v1.containsVarCode("c"));
        assertFalse(v1.containsVarCode("d"));


        VarSetBuilder b2 = csp.getSpace().newMutableVarSet();
        b2.addVar("a");
        b2.addVar("c");
        b2.addVar("d");
        assertEquals(3, b2.size());
        assertTrue(b2.containsVarCode("a"));
        assertFalse(b2.containsVarCode("b"));
        assertTrue(b2.containsVarCode("c"));
        assertTrue(b2.containsVarCode("d"));

        VarSet v2 = b2.build();
        assertEquals(3, v2.size());
        assertTrue(v2.containsVarCode("a"));
        assertFalse(v2.containsVarCode("b"));
        assertTrue(v2.containsVarCode("c"));
        assertTrue(v2.containsVarCode("d"));

        assertTrue(b1.anyVarOverlap(b2));
        assertTrue(v1.anyVarOverlap(v2));
        assertTrue(v1.anyVarOverlap(b2));
        assertTrue(b1.anyVarOverlap(v2));


    }



}
