package com.tms.csp.dnnf

import com.tms.csp.ast.Csp
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Prefix
import com.tms.csp.data.CspSample
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.ssutil.TT
import com.tms.csp.ssutil.rpad
import com.tms.csp.util.CspBaseTest2
import com.tms.csp.util.varSets.VarSet
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertTrue

class JoinSplitAnd : CspBaseTest2() {

    @Test
    fun testCreateEfcDnnf() {
        val efcDnnf = createEfcDnnf()
    }

    @Test
    fun testCreateYsCombos() {
        val efcDnnf = createEfcDnnf()
        val ysCombos: Set<Cube> = createYsCombos(efcDnnf)
        printYsCombos(ysCombos)
    }

    @Test
    fun testCreateYsDnnfs() {
        val efcDnnf = createEfcDnnf()
        val ysCombos: Set<Cube> = createYsCombos(efcDnnf)
        createYsDnnfs(efcDnnf, ysCombos)
    }

    private fun createYsCombos(efcDnnf: Exp): Set<Cube> {
        val space = efcDnnf.space

        val yearVars = space.vars.filter(Prefix.YR)
        val seriesVars = space.vars.filter(Prefix.SER)
        val ys = VarSet.union(space, yearVars, seriesVars)

        val ysCombosDnnf = efcDnnf.project(ys)


        return ysCombosDnnf.computeCubesSmooth()
    }

    private fun createEfcDnnf(): Exp {
        val sample = CspSample.EfcOriginal
        val expectedSatCount = sample.expectedSatCount

        val tt = TT()
        val clob = CspSample.EfcOriginal.loadText()
        tt.t("load text")
        val csp = Csp.parse(clob)

        csp.conditionOutAtVars()   //at makes toyota-wide compile slower
        tt.t("parse text")

        val nRough = csp.toDnnf()
        tt.t("toDnnf")

        val nSmooth = nRough.smooth
        tt.t("getSmooth")

        var satCount = nSmooth.satCount
        tt.t("satCountSmooth")

        //        assertEquals(3460501125462739908L, satCountSmooth);    //no at
        assertEquals(expectedSatCount, satCount)    //at

        val nn = nSmooth.copyToOtherSpace()
        tt.t("copyToOtherSpace")

        val tiny = nn.space.serializeTinyDnnf()
        tt.t("serializeTinyDnnf")

        val nnn = Exp.parseTinyDnnf(tiny)
        tt.t("parseTinyDnnf")

        satCount = nnn.satCount
        tt.t("satCountSmooth")

        assertEquals(expectedSatCount, satCount)   //at

        return nnn

    }

    private fun printYsCombos(ysCombos: Set<Cube>) {
        for (cube in ysCombos) {
            println(cube.trueVars.codes.sortedDescending().joinToString(separator = " "))
        }
    }

    private fun createYsDnnfs(efcDnnf: Exp, ysCombos: Set<Cube>) {
        val tinyDnnfAll = efcDnnf.space.serializeTinyDnnf()
        val satCountAll = efcDnnf.satCount

        print("TinyDnnfSize Report".rpad(30))
        print("SatCount".rpad(30))
        println("TinyDnnfSize".rpad(30))

        print("All Series Years".rpad(30))
        print(satCountAll.rpad(30))
        println(tinyDnnfAll.length.rpad(30))

        var runningTotal = BigInteger.ZERO

        for (cube in ysCombos) {
            val syDnnf = efcDnnf.condition(cube).gc()
            val syTinyDnnf = syDnnf.space.serializeTinyDnnf()
            assert(syDnnf.isDnnf)
            val sySatCount = syDnnf.smooth.satCount
            val sySer = cube.trueVars.codes.sortedDescending().joinToString(separator = " ")

            print(sySer.rpad(30))
            print(sySatCount.rpad(30))
            println(syTinyDnnf.length.rpad(30))

            assertTrue(satCountAll > sySatCount)
            assertTrue(tinyDnnfAll.length > sySer.length)

            runningTotal = runningTotal.plus(sySatCount)
        }

        print("Sat Count Sum".rpad(30))
        print(runningTotal.toString().rpad(30))

        assertEquals(satCountAll, runningTotal)
    }


}