package com.tms.csp.pl;

import com.tms.csp.Vars;
import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.data.CspSample;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.ssutil.TT;
import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.util.varSets.VarSet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VVConditionTest extends CspBaseTest2 {

    @Test
    public void testConditionOr() throws Exception {
        String expText = "and(or(x y z) or(a b c) or(j k m))";


        Csp csp = Csp.parse("vars(x y z a b c m j k)");
        Space space = csp.getSpace();

        System.err.println("csp[" + csp + "]");

        final Exp vvp = space.parseExp(expText);

        Exp vvpExpected;
        Exp vvpActual;

        vvpExpected = space.parseExp("and(or(x y) or(a b c) or(j k m))");
        vvpActual = vvp.conditionVV("or(x y)");
        assertEquals(vvpExpected, vvpActual);

//        vvpExpected = space.parseExp("and(or(x y z) or(b c) or(m j k))");
//        vvpActual = vvp.conditionVV("or(!a b)");
//        assertEquals(vvpExpected, vvpActual);
//
//        vvpExpected = space.parseExp("and(or(x y z) or(a c) or(m j k))");
//        vvpActual = vvp.conditionVV("or(a !b)");
//        assertEquals(vvpExpected, vvpActual);
//
//        vvpExpected = space.parseExp("and(or(x y z) or(a b c) or(m j k))");
//        vvpActual = vvp.conditionVV("or(!a !b)");
//        assertEquals(vvpExpected, vvpActual);


        System.err.println("pass");

    }


    @Test
    public void testConditionAnd() throws Exception {

        Space space = new Space(Vars.parseVarList("x y z a b c m j k"));

        final Exp vvp = space.parseExp("or(and(x y z) and(a b c) and(m j k))");

        assert vvp.getSpace() == space;

        Exp vvpExpected;
        Exp vvpActual;

        vvpExpected = space.parseExp("or(and(x z) and(a b c) and(m j k))");
        vvpActual = vvp.conditionVV("or(!x y)");

        assert vvpExpected.getSpace() == vvpActual.getSpace();

        System.err.println("vvpExpected[" + vvpExpected + "]");
        System.err.println("vvpActual  [" + vvpActual + "]");

        boolean eq = vvpExpected.equals(vvpActual);

        assertEquals(vvpExpected, vvpActual);

        vvpExpected = space.parseExp("or(and(y z) and(a b c) and(m j k))");
        vvpActual = vvp.conditionVV("or(x !y)");
        assertEquals(vvpExpected, vvpActual);

        vvpExpected = space.parseExp("or(and(a b c) and(m j k))");
        vvpActual = vvp.conditionVV("or(!x !y)");
        assertEquals(vvpExpected, vvpActual);

        vvpExpected = space.parseExp("or(and(x y z) and(a b c) and(m j k))");
        vvpActual = vvp.conditionVV("or(x y)");
        assertEquals(vvpExpected, vvpActual);


    }


    @Test
    public void testCondition() throws Exception {
        if (true) return;

//        Space csp = new Space("x or b");

        Space space = Space.withVars("vars(a b c d e f j k l m n o p)");

        Csp csp = space.getCsp();


        csp.addConstraint("or(and(a b c) and(d e f))");
//        csp.addConstraint("or(j k l m n o p)");

        csp.addConstraint("or(!a b)");
//        csp.addConstraint("or(1!k l)");


        csp.simplifyBasedOnVvs();


        ArrayList<Exp> aa = new ArrayList<Exp>(csp.getVVPlusConstraints());

        Collections.sort(aa, Exp.COMPARATOR_BY_VAR_COUNT);


        Exp vvpExpected0 = space.parseExp("or(and(a c) and(d e f))");
//        Exp vvpExpected1 = space.parseExp("or(j l m n o p)");
        assertEquals(vvpExpected0, aa.get(0));
//        assertEquals(vvpExpected1, aa.get(1));

    }

    @Test
    public void testCondition2() throws Exception {

        Space space = Space.withVars("vars(a b c d e f j k l m n o p)");

        Csp csp = space.getCsp();


        csp.addConstraint("or(and(a b c) and(d e f))");
        csp.addConstraint("or(!a b)");

        csp.propagate();


    }

    //7s
    @Test
    public void testEfc() throws Exception {
        if (true) return;
        Csp csp = createFactoryCsp();

    }

    /*
    getLargestNonXorConstraint: 6364
    createNode1 delta: 35014
    satCount[12]
    cubeCount[10]
    file size: 713867
     */

    //satCount[337]
    //cubeCount[131]
    //file size: 677347
    //createNode1 delta: 14129
    //getLargestNonXorConstraint: 6364
    @Test
    public void test_createNode1() throws Exception {
        if (true) return;
        TT tt = new TT();
        Exp pp = createNode1();
        tt.t("createNode1");
        useNode(pp);
    }

    //    satCount[337]
//    cubeCount[131]
    //file size: 677347
//    getLargestNonXorConstraint: 2242
//    createNode2 delta: 15022
    @Test
    public void test_createNode2() throws Exception {
        if (true) return;
        TT tt = new TT();
        Exp pp = createNode2();
        tt.t("createNode2");
        useNode(pp);
    }

    public void useNode(Exp pp) throws Exception {
        long satCount = pp.getSatCount();
        System.err.println("satCount[" + satCount + "]");

        Set<Cube> cubes = pp.getCubesSmooth();
        int cubeCount = cubes.size();
        System.err.println("cubeCount[" + cubeCount + "]");

//        for (Cube cube : cubes) {
//            System.err.println(cube);
//        }

        String tiny = pp.getSpace().serializeTinyDnnf();
        System.err.println("file size: " + tiny.length());


    }

    //56s
    //42s
    //42s
    //5s
    @Test
    public void test_simplifyBasedOnVvs() throws Exception {
        if (true) return;
        Csp csp = createFactoryCsp();
        csp.toNnf();
        csp.simplifyBasedOnVvs();
        assertTrue(csp.isSat());
    }

    Exp createNode1() {
        Csp csp = createFactoryCsp();
        Exp ee = csp.getLargestNonXorConstraint();
        System.err.println("getLargestNonXorConstraint: " + ee.toString().length());

        Exp n = csp.toDnnf();
        Exp nn = n.con("SER_tacoma", "YR_2014");
        VarSet cols = nn.getSpace().getVarsByPrefix("XCOL", "ICOL");

        return nn.project(cols);
    }

    Exp createNode2() {
        Csp csp = createFactoryCsp();
        csp.toNnf();
        csp.simplifyBasedOnVvs();
        Exp ee = csp.getLargestNonXorConstraint();
        System.err.println("getLargestNonXorConstraint: " + ee.toString().length());


        Exp n = csp.toDnnf();
        Exp nn = n.con("SER_tacoma", "YR_2014");
        VarSet cols = nn.getSpace().getVarsByPrefix("XCOL", "ICOL");

        return nn.project(cols);
    }


    Csp createFactoryCsp() {
        Csp csp = Csp.parse(CspSample.ProdFactoryRules);
        csp.conditionOutAtVars();
        return csp;
    }


}
