package com.smartsoft.csp.varSets

import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.ast.bitCount
import com.smartsoft.csp.ast.toCube
import com.smartsoft.csp.dnnf.products.Cube
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class VarSetTest {

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
        assertEquals(4,b12.size)
        b12.assertSer("a b c d")

        val b34 = b3.plus(b4)
        b34.assertSer("a b c d e f g h i")
        b4.minus(b3).assertSer("e f g h i")
        b4.minus(b1).assertSer(b4.ser())
        b4.minus(b2).assertSer("e f g h i")


    }


}