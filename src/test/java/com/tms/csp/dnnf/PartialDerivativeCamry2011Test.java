package com.tms.csp.dnnf;

import com.tms.csp.ast.*;
import com.tms.csp.data.CspSample;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PartialDerivativeCamry2011Test extends CspBaseTest2 {

    @Test
    public void testAssertRetractFlip() throws Exception {

        Csp csp = CspSample.Camry2011NoDc.csp();
        Space space = csp.getSpace();

        Var v6 = space.getVar("V6");
        Var ash = space.getVar("Ash");

        Exp root = space.getCsp().toDnnf().getSmooth();
        root.initParentsForArgs();


        long satCount1 = root.getSatCount();

        //set pics
        DynCube cube = new DynCube(space);
        cube.assign(v6.mkPosLit());
        space.pics = cube;

        int satCount2 = root.computeValue();
        int satCount3 = root.computeValueAfterAsserting(ash.mkPosLit());
        int satCount4 = root.computeValueAfterRetraction(v6.mkPosLit());
        int satCount5 = root.computeValueAfterFlip(v6.mkPosLit());

        assertEquals(satCount1, 520128);
        assertEquals(satCount2, 190080);
        assertEquals(satCount3, 93824);
        assertEquals(satCount4, 520128);
        assertEquals(satCount5, 330048);

    }


    @Test
    public void testSameUsingCondition1() throws Exception {

        Csp csp = CspSample.Camry2011NoDc.csp();
        Space space = csp.getSpace();
        Exp root = csp.toDnnf().getSmooth();

        long satCount1 = root.getSatCount();

        Exp v6 = root.condition("V6");
        long satCount2 = v6.getSatCount();

        Exp ash = v6.condition("Ash");
        long satCount3 = ash.getSatCount();

        assertEquals(satCount1, 520128);
        assertEquals(satCount2, 190080);
        assertEquals(satCount3, 93824);
    }

    @Test
    public void testSameUsingCondition2() throws Exception {
        Csp csp = CspSample.Camry2011NoDc.csp();
        Exp root = csp.toDnnf().getSmooth();

        long satCount4 = root.getSatCount();
        System.err.println("satCount4[" + satCount4 + "]");

        Exp v6 = root.condition("!V6");
        long satCount5 = v6.getSatCount();
        System.err.println("satCount5[" + satCount5 + "]");


        assertEquals(satCount4, 520128);
        assertEquals(satCount5, 330048);

    }

    @Test
    public void testRadioFlipUsingCalculus() throws Exception {

        Csp csp = CspSample.Camry2011NoDc.csp();
        Space space = csp.getSpace();

        Var v6 = space.getVar("V6");
        Var l4 = space.getVar("L4");
        Var hy = space.getVar("Hybrid");

        Exp root = space.getCsp().toDnnf().getSmooth();
        root.initParentsForArgs();


        long satCountAll = root.getSatCount();

        //set pics
        DynCube cube = new DynCube(space);
        cube.assign(v6.mkPosLit());
        space.pics = cube;

        int value = root.computeValue();

        int radioFlip = root.computeValueAfterRadioFlip(v6, l4);
        System.err.println("radioFlip[" + radioFlip + "]");

        System.err.println("   val[" + value + "]");

        System.err.println(" V6.pd[" + v6.mkPosLit().getPd() + "]");
        System.err.println("!V6.pd[" + v6.mkNegLit().getPd() + "]");
        System.err.println(" L4.pd[" + l4.mkPosLit().getPd() + "]");
        System.err.println("!L4.pd[" + l4.mkNegLit().getPd() + "]");
        System.err.println(" Hy.pd[" + hy.mkPosLit().getPd() + "]");
        System.err.println("!Hy.pd[" + hy.mkNegLit().getPd() + "]");


    }

    @Test
    public void testFlipRadioUsingCondition2() throws Exception {
        Csp csp = CspSample.Camry2011NoDc.csp();
        Space space = csp.getSpace();
        Exp root = csp.toDnnf().getSmooth();


        long satCountAll = root.getSatCount();
        System.err.println("satCountAll[" + satCountAll + "]");

        Exp v6 = root.condition("V6");
        long satCountV6 = v6.getSatCount();
        System.err.println("satCountV6[" + satCountV6 + "]");


        Exp l4 = root.condition("L4");
        long satCountL4 = l4.getSatCount();
        System.err.println("satCountL4[" + satCountL4 + "]");

        Exp hyb = root.condition("Hybrid");
        long satCountHybrid = hyb.getSatCount();
        System.err.println("satCountHybrid[" + satCountHybrid + "]");


    }


}
