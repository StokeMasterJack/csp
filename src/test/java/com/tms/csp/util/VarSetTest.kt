package com.tms.csp.util

import com.tms.csp.ast.Space
import com.tms.csp.ast.toCube
import com.tms.csp.fm.dnnf.products.Cube
import kotlin.test.Test
import kotlin.test.assertEquals

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


}