package com.smartsoft.csp.minModels

import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.data.CspSample
import com.smartsoft.csp.ssutil.Path
import com.smartsoft.csp.ssutil.TT
import com.smartsoft.csp.util.SpaceJvm.loadResource
import org.junit.Assert.assertEquals
import org.junit.Test

class MinFCardTest {

    internal var p = Path("csp/minModels/stageisfullrules.txt")

    @Test
    fun test1A() {
        val n = Csp.parse("a").toDnnf()

        assertEquals(1, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(0, n.minFCard())


        val mm = n.minFModels()
        println("mm[$mm]")
        assertEquals(1, mm.products.size)

        val onlyCube = mm.products.iterator().next()
        assertEquals(0, onlyCube.falseVarCount)

    }

    @Test
    fun test1B() {
        val n = Csp.parse("!a").toDnnf()

        assertEquals(1, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(1, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(1, mm.products.size.toLong())

        val onlyCube = mm.products.iterator().next()
        assertEquals(1, onlyCube.falseVarCount.toLong())

    }

    @Test
    fun test2A() {
        val n = Csp.parse("and(a b)").toDnnf()

        assertEquals(2, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(0, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(1, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(0, cube.falseVarCount)
        }
    }

    @Test
    fun test2B() {
        val n = Csp.parse("and(!a !b)").toDnnf()

        assertEquals(2, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(2, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(1, mm.products.size)


        val products = mm.products
        for (cube in products) {
            assertEquals(2, cube.falseVarCount)
        }

    }

    @Test
    fun test2C() {
        val n = Csp.parse("and(a !b)").toDnnf()

        assertEquals(2, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size.toLong())
        assertEquals(1, n.minFCard().toLong())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(1, mm.products.size.toLong())

        val products = mm.products
        for (cube in products) {
            assertEquals(1, cube.falseVarCount.toLong())
        }

    }

    @Test
    fun test2D() {
        val n = Csp.parse("and(!a b)").toDnnf()

        assertEquals(2, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(1, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(1, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(1, cube.falseVarCount)
        }

    }

    /*
        csp:        and(a b c)
        varCount:   3
        satCount:   1
        minCard:    3
        products:   +a +b +c
        minModels:  +a +b +c
    */
    @Test
    fun test3a() {
        val csp = Csp.parse("and(a b c)")
        val n = csp.toDnnf()

        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(3, n.varCount)
        assertEquals(0, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(1, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(0, cube.falseVarCount)
        }

    }

    @Test
    fun test3b() {
        val csp = Csp.parse("and(!a b c)")
        val n = csp.toDnnf()

        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(3, n.varCount)
        assertEquals(1, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(1, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(1, cube.falseVarCount)
        }

    }

    @Test
    fun test3c() {
        val csp = Csp.parse("and(!a !b c)")
        val n = csp.toDnnf()

        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(3, n.varCount)
        assertEquals(2, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(1, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(2, cube.falseVarCount)
        }
    }

    /*
        csp:       xor(a b)
        varCount:  2
        satCount:  2
        minNCard:   1
        products:  +a -b
                   -a +b
        minFModels: +a -b
                    -a +b
    */
    @Test
    fun testXor2() {
        val csp = Csp.parse("xor(a b)")
        val n = csp.toDnnf()

        assertEquals(2, n.satCountLong)
        assertEquals(2, n.products.size)
        assertEquals(2, n.varCount)


        assertEquals(1, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(2, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(1, cube.falseVarCount)
        }

    }

    /*
       csp:       xor(a b)
       varCount:  3
       satCount:  3
       minNCard:  2
       products:  +a -b -c
                  -a +b -c
                  -a -b +c
       minFModels: +a -b -c
                   -a +b -c
                   -a -b +c
   */
    @Test
    fun testXor3() {
        val csp = Csp.parse("xor(a b c)")
        val n = csp.toDnnf()

        assertEquals(3, n.satCountLong)
        assertEquals(3, n.products.size)
        assertEquals(3, n.varCount)


        assertEquals(2, n.minFCard())

        val mm = n.minFModels()
        println("mm[$mm]")

        assertEquals(3, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(2, cube.falseVarCount)
        }

    }


    /*


        csp:       xor(a b)
                   conflict(a c)
        varCount:  3
        satCount:  3
        minFCard:  1
        products:  +a -b -c
                   -a +b -c
                   -a +b +c

        minFModels: -a +b +c

     */
    @Test
    fun test3() {
        val csp = Csp.parse("xor(a b)\nconflict(a c)")
        val smooth = csp.toDnnfSmooth()

        val cubes = smooth.cubesSmooth

        println("products: ")
        Exp.printCubes(cubes)


        assertEquals(3, smooth.varCount)
        assertEquals(3, smooth.satCountLong)
        assertEquals(3, smooth.products.size)


        assertEquals(1, smooth.minFCard())

        val minFModels = smooth.minFModels()
        println("minFModels[$minFModels]")


        val products = minFModels.products
        println("minFModelsCubes: ")
        for (p in products) {
            println("  $p")
            assertEquals(1, p.falseVarCount)
        }

        assertEquals(1, products.size)


    }

    @Test
    fun testTiny() {

        val csp = Csp.parse(CspSample.TinyDc)
        val n = csp.toDnnf().smooth

        assertEquals(6, n.products.size)

        val products = n.products
        for (p in products) {

            println(p)
        }

        assertEquals(1, n.minFCard())
        assertEquals(1, n.minFModels().products.size)
    }

    @Test
    fun testTrim() {

        val csp = Csp.parse(CspSample.TrimNoDc)
        val n = csp.toDnnf().smooth


        assertEquals(4, n.minTCard())
        assertEquals(18, n.minFCard())

        val minT = n.minTModels()
        val minF = n.minFModels()

        for (cube in minT.cubesSmooth) {
            assertEquals(4, cube.trueVarCount)
        }

        for (cube in minF.cubesSmooth) {
            assertEquals(18, cube.falseVarCount)
        }

        //        Set<Cube> products = n.getProducts();
        //        for (Cube p : products) {
        //            VarSet falseVars = p.get_complexVars().minus(p.getTrueVars());
        //            println(falseVars.size());
        //        }
    }

    @Test
    fun testCamry() {

        val csp = Csp.parse(CspSample.Camry2011Dc)
        val n = csp.toDnnf().smooth

        val minCardT1 = System.currentTimeMillis()
        val minCard = n.minCard()
        val minCardT2 = System.currentTimeMillis()
        println("minCard[$minCard]")
        println("minCard Delta: " + (minCardT2 - minCardT1))

        val products = n.products
        println("products.size()[" + products.size + "]")

        var cc = 0
        //        println("minModels hard-way:");
        for (p in products) {
            val trueVarCount = p.trueVarCount
            if (trueVarCount == 10) {
                //                println("  " + p.getTrueVars());
                cc++
            }
            if (trueVarCount < 10) {
                throw IllegalStateException()
            }
        }
        println("cc[$cc]")

        val minModelsT1 = System.currentTimeMillis()
        val minModels = n.minModels()
        val minModelsT2 = System.currentTimeMillis()
        println("minModels[$minModels]")
        println("minModels.getProducts().size(): [" + minModels.products.size + "]")
        println("minModels Delta: " + (minModelsT2 - minModelsT1))

        println("minModels easy-way:")
        val mmProducts = minModels.products


        for (mmProduct in mmProducts) {
            val tVars = mmProduct.trueVars
            val card = tVars.size
            println("  $card : $tVars")
        }


    }

    @Test
    fun test_IS_FullRules() {

        val tt = TT()


        //        Path p = new Path("csp/minModels/stageisfullrules.txt");
        //        String clob = loadResource(p);

        val clob = loadResource(this, "stageisfullrules.txt")

        tt.t("load")
        val csp = Csp.parse(clob)
        tt.t("parse")
        val n = csp.toDnnf().smooth
        tt.t("compile")

        println("csp.vars.size = ${csp.vars.size}")
        assertEquals(80, n.varCount)
        tt.t("getVarCount")
        assertEquals(50281185280L, n.satCountLong)
        tt.t("getSatCount")

        assertEquals(11, n.minTCard())
        tt.t("minTCard")

        assertEquals(39, n.minFCard())
        tt.t("minFCard")

        val mmt = n.minTModels()
        tt.t("minTModels")
        val mmf = n.minFModels()
        tt.t("minFModels")

        println("mmt = ${mmt}")
        println("mmf = ${mmf}")

    }

    @Test
    fun test_IS_FullRules_FixList() {

        val clob = loadResource(this, "stageisfullrules.txt")
        val csp = Csp.parse(clob)
        var n = csp.toDnnf()


        n = n.reduce()

        //        VarSet _complexVars = n.get_complexVars();
        //        for (Var vr : _complexVars) {
        //            println(vr);
        //        }

        val eb10_0212 = n.con("ICOL_EB10", "XCOL_0212")
        println("eb10_0212.isSat()[" + eb10_0212.isSat + "]")


        val eb10_0212_3M = eb10_0212.con("ICOL_EB10", "XCOL_0212", "ACY_3M")
        println("eb10_0212_3M.isSat()[" + eb10_0212_3M.isSat + "]")


        val eb10_3M = n.con("ICOL_EB10", "ACY_3M")
        println("eb10_3M.isSat()[" + eb10_3M.isSat + "]")


        val c0212_3M = n.con("XCOL_0212", "ACY_3M")
        println("0212_3M.isSat()[" + c0212_3M.isSat + "]")

        val hard = n.con("ACY_3M")
        val soft = hard.project("ICOL_EB10", "XCOL_0212")


        println("Cubes:")
        for (cube in soft.cubesSmooth) {
            println(" $cube")
        }


        val tt = TT()
        val minFModels = soft.minFModels()
        tt.t("minFModels")


        println("minFModels:")
        for (cube in minFModels.cubesSmooth) {
            println(" $cube")
        }


    }


    @Test
    fun test_Tundra_FixList() {

        val clob = loadResource(this, "toyota-tundra.dnnf.txt")

        var n = Exp.parseTinyDnnf(clob)


        n = n.reduce()

        val vars = n.vars
        for (vr in vars) {
            println(vr)
        }

        val c_FA13_08T5_TRAN_6AT = n.con("ICOL_FA13", "XCOL_08T5", "TRAN_6AT")
        println("FA13_08T5_TRAN_6AT.isSat()[" + c_FA13_08T5_TRAN_6AT.isSat + "]")

        val c_FA13_08T5_TRAN_6AT_crewmax = n.con("ICOL_FA13", "XCOL_08T5", "TRAN_6AT", "CAB_crewmax")
        println("FA13_08T5_TRAN_6AT_crewmax.isSat()[" + c_FA13_08T5_TRAN_6AT_crewmax.isSat + "]")

        val tt = TT()

        val hard = n.con("CAB_crewmax")
        val soft = hard.project("ICOL_FA13", "XCOL_08T5", "TRAN_6AT")
        val minFModels = soft.minFModels()

        tt.t("FixList")


        println("Fix List:")
        for (cube in minFModels.cubesSmooth) {
            println("  $cube")
        }
    }


}
