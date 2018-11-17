package com.tms.csp.dnnf

import com.tms.csp.ast.*
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Test

import org.junit.Assert.assertEquals

class DnnfDeadCubesTest : CspBaseTest2() {

    @Test
    @Throws(Exception::class)
    fun superTiny() {
        val space = Space()
        val c1 = space.parseExp("xor(a b)")
        assertEquals("xor(a b)", c1.toString())
        val csp = Csp(space)
        csp.addConstraint(c1)
        val n = c1.toDnnf()

        val EMPTY = DynCube(space)

        val pa = space.lits("a")
        val pb = space.lits("b")
        val na = space.lits("!a")
        val nb = space.lits("!b")

        assertEquals(EMPTY, n.bb)
        assertEquals(nb, n.con("a").bb)
        assertEquals(na, n.con("b").bb)
        assertEquals(pb, n.con("!a").bb)
        assertEquals(pa, n.con("!b").bb)

    }

    //    @Test
    //    public void tiny() throws Exception {
    //        DnnfCsp csp = loadCspTiny();
    //        Exp n = csp.getConditioned();
    //
    //        Assignments bb = n.bb;
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
    //        assertEquals(EMPTY, n.bb);
    //        assertEquals(nb, n.con("a").bb);
    //        assertEquals(nanc, n.con("b").bb);
    //        assertEquals(pbnc, n.con("!a").bb);
    //        assertEquals(pa, n.con("!b").bb);
    //    }
    //
    //    @Test
    //    public void trim() throws Exception {
    //        DnnfCsp csp = loadCspTrim();
    //        Exp n = csp.getConditioned();
    //
    //
    //        Set<Var> _complexVars = n.get_complexVars();
    //        for (Var vr : _complexVars) {
    //            printBB(n, vr.toString());
    //            printBB(n, "!" + vr.toString());
    //        }
    //
    //
    //    }
    //

    internal fun printBB(n: Exp) {
        System.err.println(n.serialize() + "\n  bb: " + n.bb)
        System.err.println()
    }


    internal fun printBB(n: Exp, pic: String) {
        val nn = n.con(pic)
        System.err.println(pic)
        System.err.println("\t" + nn.serialize())
        System.err.println("\t" + nn.bb)
        System.err.println()
    }

    @Test
    @Throws(Exception::class)
    fun test1() {
        var n = CspSample.TinyNoDc.compileDnnf()
        System.err.println(n)
        var bb = n.bb.asLitSet()
        System.err.println("bb[$bb]")

        n = n.con("a")

        System.err.println(n)
        bb = n.bb.asLitSet()
        System.err.println("bb[$bb]")

    }

    @Test
    @Throws(Exception::class)
    fun camry() {
        val csp = CspSample.Camry2011NoDc.csp()
        val n = csp.toDnnf()
        val bb = n.bb.asLitSet()
        System.err.println("bb$bb")
    }


    @Test
    @Throws(Exception::class)
    fun efc1() {
        val n = CspSample.EfcOriginal.compileDnnf()

        var t1 = System.currentTimeMillis()
        var bb = n.bb.asLitSet()
        var t2 = System.currentTimeMillis()
        System.err.println("bb 1st run Delta: " + (t2 - t1))
        System.err.println("\t bb[$bb]")


        t1 = System.currentTimeMillis()
        bb = n.bb.asLitSet()
        t2 = System.currentTimeMillis()
        System.err.println("bb 2nd run Delta: " + (t2 - t1))
        System.err.println("\t bb[$bb]")
    }


    // for picks YR_2013 SER_tundra
    @Test
    @Throws(Exception::class)
    fun efc3() {
        var n = CspSample.EfcOriginal.compileDnnf()
        n = n.con("YR_2013", "SER_tundra")
        val bb = n.bb.asLitSet()
        System.err.println(bb)
    }


    @Test
    @Throws(Exception::class)
    fun efc4() {
        var n = CspSample.EfcOriginal.compileDnnf()

        var t1 = System.currentTimeMillis()
        var bb = n.bb.asLitSet()
        var t2 = System.currentTimeMillis()
        System.err.println("computeBB2 Delta: " + (t2 - t1))
        System.err.println("\t bb[$bb]")


        n = n.con("YR_2014")
        t1 = System.currentTimeMillis()
        bb = n.bb.asLitSet()
        t2 = System.currentTimeMillis()
        System.err.println("computeBB2 Delta: " + (t2 - t1))
        System.err.println("\t YR_2014 bb[$bb]")
    }

    @Test
    @Throws(Exception::class)
    fun efcFromTinyDnnf() {
        val n = CspSample.EfcOriginalDnnf.parseDnnf()

        var t1 = System.currentTimeMillis()
        var bb = n.bb.asLitSet()
        var t2 = System.currentTimeMillis()
        System.err.println("bb 1st run Delta: " + (t2 - t1))
        System.err.println("\t bb[$bb]")


        t1 = System.currentTimeMillis()
        bb = n.bb.asLitSet()
        t2 = System.currentTimeMillis()
        System.err.println("bb 2nd run Delta: " + (t2 - t1))
        System.err.println("\t bb[$bb]")
    }


}