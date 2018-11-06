package com.tms.csp.dnnf;

import com.tms.csp.ast.*;
import com.tms.csp.data.CspSample;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DnnfDeadCubesTest extends CspBaseTest2 {

    @Test
    public void superTiny() throws Exception {
        Space space = new Space();
        Exp c1 = space.parseExp("xor(a b)");
        assertEquals("xor(a b)", c1.toString());
        Csp csp = new Csp(space);
        csp.addConstraint(c1);
        Exp n = c1.toDnnf();

        DynCube EMPTY = new DynCube(space);

        DynCube pa = space.lits("a");
        DynCube pb = space.lits("b");
        DynCube na = space.lits("!a");
        DynCube nb = space.lits("!b");

        assertEquals(EMPTY, n.getBB());
        assertEquals(nb, n.con("a").getBB());
        assertEquals(na, n.con("b").getBB());
        assertEquals(pb, n.con("!a").getBB());
        assertEquals(pa, n.con("!b").getBB());

    }

//    @Test
//    public void tiny() throws Exception {
//        DnnfCsp csp = loadCspTiny();
//        Exp n = csp.getConditioned();
//
//        Assignments bb = n.getBB();
//
//        Space space = new Space();
//
//        Assignments EMPTY =  new Assignments(space);
//
//        Assignments pa = ImmutableSet.of(ds.mkLit("a"));
//        Assignments pbnc = ImmutableSet.of(ds.mkLit("b"), ds.mkLit("!c"));
//        Assignments nanc = ImmutableSet.of(ds.mkLit("!a"), ds.mkLit("!c"));
//        Assignments nb = ImmutableSet.of(ds.mkLit("!b"));
//
//        assertEquals(EMPTY, n.getBB());
//        assertEquals(nb, n.con("a").getBB());
//        assertEquals(nanc, n.con("b").getBB());
//        assertEquals(pbnc, n.con("!a").getBB());
//        assertEquals(pa, n.con("!b").getBB());
//    }
//
//    @Test
//    public void trim() throws Exception {
//        DnnfCsp csp = loadCspTrim();
//        Exp n = csp.getConditioned();
//
//
//        Set<Var> vars = n.getVars();
//        for (Var vr : vars) {
//            printBB(n, vr.toString());
//            printBB(n, "!" + vr.toString());
//        }
//
//
//    }
//

    void printBB(Exp n) {
        System.err.println(n.serialize() + "\n  bb: " + n.getBB());
        System.err.println();
    }


    void printBB(Exp n, String pic) {
        Exp nn = n.con(pic);
        System.err.println(pic);
        System.err.println("\t" + nn.serialize());
        System.err.println("\t" + nn.getBB());
        System.err.println();
    }

    @Test
    public void test1() throws Exception {
        Exp n = CspSample.TinyNoDc.compileDnnf();
        System.err.println(n);
        Set<Lit> bb = n.getBB().asLitSet();
        System.err.println("bb[" + bb + "]");

        n = n.con("a");

        System.err.println(n);
        bb = n.getBB().asLitSet();
        System.err.println("bb[" + bb + "]");

    }

    @Test
    public void camry() throws Exception {
        Csp csp = CspSample.Camry2011NoDc.csp();
        Exp n = csp.toDnnf();
        Set<Lit> bb = n.getBB().asLitSet();
        System.err.println("bb" + bb);
    }


    @Test
    public void efc1() throws Exception {
        Exp n = CspSample.EfcOriginal.compileDnnf();

        long t1 = System.currentTimeMillis();
        Set<Lit> bb = n.getBB().asLitSet();
        long t2 = System.currentTimeMillis();
        System.err.println("bb 1st run Delta: " + (t2 - t1));
        System.err.println("\t bb[" + bb + "]");


        t1 = System.currentTimeMillis();
        bb = n.getBB().asLitSet();
        t2 = System.currentTimeMillis();
        System.err.println("bb 2nd run Delta: " + (t2 - t1));
        System.err.println("\t bb[" + bb + "]");
    }


    // for picks YR_2013 SER_tundra
    @Test
    public void efc3() throws Exception {
        Exp n = CspSample.EfcOriginal.compileDnnf();
        n = n.con("YR_2013", "SER_tundra");
        Set<Lit> bb = n.getBB().asLitSet();
        System.err.println(bb);
    }


    @Test
    public void efc4() throws Exception {
        Exp n = CspSample.EfcOriginal.compileDnnf();

        long t1 = System.currentTimeMillis();
        Set<Lit> bb = n.getBB().asLitSet();
        long t2 = System.currentTimeMillis();
        System.err.println("computeBB2 Delta: " + (t2 - t1));
        System.err.println("\t bb[" + bb + "]");


        n = n.con("YR_2014");
        t1 = System.currentTimeMillis();
        bb = n.getBB().asLitSet();
        t2 = System.currentTimeMillis();
        System.err.println("computeBB2 Delta: " + (t2 - t1));
        System.err.println("\t YR_2014 bb[" + bb + "]");
    }

    @Test
    public void efcFromTinyDnnf() throws Exception {
        Exp n = CspSample.EfcOriginalDnnf.parseDnnf();

        long t1 = System.currentTimeMillis();
        Set<Lit> bb = n.getBB().asLitSet();
        long t2 = System.currentTimeMillis();
        System.err.println("bb 1st run Delta: " + (t2 - t1));
        System.err.println("\t bb[" + bb + "]");


        t1 = System.currentTimeMillis();
        bb = n.getBB().asLitSet();
        t2 = System.currentTimeMillis();
        System.err.println("bb 2nd run Delta: " + (t2 - t1));
        System.err.println("\t bb[" + bb + "]");
    }


}