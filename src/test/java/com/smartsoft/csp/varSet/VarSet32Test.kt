package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.ast.toCube
import com.smartsoft.csp.dnnf.products.Cube
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class VarSet32Test {

    @Test
    fun testSize() {
        val sp = Space("a b c d e f g h i j k")
        val b0 = sp.mkVarSet("")
        val b1 = sp.mkVarSet("a")
        val b2 = sp.mkVarSet("a b")
        val b3 = sp.mkVarSet("a b c")
        val b4 = sp.mkVarSet("a b c d")

        assertEquals(EmptyVarSet::class, b0::class)
        assertEquals(SingletonVarSet::class, b1::class)
        assertEquals(VarPair::class, b2::class)
        assertEquals(VarSetBuilder::class, b3::class)
        assertEquals(VarSetBuilder::class, b4::class)

        assertEquals(0, b0.size)
        assertEquals(1, b1.size)
        assertEquals(2, b2.size)
        assertEquals(3, b3.size)
        assertEquals(4, b4.size)


    }

    @Test
    fun testEquals() {
        val sp = Space("a b c d e")

        val b1 = sp.varSetBuilder()
        b1.addVar("a")
        b1.addVar("b")

        val b2 = sp.varSetBuilder()
        b2.addVar("a")
        b2.addVar("b")

        assertEquals(b1, b2)

    }

    @Test
    fun testToCube() {
        val space = Space();
        val sCube = "MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560      ENG_L4     !ENG_V6 !ENG_Hybrid    GRD_Base     !GRD_LE     !GRD_SE    !GRD_XLE    !GRD_Hyb      TX_6MT     !TX_6AT    !TX_ECVT"
        val cube1: Cube = sCube.toCube(space)
        val cube2: Cube = sCube.toCube(space)

        val vars1 = cube1.vars
        val vars2 = cube2.vars

        assertEquals(vars1, vars2)

        val trueVars1 = cube1.trueVars
        val trueVars2 = cube2.trueVars

        assertEquals(trueVars1, trueVars2)


    }

    @Test
    fun testAddRemoveVarSet() {
        val sp = Space("a b c d e f g")

        val b1 = sp.mkVarSetBuilder("a b")
        assertEquals(2, b1.size)
        assertTrue(b1.containsVar("a"))
        assertTrue(b1.containsVar("b"))
        b1.assertSer("a b")

        val b2 = sp.mkVarSetBuilder("c d")
        assertEquals(2, b2.size)
        assertTrue(b2.containsVar("c"))
        assertTrue(b2.containsVar("d"))
        b2.assertSer("c d")

        assertNotEquals(b1, b2)

        b2.addVarSet(b1)
        assertEquals(4, b2.size)
        assert(b2.containsVar("a"))
        assert(b2.containsVar("b"))
        assert(b2.containsVar("c"))
        assert(b2.containsVar("d"))
        b2.assertSer("a b c d")


        b2.removeVar("d")
        assertEquals(3, b2.size)
        assert(b2.containsVar("a"))
        assert(b2.containsVar("b"))
        assert(b2.containsVar("c"))
        assert(b2.notContainsVar("d"))
        b2.assertSer("a b c")

        b2.removeVarSet(b1)
        assertEquals(1, b2.size)
        assert(b2.notContainsVar("a"))
        assert(b2.notContainsVar("b"))
        assert(b2.containsVar("c"))
        assert(b2.notContainsVar("d"))
        b2.assertSer("c")

        b2.addVarSet("a b f g")
        b2.assertSer("a b c f g")

    }

    @Test
    fun testPlus() {
        val sp = Space("a b c d e f g h i j k")
        val b1 = sp.mkVarSet("a b")
        val b2 = sp.mkVarSet("c d")
        val b3 = sp.mkVarSet("a b c d")
        val b4 = sp.mkVarSet("c d e f g h i")


        val b12 = b1.plus(b2)
        val b21 = b2.plus(b1)

        assertEquals(b12, b3)
        assertEquals(b21, b3)
        assertEquals(4, b12.size)
        b12.assertSer("a b c d")


        val b3Plus4 = b3.plus(b4)

        b3Plus4.assertSer("a b c d e f g h i")

    }


    @Test
    fun testMinusPlus() {
        val sp = Space("a b c d e f g h i j k")
        val b1 = sp.mkVarSet("a b")
        val b2 = sp.mkVarSet("c d")
        val b3 = sp.mkVarSet("a b c d")
        val b4 = sp.mkVarSet("c d e f g h i")


        val b12 = b1.plus(b2)
        val b21 = b2.plus(b1)

        assertEquals(b12, b3)
        assertEquals(b21, b3)
        assertEquals(4, b12.size)
        b12.assertSer("a b c d")


        val b3Plus4 = b3.plus(b4)
        val b4Minus3 = b4.minus(b3)



        b3Plus4.assertSer("a b c d e f g h i")
        b4Minus3.assertSer("e f g h i")

    }

    @Test
    fun testMinus() {
        val sp = Space("a b c d e f g h i j k")
        val b1 = sp.mkVarSet("a b")
        val b2 = sp.mkVarSet("c d")
        val b3 = sp.mkVarSet("a b c d")
        val b4 = sp.mkVarSet("c d e f g h i")

        val b4MinusB3 = b4.minus(b3)
        b4MinusB3.assertSer("e f g h i")

        val b4minusB2 = b4.minus(b2)
        b4minusB2.assertSer("e f g h i")


    }

    @Test
    fun tesVarSetBuilderMinusSingletonVarSet() {
        val sp = Space("a b c d e f g h i j k")
        val b1 = sp.mkVarSet("a b c d e f")
        val b2 = sp.mkVarSet("c")
        val b3 = sp.mkVarSet("j")

        assert(b1 is VarSetBuilder)
        assert(b2 is SingletonVarSet)
        assert(b3 is SingletonVarSet)


        val b1MinusB2 = b1.minus(b2)
        b1MinusB2.assertSer("a b d e f")

        val b1MinusB3 = b1.minus(b3)
        b1MinusB3.assertSer("a b c d e f")


    }

    @Test
    fun tesSingletonVarSetMinusSingletonVarSet() {
        val sp = Space("a b c d e f g h i j k")
        val b1 = sp.mkVarSet("c")
        val b2 = sp.mkVarSet("c")
        val b3 = sp.mkVarSet("j")

        assert(b1 is SingletonVarSet)
        assert(b2 is SingletonVarSet)
        assert(b3 is SingletonVarSet)


        val b1MinusB2 = b1.minus(b2)
        assert(b1MinusB2 is EmptyVarSet) { b1MinusB2.toStringDetail() }

        val b2MinusB1 = b2.minus(b1)
        assert(b2MinusB1 is EmptyVarSet)

        val b2MinusB3 = b2.minus(b3)
        assert(b2MinusB3 is SingletonVarSet)
        assertEquals(b2MinusB3, b2)


    }

    @Test
    fun testRemoveVarSet() {
        val sp = Space("a b c d e f g h i j k")

        val b3 = sp.mkVarSetBuilder("a b c d")
        val b4 = sp.mkVarSetBuilder("c d e f g h i")


        b4.assertSer("c d e f g h i")
        b4.removeVarSet(b3)

        b4.assertSer("e f g h i")


    }

    @Test
    fun testOverlap() {
        val sp = Space("a b c d e f g h i j k")
        val b0 = sp.mkVarSet("k")
        val b1 = sp.mkVarSet("a")
        val b2 = sp.mkVarSet("c d")
        val b3 = sp.mkVarSet("a b c d")
        val b4 = sp.mkVarSet("c d e f g h i")

        assert(b0.anyVarOverlap(b0))
        assert(b0.isVarDisjoint(b1))
        assert(b0.isVarDisjoint(b2))
        assert(b0.isVarDisjoint(b3))
        assert(b0.isVarDisjoint(b4))

        assert(b1.anyVarOverlap(b1))
        assert(b1.isVarDisjoint(b2))
        assert(b1.anyVarOverlap(b3))
        assert(b1.isVarDisjoint(b4))

        assert(b2.anyVarOverlap(b2))
        assert(b2.anyVarOverlap(b3))
        assert(b2.anyVarOverlap(b4))


        assert(b3.anyVarOverlap(b3))
        assert(b3.anyVarOverlap(b4))

        assert(b4.anyVarOverlap(b4))
    }


}