package com.smartsoft.csp.minModels

import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.data.CspSample
import com.smartsoft.csp.ssutil.TT
import com.smartsoft.csp.util.CspBaseTest2
import org.junit.Test

import org.junit.Assert.assertEquals

class MinTCardTest : CspBaseTest2() {

//    internal var p = Path("csp/minModels/stageisfullrules.txt")

    @Test
    fun test1A() {
        val n = Csp.parse("a").toDnnf().smooth

        assertEquals(1, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(1, n.minCard())


        val mm = n.minModels()
        assertEquals(1, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(1, cube.trueVarCount.toLong())
        }

    }

    @Test
    fun test1B() {
        val n = Csp.parse("!a").toDnnf()

        assertEquals(1, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(0, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(1, mm.products.size)
        val products = mm.products
        for (cube in products) {
            assertEquals(0, cube.trueVarCount.toLong())
        }

    }

    @Test
    fun test2A() {
        val n = Csp.parse("and(a b)").toDnnf()

        assertEquals(2, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(2, n.minTCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(1, mm.products.size)


        val products = mm.products
        for (cube in products) {
            assertEquals(2, cube.trueVarCount)
        }

    }

    @Test
    fun test2B() {
        val n = Csp.parse("and(!a !b)").toDnnf()

        assertEquals(2, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(0, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(1, mm.products.size)
        val products = mm.products
        for (cube in products) {
            assertEquals(0, cube.trueVarCount)
        }


    }

    @Test
    fun test2C() {
        val n = Csp.parse("and(a !b)").toDnnf()

        assertEquals(2, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(1, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(1, mm.products.size)

    }

    @Test
    fun test2D() {
        val n = Csp.parse("and(!a b)").toDnnf()

        assertEquals(2, n.varCount)
        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(1, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(1, mm.products.size)

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
        assertEquals(3, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(1, mm.products.size)

    }

    @Test
    fun test3b() {
        val csp = Csp.parse("and(!a b c)")
        val n = csp.toDnnf()

        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(3, n.varCount)
        assertEquals(2, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(1, mm.products.size)

    }

    @Test
    fun test3c() {
        val csp = Csp.parse("and(!a !b c)")
        val n = csp.toDnnf()

        assertEquals(1, n.satCountLong)
        assertEquals(1, n.products.size)
        assertEquals(3, n.varCount)
        assertEquals(1, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(1, mm.products.size)

    }

    /*
        csp:       xor(a b)
        varCount:  2
        satCount:  2
        minCard:   1
        products:  +a -b
                   -a +b
        minModels: +a -b
                   -a +b
    */
    @Test
    fun testXor2() {
        val csp = Csp.parse("xor(a b)")
        val n = csp.toDnnf()

        assertEquals(2, n.satCountLong)
        assertEquals(2, n.products.size)
        assertEquals(2, n.varCount)
        assertEquals(1, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(2, mm.products.size)


        val products = mm.products
        for (cube in products) {
            assertEquals(1, cube.trueVarCount.toLong())
        }

    }

    /*
       csp:       xor(a b c)
       varCount:  3
       satCount:  3
       minCard:   1
       products:  +a -b -c
                  -a +b -c
                  -a -b +c
       minModels: +a -b -c
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
        assertEquals(1, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        assertEquals(3, mm.products.size)

        val products = mm.products
        for (cube in products) {
            assertEquals(1, cube.trueVarCount.toLong())
        }


    }


    /*


        csp:       xor(a b)
                   conflict(a c)
        varCount:  3
        satCount:  3
        minCard:   1
        products:  +a -b -c
                   -a +b -c
                   -a +b +c

        minModels: +a -b -c
                   -a +b -c
     */
    @Test
    fun test3() {
        val csp = Csp.parse("xor(a b)\nconflict(a c)")
        csp.print()

        //        if(true) return;
        val n = csp.toDnnf()

        //        Set<Cube> products = n.getCubesRough();
        val cubes = n.smooth.cubesSmooth
        System.err.println("products: ")
        for (p in cubes) {
            System.err.println("  $p")
        }

        assertEquals(3, n.varCount)
        assertEquals(3, n.smooth.satCountLong)
        assertEquals(3, n.smooth.cubesSmooth.size)
        assertEquals(1, n.minCard())

        val mm = n.minModels()
        System.err.println("mm[$mm]")

        val mmProducts = mm.products
        System.err.println("minModels: ")
        for (p in mmProducts) {
            System.err.println("  $p")
        }

        assertEquals(2, n.minModels().products.size)

    }

    @Test
    fun testTiny() {

        val csp = loadCsp(CspSample.TinyDc, true)
        val n = csp.toDnnf()

        //        System.err.println(n.toXml());

        val products = n.products
        for (p in products) {
            System.err.println(p.trueVars)
        }

        assertEquals(1, n.minCard())
        assertEquals(2, n.minModels().products.size.toLong())
    }

    @Test
    fun testTrim() {

        val csp = loadCsp(CspSample.TrimNoDc)
        val n = csp.toDnnf().smooth

        System.err.println(n.minCard())

        val products = n.products
        for (p in products) {
            System.err.println(p.trueVars)
        }
    }

    @Test
    fun testCamry() {

        val csp = loadCsp(CspSample.Camry2011Dc, true)
        val n = csp.toDnnf().smooth

        val minCardT1 = System.currentTimeMillis()
        val minCard = n.minCard()
        val minCardT2 = System.currentTimeMillis()
        System.err.println("minCard[$minCard]")
        System.err.println("minCard Delta: " + (minCardT2 - minCardT1))

        val products = n.products
        System.err.println("products.size()[" + products.size + "]")

        var cc = 0
        //        System.err.println("minModels hard-way:");
        for (p in products) {
            val trueVarCount = p.trueVarCount
            if (trueVarCount == 10) {
                //                System.err.println("  " + p.getTrueVars());
                cc++
            }
            if (trueVarCount < 10) {
                throw IllegalStateException()
            }
        }
        System.err.println("cc[$cc]")

        val minModelsT1 = System.currentTimeMillis()
        val minModels = n.minModels()
        val minModelsT2 = System.currentTimeMillis()
        System.err.println("minModels[$minModels]")
        System.err.println("minModels.getProducts().size(): [" + minModels.products.size + "]")
        System.err.println("minModels Delta: " + (minModelsT2 - minModelsT1))

        System.err.println("minModels easy-way:")
        val mmProducts = minModels.products


        for (mmProduct in mmProducts) {
            val tVars = mmProduct.trueVars
            val card = tVars.size
            System.err.println("  $card : $tVars")
        }


    }

    @Test
    fun test5() {

        val tt = TT()

        //        String clob1 = loadResource(p);


        val clob2 = loadResource(this, "stageisfullrules.txt")
        tt.t("load")

        val csp = Csp.parse(clob2)
        tt.t("parse")

        val n = csp.toDnnf()
        tt.t("compile")

        assertEquals(80, n.varCount)
        tt.t("getVarCount")

        assertEquals(50281185280L, n.smooth.satCountLong)
        tt.t("getSatCount")

        assertEquals(11, n.minCard())
        tt.t("minCard")

        val mm = n.minModels()
        tt.t("minCard")

        System.err.println("mm[$mm]")

    }

}
