package com.tms.csp.dnnf

import com.tms.csp.ast.Csp
import com.tms.csp.ast.toCubes
import com.tms.csp.data.CspSample
import com.tms.csp.fm.dnnf.vars.VarGrp
import org.junit.Assert.assertEquals
import org.junit.Test

class Projection {


    @Test
    fun tiny() {
        var n = Csp.compileDnnf(CspSample.TinyNoDc).smooth

        assertEquals(3L, n.satCountLong)
        assertEquals(3, n.forEachSatCount.toLong())

        n.printCubes()
        System.err.println()


        n = n.project("a", "b").smooth


        assertEquals(2, n.vars.size)
        assertEquals(2L, n.satCountLong)


        n.printCubes()

    }


    @Test
    fun trimWithOutVars() {

        val expectedCubes3 = """
            L4 Base
            L4 LE
            V6 LE
            L4 XLE
            V6 XLE
            L4 SE
            V6 SE
            Hybrid Hyb
        """.trimIndent()

        val grades = "Base Hyb LE SE XLE"
        val engines = "Hybrid L4 V6"
        //        Csp csp = loadCsp(CspSample.TrimNoDc, false);
        val csp = Csp.parse(CspSample.TrimNoDc)

        val space = csp.space

        val n1 = csp.toDnnf().smooth
        val engVars = space.mkVarSet(engines)
        val grdVars = space.mkVarSet(grades)
        val grdEngVars = engVars.plus(grdVars)

        System.err.println("engVars[$engVars]")
        System.err.println("grdVars[$grdVars]")
        System.err.println("grdEngVars[$grdEngVars]")
        System.err.println()

        val n2 = n1.project(engVars)

        val cubeCount = n2.cubes.size.toLong()
        assertEquals(3, cubeCount)
        for (cube in n2.cubes) {
            System.err.println(cube.trueVars)
        }
        System.err.println()


        val n3 = n1.project(grdEngVars)


        val products3 = expectedCubes3.toCubes(space).map { it.trueVars }

        val cubes3 = n3.cubes.map { it.trueVars }
        assertEquals(8, cubes3.size)
        assertEquals(products3, cubes3)

    }

    @Test
    fun testEfc() {
        var n = Csp.compileDnnf(CspSample.EfcOriginal)


        val space = n.space

        val vars = space.getVars(VarGrp.CORE)

        n = n.project(vars)
        val smooth = n.smooth

        val satCount = smooth.satCount.toLong()


        assertEquals(5788, satCount)


    }

}