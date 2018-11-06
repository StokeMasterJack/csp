package com.tms.csp.minModels;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.data.CspSample;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.ssutil.Path;
import com.tms.csp.ssutil.TT;
import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.util.varSets.VarSet;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MinTCardTest extends CspBaseTest2 {

    Path p = new Path("csp/minModels/stageisfullrules.txt");

    @Test
    public void test1A() throws Exception {
        Exp n = Csp.parse("a").toDnnf().getSmooth();

        assertEquals(1, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(1, n.minCard());


        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");
        assertEquals(1, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(1, cube.getTrueVarCount());
        }

    }

    @Test
    public void test1B() throws Exception {
        Exp n = Csp.parse("!a").toDnnf();

        assertEquals(1, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(0, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());
        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(0, cube.getTrueVarCount());
        }

    }

    @Test
    public void test2A() throws Exception {
        Exp n = Csp.parse("and(a b)").toDnnf();

        assertEquals(2, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(2, n.minTCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());


        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(2, cube.getTrueVarCount());
        }

    }

    @Test
    public void test2B() throws Exception {
        Exp n = Csp.parse("and(!a !b)").toDnnf();

        assertEquals(2, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(0, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());
        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(0, cube.getTrueVarCount());
        }


    }

    @Test
    public void test2C() throws Exception {
        Exp n = Csp.parse("and(a !b)").toDnnf();

        assertEquals(2, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(1, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

    }

    @Test
    public void test2D() throws Exception {
        Exp n = Csp.parse("and(!a b)").toDnnf();

        assertEquals(2, n.getVarCount());
        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(1, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

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
        assertEquals(3, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

    }

    @Test
    public void test3b() throws Exception {
        Csp csp = Csp.parse("and(!a b c)");
        Exp n = csp.toDnnf();

        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(3, n.getVarCount());
        assertEquals(2, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

    }

    @Test
    public void test3c() throws Exception {
        Csp csp = Csp.parse("and(!a !b c)");
        Exp n = csp.toDnnf();

        assertEquals(1, n.getSatCount());
        assertEquals(1, n.getProducts().size());
        assertEquals(3, n.getVarCount());
        assertEquals(1, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(1, mm.getProducts().size());

    }

    /*
        csp:       xor(a b)
        varCount:  2
        satCount:  2
        minCard:   1
        products:  +a -b
                   -a +b
        minModels: +a -b
                   -a +b
    */
    @Test
    public void testXor2() throws Exception {
        Csp csp = Csp.parse("xor(a b)");
        Exp n = csp.toDnnf();

        assertEquals(2, n.getSatCount());
        assertEquals(2, n.getProducts().size());
        assertEquals(2, n.getVarCount());
        assertEquals(1, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(2, mm.getProducts().size());


        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(1, cube.getTrueVarCount());
        }

    }

    /*
       csp:       xor(a b c)
       varCount:  3
       satCount:  3
       minCard:   1
       products:  +a -b -c
                  -a +b -c
                  -a -b +c
       minModels: +a -b -c
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
        assertEquals(1, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        assertEquals(3, mm.getProducts().size());

        Set<Cube> products = mm.getProducts();
        for (Cube cube : products) {
            assertEquals(1, cube.getTrueVarCount());
        }


    }


    /*


        csp:       xor(a b)
                   conflict(a c)
        varCount:  3
        satCount:  3
        minCard:   1
        products:  +a -b -c
                   -a +b -c
                   -a +b +c

        minModels: +a -b -c
                   -a +b -c
     */
    @Test
    public void test3() throws Exception {
        Csp csp = Csp.parse("xor(a b)\nconflict(a c)");
        csp.print();

//        if(true) return;
        Exp n = csp.toDnnf();

//        Set<Cube> products = n.getCubesRough();
        Set<Cube> cubes = n.getSmooth().getCubesSmooth();
        System.err.println("products: ");
        for (Cube p : cubes) {
            System.err.println("  " + p);
        }

        assertEquals(3, n.getVarCount());
        assertEquals(3, n.getSmooth().getSatCount());
        assertEquals(3, n.getSmooth().getCubesSmooth().size());
        assertEquals(1, n.minCard());

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

        Set<Cube> mmProducts = mm.getProducts();
        System.err.println("minModels: ");
        for (Cube p : mmProducts) {
            System.err.println("  " + p);
        }

        assertEquals(2, n.minModels().getProducts().size());

    }

    @Test
    public void testTiny() throws Exception {

        Csp csp = loadCsp(CspSample.Tiny, true);
        Exp n = csp.toDnnf();

//        System.err.println(n.toXml());

        Set<Cube> products = n.getProducts();
        for (Cube p : products) {
            System.err.println(p.getTrueVars());
        }

        assertEquals(1, n.minCard());
        assertEquals(2, n.minModels().getProducts().size());
    }

    @Test
    public void testTrim() throws Exception {

        Csp csp = loadCsp(CspSample.Trim);
        Exp n = csp.toDnnf().getSmooth();

        System.err.println(n.minCard());

        Set<Cube> products = n.getProducts();
        for (Cube p : products) {
            System.err.println(p.getTrueVars());
        }
    }

    @Test
    public void testCamry() throws Exception {

        Csp csp = loadCsp(CspSample.Camry2011, true);
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
    public void test5() throws Exception {

        TT tt = new TT();

//        String clob1 = loadResource(p);


        String clob2 = loadResource(this, "stageisfullrules.txt");
        Csp csp = Csp.parse(clob2);

        tt.t("load");
        tt.t("parse");
        Exp n = csp.toDnnf();
        tt.t("compile");

        assertEquals(72, n.getVarCount());
        tt.t("getVarCount");
        assertEquals(196410880L, n.getSmooth().getSatCount());
        tt.t("getSatCount");
        assertEquals(11, n.minCard());
        tt.t("minCard");

        Exp mm = n.minModels();
        System.err.println("mm[" + mm + "]");

    }

}
