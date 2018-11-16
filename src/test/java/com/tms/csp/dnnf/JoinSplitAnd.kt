package com.tms.csp.dnnf

import com.tms.csp.ast.Csp
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Prefix
import com.tms.csp.data.CspSample
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.ssutil.rpad
import com.tms.csp.ssutil.tt
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


        val clob = tt("Load text"){CspSample.EfcOriginal.loadText()}
        val csp = tt("Parse text"){Csp.parse(clob)}

        val nRough = tt("Compile dnnf"){csp.toDnnf()}

        val nSmooth = tt("Smooth"){nRough.smooth}

        var satCount = tt("SatCount"){nSmooth.satCount}

        //        assertEquals(3460501125462739908L, satCountSmooth);    //no at
        assertEquals(expectedSatCount, satCount)    //at

        val nn = tt("CopyToOtherSpace"){nSmooth.copyToOtherSpace()}

        val tiny = tt("SerializeTinyDnnf"){nn.space.serializeTinyDnnf()}

        val nnn = tt("ParseTinyDnnf"){Exp.parseTinyDnnf(tiny)}

        satCount = tt("SatCountSmooth"){nnn.satCount}

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