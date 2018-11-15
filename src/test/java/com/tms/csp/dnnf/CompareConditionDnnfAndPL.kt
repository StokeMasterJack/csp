package com.tms.csp.dnnf

import com.tms.csp.ast.*
import com.tms.csp.data.CspSample
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.varSets.VarSet
import kotlin.test.Test

class CompareConditionDnnfAndPL {


    @Test
    fun compareAllLitsAllCsps() {
        val list: List<CspSample> = CspSample.allSimplePL
        for (sample in list) {
            val smallest = compareAllLitsOneCsp(sample)
//            println("smallest = ${smallest}")
        }
    }

    @Test
    fun compareAllPairsAllCsps() {
        val list: List<CspSample> = CspSample.allSimplePL
        for (sample in list) {
            compareAllPairsOneCsp(sample)
        }
    }

    //Camry2011NoDc R7 SR
    @Test
    fun testCamry2011NoDc_R7_SR() {
        val con = "R7 SR"

        val fullCsp = Csp.parse(CspSample.Camry2011NoDc)
        val vars = fullCsp.vars
        val space = fullCsp.space
        fullCsp.toNnf(true)

        val cube = space.parser.parseLitsToDynCube(con)

        fullCsp.space.config.tmp = "cspFull"
        val fullDnnf = fullCsp.toDnnfSmooth()
        fullCsp.space.config.tmp = null
        println("fullCsp.satCountPL: ${fullCsp.satCountPL()}")
        println("fullDnnf.satCount: ${fullDnnf.satCount}")


        val cspConditioned = fullCsp.condition(con)
//        fullCsp.print()
//        cspConditioned.print()
        fullCsp.space.config.tmp = "cspConditioned"
        val dnnfCspConditioned = cspConditioned.toDnnfSmooth()
        fullCsp.space.config.tmp = null
        println("fullDnnf.vars =            ${fullDnnf.vars}")
        val dnnfDnnfConditioned = fullDnnf.condition(con)
        println("dnnfDnnfConditioned.vars = ${dnnfDnnfConditioned.vars}")
        println("dFullVars-dCondVars = ${fullDnnf.vars.minus(dnnfDnnfConditioned.vars)}")
        println("dCondVars-dFullVars = ${dnnfDnnfConditioned.vars.minus(fullDnnf.vars)}")

        assert(dnnfCspConditioned.isSmooth)
        assert(dnnfDnnfConditioned.isSmooth)
        println("cspConditioned[$con].satCountPL: ${cspConditioned.satCountPL()}")
        println("dnnfCspConditioned[$con].satCount: ${dnnfCspConditioned.satCount}")
        println("dnnfDnnfConditioned[$con].satCount: ${dnnfDnnfConditioned.getSatCount(vars, cube.vars)}")

        val cubes1 = dnnfCspConditioned.cubes
        val cubes2 = dnnfDnnfConditioned.cubes

        println("cubes1 = ${cubes1.size}")
        println("cubes2 = ${cubes2.size}")


        println("cubes1 with QD: ${cubes1.count {
            it.isTrue("QD")
        }}")
        println("cubes1 with !QD: ${cubes1.count {
            it.isFalse("QD")
        }}")
        println("cubes2 with QD: ${cubes2.count {
            it.isTrue("QD")
        }}")
        println("cubes2 with !QD: ${cubes2.count {
            it.isFalse("QD")
        }}")
//
//
//        val c1 = cubes1.map { it.trueVars.toVarCodeSetSorted().reversed().toString() }.sorted()
//        val c2 = cubes2.map { it.trueVars.toVarCodeSetSorted().reversed().toString() }.sorted()

        fun op(acc: VarSet, cube: Cube): VarSet = VarSet.union(space, acc, cube.trueVars)

        val c1TrueVars: VarSet = cubes1.fold(space.mkEmptyVarSet(), ::op)
        val c2TrueVars: VarSet = cubes2.fold(space.mkEmptyVarSet(), ::op)
//        println("c1:")
//        for (cube in c1) {
//            println(cube)
//        }
//
//        println()
//        println()
//        println()

//        println("c2:")
//        for (cube in c2) {
//            println(cube)
//        }
//
//        println()
//        println()
//        println()

//        val c2MinusC1 = c2.minus(c1)
//        val c1MinusC2 = c1.minus(c2)
//        println("c2 minus c1: ${c2MinusC1.size}")
//        println("c1 minus c2: ${c1MinusC2.size}")
        val message12 = c1TrueVars.minus(c2TrueVars).toVarCodeSetSorted()
        val message21 = c2TrueVars.minus(c1TrueVars).toVarCodeSetSorted()
        println(message12)
        println(message21)
//        for (cube in c2MinusC1) {
//            println(cube)
//        }


    }


    private fun compareAllLitsOneCsp(sample: CspSample): Lit? {

        val clob = sample.loadText()
        println(sample.name)
        val efcCsp = Csp.parse(clob)
        val efcDnnf = efcCsp.toDnnfSmooth()

        var smallest = 1000000000L
        var smallestLit: Lit? = null
        for (vr in efcCsp.vars) {
            val c = compareOneCsp(sample, efcCsp, efcDnnf, vr.pLit());
            if (c < smallest) {
                smallest = c
                smallestLit = vr.pLit()
            }
        }
        return smallestLit
    }

    private fun compareAllPairsOneCsp(sample: CspSample) {

        val clob = sample.loadText()
        println(sample.name)
        val efcCsp = Csp.parse(clob)
        val efcDnnf = efcCsp.toDnnfSmooth()
        val pairs = efcCsp.vars.allPairs

        var i = 1
        for (pair in pairs) {
//            println("  Pair $i of ${pairs.size}: ${sample.name}.condition($pair)")
            val cube = DynCube(efcCsp.space, pair, pair)
            compareOneCsp(sample, efcCsp, efcDnnf, cube);
            i++
        }
    }

    private fun compareOneCsp(sample: CspSample, cspFull: Csp, dnnfFull: Exp, con: ConditionOn): Long {

        val space = cspFull.space
        val vars = cspFull.vars
        val picVars = space.mkVarSet(con)

        val cspConditioned = cspFull.condition(con)
        val dnnfCspConditioned = cspConditioned.toDnnfSmooth()
        val dnnfDnnfConditioned = dnnfFull.condition(con).smooth

        assert(dnnfFull.isSmooth)
        assert(dnnfCspConditioned.isSmooth)
        assert(dnnfDnnfConditioned.isSmooth)

        val topSatCount = dnnfFull.satCount.toLong()

        val cspSatCount = cspFull.satCountPL()
        val cspConditionedSatCount = cspConditioned.satCountPL()
        val cspConditionedDnnfSatCount = dnnfCspConditioned.getSatCount(vars, picVars).toLong()
        val dnnfConditionedDnnfSatCount = dnnfDnnfConditioned.getSatCount(vars, picVars).toLong()

        val ass1 = cspConditionedDnnfSatCount >= 0
        val ass2 = dnnfConditionedDnnfSatCount >= 0
        val ass3 = cspConditionedDnnfSatCount < topSatCount
        val ass4 = dnnfConditionedDnnfSatCount < topSatCount
        val ass5 = cspConditionedDnnfSatCount == dnnfConditionedDnnfSatCount


        val ass = ass1 && ass2 && ass3 && ass4 && ass5


        return if (!ass) {
            println("  Fail! ${sample.name} $con  satCounts:")
            println("    dnnf: $topSatCount")
            println("    csp: $cspSatCount")
            println("    csp conditioned: $cspConditionedSatCount")
            println("    dnnf (csp cond): $cspConditionedDnnfSatCount")
            println("    dnnf (dnnf cond): $dnnfConditionedDnnfSatCount")
            cspConditionedDnnfSatCount
        } else {
            10000000000L
//            println("Pass! lits: $lits")
//            println("  dnf/cspFull cond: $cspConditionedSatCount")
        }
    }
}