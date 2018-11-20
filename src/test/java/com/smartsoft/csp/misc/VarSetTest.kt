package com.smartsoft.csp.misc

import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.ast.toCube
import com.smartsoft.csp.fm.dnnf.products.Cube
import kotlin.test.*

class VarSetTest {


    @Test
    fun test() {
        val parseVarCodes = Space.parseVarCodes("a b c d e")
        val sp = Space(parseVarCodes.asIterable())

        val b1 = sp.varSetBuilder()
        b1.addVar("a")
        b1.addVar("b")

        val b2 = sp.varSetBuilder()
        b2.addVar("a")
        b2.addVar("b")

        assertEquals(b1, b2)

    }

    @Test
    fun test2() {
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
    fun test3() {
        val parseVarCodes = Space.parseVarCodes("a b c d e fCon g")
        val sp = Space(parseVarCodes.asIterable())

        val b1 = sp.varSetBuilder()
        b1.addVar("a")
        b1.addVar("b")
        assertEquals(2, b1.size)
        assertTrue(b1.containsVar("a"))
        assertTrue(b1.containsVar("b"))


        val b2 = sp.varSetBuilder()
        b2.addVar("c")
        b2.addVar("d")
        assertEquals(2, b2.size)
        assertTrue(b2.containsVar("c"))
        assertTrue(b2.containsVar("d"))

        assertNotEquals(b1, b2)

        val ch = b2.addVarSet(b1)
        assertTrue(ch)
        assertEquals(4, b2.size)

        assertTrue(b2.containsVar("a"))
        assertTrue(b2.containsVar("b"))
        assertTrue(b2.containsVar("c"))
        assertTrue(b2.containsVar("d"))

        val b3 = b2.minus("d")
        assertEquals(3, b3.size)
        assertTrue(b3.containsVar("a"))
        assertTrue(b3.containsVar("b"))
        assertTrue(b3.containsVar("c"))
        assertFalse(b3.containsVar("d"))

        println("{a b c d} minus {a b}")
        val b4 = b2.minus(b1)
        assertEquals(2, b4.size)
        assertFalse(b4.containsVar("a"))
        assertFalse(b4.containsVar("b"))
        assertTrue(b4.containsVar("c"))
        assertTrue(b4.containsVar("d"))

        println("{a b c d} minus {a b c g}")
        val b5 = b2.minus(b3.plus("g"))
        assertEquals(1, b5.size)
        assertFalse(b5.containsVar("a"))
        assertFalse(b5.containsVar("b"))
        assertFalse(b5.containsVar("c"))
        assertTrue(b5.containsVar("d"))
        assertFalse(b5.containsVar("g"))

        val vs4 = sp.mkVarSet("a b c")
        println("vs4 = ${vs4}")

    }


}