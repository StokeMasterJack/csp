package com.tms.csp.minModels;

import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.data.CspSample;
import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Var;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.ssutil.Path;
import com.tms.csp.ssutil.TT;
import com.tms.csp.util.varSets.VarSet;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MinFCardTest extends CspBaseTest2 {

    Path p = new Path("csp/minModels/stageisfullrules.txt");

    @Test
    public void test1A() throws Exception {
        Exp n = Csp.parse("a").toDnnf();

        assertEquals(1, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(0, n.minFCard());


        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");
        assertEquals(1, mm.getProducts().size());

        Cube onlyCube = mm.getProducts().iterator().next();
        assertEquals(0, onlyCube.getFalseVarCount());

    }

    @Test
    public void test1B() throws Exception {
        Exp n = Csp.parse("!a").toDnnf();

        assertEquals(1, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(1, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

        Cube onlyCube = mm.getProducts().iterator().next();
        assertEquals(1, onlyCube.getFalseVarCount());

    }

    @Test
    public void test2A() throws Exception {
        Exp n = Csp.parse("and(a b)").toDnnf();

        assertEquals(2, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(0, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(0, cube.getFalseVarCount());
        }
    }

    @Test
    public void test2B() throws Exception {
        Exp n = Csp.parse("and(!a !b)").toDnnf();

        assertEquals(2, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(2, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());


        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(2, cube.getFalseVarCount());
        }

    }

    @Test
    public void test2C() throws Exception {
        Exp n = Csp.parse("and(a !b)").toDnnf();

        assertEquals(2, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(1, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(1, cube.getFalseVarCount());
        }

    }

    @Test
    public void test2D() throws Exception {
        Exp n = Csp.parse("and(!a b)").toDnnf();

        assertEquals(2, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(1, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(1, cube.getFalseVarCount());
        }

    }

    /*
        csp:        and(a b c)
        varCount:   3
        satCount:   1
        minCard:    3
        products:   +a +b +c
        minModels:  +a +b +c
    */
    @Test
    public void test3a() throws Exception {
        Csp csp = Csp.parse("and(a b c)");
        Exp n = csp.toDnnf();

        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(3, n.getVarCount());
        assertEquals(0, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(0, cube.getFalseVarCount());
        }

    }

    @Test
    public void test3b() throws Exception {
        Csp csp = Csp.parse("and(!a b c)");
        Exp n = csp.toDnnf();

        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(3, n.getVarCount());
        assertEquals(1, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(1, cube.getFalseVarCount());
        }

    }

    @Test
    public void test3c() throws Exception {
        Csp csp = Csp.parse("and(!a !b c)");
        Exp n = csp.toDnnf();

        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(3, n.getVarCount());
        assertEquals(2, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(2, cube.getFalseVarCount());
        }
    }

    /*
        csp:       xor(a b)
        varCount:  2
        satCount:  2
        minNCard:   1
        products:  +a -b
                   -a +b
        minFModels: +a -b
                    -a +b
    */
    @Test
    public void testXor2() throws Exception {
        Csp csp = Csp.parse("xor(a b)");
        Exp n = csp.toDnnf();

        assertEquals(2, n.getSatCount());
        assertEquals(2, n.getProducts().size());
        assertEquals(2, n.getVarCount());


        assertEquals(1, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(2, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(1, cube.getFalseVarCount());
        }

    }

    /*
       csp:       xor(a b)
       varCount:  3
       satCount:  3
       minNCard:  2
       products:  +a -b -c
                  -a +b -c
                  -a -b +c
       minFModels: +a -b -c
                   -a +b -c
                   -a -b +c
   */
    @Test
    public void testXor3() throws Exception {
        Csp csp = Csp.parse("xor(a b c)");
        Exp n = csp.toDnnf();

        assertEquals(3, n.getSatCount());
        assertEquals(3, n.getProducts().size());
        assertEquals(3, n.getVarCount());


        assertEquals(2, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(3, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(2, cube.getFalseVarCount());
        }

    }


    /*


        csp:       xor(a b)
                   conflict(a c)
        varCount:  3
        satCount:  3
        minFCard:  1
        products:  +a -b -c
                   -a +b -c
                   -a +b +c

        minFModels: -a +b +c

     */
    @Test
    public void test3() throws Exception {
        Csp csp = Csp.parse("xor(a b)\nconflict(a c)");
        Exp n = csp.toDnnf().getSmooth();

        Set<Cube> products = n.getProducts();
        System.err.println("products: ");
        for (Cube p : products) {
            System.err.println("  " + p);
        }

        assertEquals(3, n.getVarCount());
        assertEquals(3, n.getSatCount());
        assertEquals(3, n.getProducts().size());
        assertEquals(1, n.minFCard());

        Exp mm = n.minFModels();
        System.err.println("mm[" + mm + "]");

        Set<Cube> mmProducts = mm.getProducts();
        System.err.println("minModels: ");
        for (Cube p : mmProducts) {
            System.err.println("  " + p);
            assertEquals(1, p.getFalseVarCount());
        }

        assertEquals(1, mm.getProducts().size());


    }

    @Test
    public void testTiny() throws Exception {

        Csp csp = loadCsp(CspSample.Tiny);
        Exp n = csp.toDnnf().getSmooth();

        assertEquals(3, n.getProducts().size());

        Set<Cube> products = n.getProducts();
        for (Cube p : products) {

            System.err.println(p);
        }

        assertEquals(1, n.minFCard());
        assertEquals(1, n.minFModels().getProducts().size());
    }

    @Test
    public void testTrim() throws Exception {

        Csp csp = loadCsp(CspSample.Trim);
        Exp n = csp.toDnnf().getSmooth();


        assertEquals(4, n.minTCard());
        assertEquals(18, n.minFCard());

        Exp minT = n.minTModels();
        Exp minF = n.minFModels();

        for (Cube cube : minT.getCubesSmooth()) {
            assertEquals(4, cube.getTrueVarCount());
        }

        for (Cube cube : minF.getCubesSmooth()) {
            assertEquals(18, cube.getFalseVarCount());
        }

//        Set<Cube> products = n.getProducts();
//        for (Cube p : products) {
//            VarSet falseVars = p.getVars().minus(p.getTrueVars());
//            System.err.println(falseVars.size());
//        }
    }

    @Test
    public void testCamry() throws Exception {

        Csp csp = loadCsp(CspSample.Camry2011);
        Exp n = csp.toDnnf().getSmooth();

        long minCardT1 = System.currentTimeMillis();
        int minCard = n.minCard();
        long minCardT2 = System.currentTimeMillis();
        System.err.println("minCard[" + minCard + "]");
        System.err.println("minCard Delta: " + (minCardT2 - minCardT1));

        Set<Cube> products = n.getProducts();
        System.err.println("products.size()[" + products.size() + "]");

        int cc = 0;
//        System.err.println("minModels hard-way:");
        for (Cube p : products) {
            int trueVarCount = p.getTrueVarCount();
            if (trueVarCount == 10) {
//                System.err.println("  " + p.getTrueVars());
                cc++;
            }
            if (trueVarCount < 10) {
                throw new IllegalStateException();
            }
        }
        System.err.println("cc[" + cc + "]");

        long minModelsT1 = System.currentTimeMillis();
        Exp minModels = n.minModels();
        long minModelsT2 = System.currentTimeMillis();
        System.err.println("minModels[" + minModels + "]");
        System.err.println("minModels.getProducts().size(): [" + minModels.getProducts().size() + "]");
        System.err.println("minModels Delta: " + (minModelsT2 - minModelsT1));

        System.err.println("minModels easy-way:");
        Set<Cube> mmProducts = minModels.getProducts();


        for (Cube mmProduct : mmProducts) {
            VarSet tVars = mmProduct.getTrueVars();
            int card = tVars.size();
            System.err.println("  " + card + " : " + tVars);
        }


    }

    @Test
    public void test_IS_FullRules() throws Exception {

        TT tt = new TT();


//        Path p = new Path("csp/minModels/stageisfullrules.txt");
        //        String clob = loadResource(p);

        String clob = loadResource(this, "stageisfullrules.txt");

        tt.t("load");
        Csp csp = Csp.parse(clob);
        tt.t("parse");
        Exp n = csp.toDnnf().getSmooth();
        tt.t("compile");

        assertEquals(72, n.getVarCount());
        tt.t("getVarCount");
        assertEquals(196410880L, n.getSatCount());
        tt.t("getSatCount");

        assertEquals(11, n.minTCard());
        tt.t("minTCard");

        assertEquals(39, n.minFCard());
        tt.t("minFCard");

        Exp mmt = n.minTModels();
        tt.t("minTModels");
        Exp mmf = n.minFModels();
        tt.t("minFModels");

    }

    @Test
    public void test_IS_FullRules_FixList() throws Exception {

        String clob = loadResource(this, "stageisfullrules.txt");
        Csp csp = Csp.parse(clob);
        Exp n = csp.toDnnf();


        n = n.reduce();

//        VarSet vars = n.getVars();
//        for (Var vr : vars) {
//            System.err.println(vr);
//        }

        Exp eb10_0212 = n.con("ICOL_EB10", "XCOL_0212");
        System.err.println("eb10_0212.isSat()[" + eb10_0212.isSat() + "]");


        Exp eb10_0212_3M = eb10_0212.con("ICOL_EB10", "XCOL_0212", "ACY_3M");
        System.err.println("eb10_0212_3M.isSat()[" + eb10_0212_3M.isSat() + "]");


        Exp eb10_3M = n.con("ICOL_EB10", "ACY_3M");
        System.err.println("eb10_3M.isSat()[" + eb10_3M.isSat() + "]");


        Exp c0212_3M = n.con("XCOL_0212", "ACY_3M");
        System.err.println("0212_3M.isSat()[" + c0212_3M.isSat() + "]");

        Exp hard = n.con("ACY_3M");
        Exp soft = hard.project("ICOL_EB10", "XCOL_0212");


        System.err.println("Cubes:");
        for (Cube cube : soft.getCubesSmooth()) {
            System.err.println(" " + cube);
        }


        TT tt = new TT();
        Exp minFModels = soft.minFModels();
        tt.t("minFModels");


        System.err.println("minFModels:");
        for (Cube cube : minFModels.getCubesSmooth()) {
            System.err.println(" " + cube);
        }


    }


    @Test
    public void test_Tundra_FixList() throws Exception {

        String clob = loadResource(this, "toyota-tundra.dnnf.txt");

        Exp n = Exp.parseTinyDnnf(clob);


        n = n.reduce();

        VarSet vars = n.getVars();
        for (Var var : vars) {
            System.err.println(var);
        }

        Exp c_FA13_08T5_TRAN_6AT = n.con("ICOL_FA13", "XCOL_08T5", "TRAN_6AT");
        System.err.println("FA13_08T5_TRAN_6AT.isSat()[" + c_FA13_08T5_TRAN_6AT.isSat() + "]");

        Exp c_FA13_08T5_TRAN_6AT_crewmax = n.con("ICOL_FA13", "XCOL_08T5", "TRAN_6AT", "CAB_crewmax");
        System.err.println("FA13_08T5_TRAN_6AT_crewmax.isSat()[" + c_FA13_08T5_TRAN_6AT_crewmax.isSat() + "]");

        TT tt = new TT();

        Exp hard = n.con("CAB_crewmax");
        Exp soft = hard.project("ICOL_FA13", "XCOL_08T5", "TRAN_6AT");
        Exp minFModels = soft.minFModels();

        tt.t("FixList");


        System.err.println("Fix List:");
        for (Cube cube : minFModels.getCubesSmooth()) {
            System.err.println("  " + cube);
        }
    }


}
