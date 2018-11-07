package com.tms.csp.dnnf

import com.tms.csp.ast.Csp
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Parser
import com.tms.csp.ast.Space
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Assert.assertEquals
import kotlin.test.Test

class CubesTest : CspBaseTest2() {

    @Test
    fun tinySmooth() {

        val csp = Csp.parse(CspSample.TinyNoDc)

        val smooth = csp.toDnnf().smooth

        assertEquals(3, smooth.computeCubesSmooth().size.toLong())

    }

    @Test
    fun trimSmooth() {

        val csp = Csp.parse(CspSample.Trim)
        val smooth = csp.toDnnf().smooth

        val cubes = smooth.computeCubesSmooth()

        assertEquals(11, cubes.size.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun trimColorSmooth() {
        val csp = Csp.parse(CspSample.TrimColor)
        val smooth = csp.toDnnf().smooth

        val t1 = System.currentTimeMillis()
        val cubes = smooth.computeCubesSmooth()
        assertEquals(227, cubes.size.toLong())
        val t2 = System.currentTimeMillis()
        System.err.println("computeCubesSmooth[trimColor] Delta: " + (t2 - t1))    //399ms
    }

    //1m 51s
    @Test
    fun trimColorOptionsSmooth() {
        val csp = Csp.parse(CspSample.TrimColorOptions)
        println(csp.dontCares)

        csp.flag = true
        val rough = csp.toDnnf()
        System.err.println("rough.get_vars().size()[" + rough.vars.size + "]")
        val smooth = rough.smooth
        System.err.println("smooth.get_vars().size()[" + smooth.vars.size + "]")
        val t1 = System.currentTimeMillis()
        val cubes = smooth.cubesSmooth
        val t2 = System.currentTimeMillis()
        System.err.println("computeCubesSmooth[trimColorOptions]  Delta: " + (t2 - t1))


        val satCountPL = csp.satCountPL()

        println("satCountPL = ${satCountPL}")
        val sat = csp.isSat()
        println("sat = ${sat}")
        assertEquals(44944, cubes.size.toLong())
        assertEquals(44944, smooth.satCount)
        assertEquals(44944, satCountPL)
        //44944

        System.err.println("getSatCountSmooth[" + smooth.satCount + "]")
    }

    @Test
    fun trimColorOptionsSmoothTmp() {
        val clob1 = CspSample.TrimColorOptions.loadText()
val clob = """
vars(AW LE QA BTButton EJ Ash 13 Fabric Base FB13 RearAC SE Antenna XLE Hyb V6 6AT 2550 EX NV L4 2514 LeatherWheel 2Q CF 14 Chrome Charcoal 15 Leather LB15 LF R7 Hybrid ECVT 2560 SK 6MT 8U8 2531 Camera SR Bisque 40 LA40 Buttons LB14 HD QF 3R3 1G3 LA13 776 EC 6V4 2532 2513 2552 2540 2554 2545 2546 StartButton UP UT QE Wood ACButton FB40 FA13 FA40 FC14 FC15 4T8 QC 28 CQ LA QD 040 1F7 202 8T5 E5 QB WB 32)
iff(QA and(AW LE))
imp(EJ BTButton)
imp(13 Ash)
iff(FB13 and(Ash 13 Fabric or(Base LE)))
nand(RearAC or(Base LE SE))
imp(EJ Antenna)
xor(Base LE SE XLE Hyb)
iff(2550 and(SE V6 6AT))
nand(EJ or(EX NV))
iff(2514 and(Base L4 6AT))
imp(LeatherWheel Hyb)
imp(Ash or(13 14))
nand(Chrome or(Base LE SE))
imp(14 Ash)
iff(LB15 and(Charcoal 15 Leather SE))
imp(SE LF)
iff(2560 and(Hyb Hybrid ECVT))
nand(SK or(Base LE 6MT))
nand(XLE 8U8)
iff(2531 and(LE L4 6MT))
imp(NV and(Camera Antenna BTButton))
imp(XLE SR)
iff(LA40 and(Bisque 40 Leather or(Hyb XLE)))
nand(EX or(EJ NV))
imp(Buttons SK)
iff(LB14 and(Ash 14 Leather SE))
xor(L4 V6 Hybrid)
imp(Antenna or(NV EJ EX))
iff(QF and(HD SK XLE))
nand(Base 3R3)
nand(Base 1G3)
nand(Base 8U8)
iff(LA13 and(Ash 13 Leather or(Hyb XLE)))
nand(Base 776)
nand(EC Base)
nand(Base 6V4)
iff(2532 and(LE L4 6AT))
xor(2513 2514 2531 2532 2552 2540 2554 2545 2546 2550 2560)
xor(6MT 6AT ECVT)
nand(BTButton or(Base LE))
imp(StartButton SK)
imp(LeatherWheel or(UP UT))
nand(Base SR)
nand(HD LE)
nand(HD Base)
nand(NV or(EJ EX))
iff(2552 and(LE V6 6AT))
nand(LE 8U8)
nand(LF or(Base LE))
nand(QE or(Base LE Hyb))
iff(15 Charcoal)
iff(2513 and(Base L4 6MT))
imp(XLE and(Chrome RearAC Wood ACButton))
imp(QE NV)
xor(FB13 FB40 FA13 FA40 FC14 FC15 LA13 LA40 LB14 LB15)
iff(2540 and(XLE L4 6AT))
nand(SE or(4T8 6V4 776 8U8))
imp(Camera NV)
imp(UP and(EC EJ LeatherWheel Hyb))
nand(Camera or(Base LE))
xor(Fabric Leather)
imp(UT and(NV EC LeatherWheel Hyb))
iff(SK and(Buttons StartButton))
imp(QC SE)
iff(FC15 and(Charcoal 15 Fabric SE))
iff(40 Bisque)
imp(CQ and(HD Hyb))
imp(ACButton or(XLE Hyb))
imp(Wood or(XLE LA))
iff(2554 and(XLE V6 6AT))
xor(13 14 15 40)
imp(Hyb and(Chrome RearAC ACButton SK))
imp(BTButton or(NV EJ EX))
nand(Ash 4T8)
nand(Ash 776)
iff(FA40 and(Bisque 40 Fabric or(Hyb XLE)))
iff(FC14 and(Ash 14 Fabric SE))
nand(Wood or(Base LE))
xor(040 1F7 1G3 202 3R3 4T8 6V4 776 8T5 8U8)
iff(2545 and(SE L4 6MT))
iff(FA13 and(Ash 13 Fabric or(Hyb XLE)))
nand(NV or(Base LE))
imp(LA Leather)
imp(XLE EC)
imp(QC and(HD LA))
imp(EX Antenna)
imp(HD or(CQ QC QF))
imp(EX BTButton)
iff(2546 and(SE L4 6AT))
nand(Bisque 1F7)
iff(QB and(EC SE 6AT))
nand(XLE EX)
iff(FB40 and(Bisque 40 Fabric or(Base LE)))
nand(LA or(Base LE))
iff(32 SE)
nand(LA or(and(Ash 4T8) and(Ash 776) and(Bisque 1F7)))
xor(Ash Charcoal Bisque)
imp(LA Wood)
xor(2Q CF)
or(!R7 SE)
or(!28 !6MT)
or(!2540 E5)
or(!UT WB)
or(!UP WB)
or(!QD and(R7 SE SR))
""".trimIndent().trim()




        val csp = Csp.parse(clob)
//        csp.toNnf()

//        val space = Space.withVars("AW LE QA BTButton EJ Ash 13 Fabric Base FB13 RearAC SE Antenna XLE Hyb V6 6AT 2550 EX NV L4 2514 LeatherWheel 2Q CF 14 Chrome Charcoal 15 Leather LB15 LF R7 Hybrid ECVT 2560 SK 6MT 8U8 2531 Camera SR Bisque 40 LA40 Buttons LB14 HD QF 3R3 1G3 LA13 776 EC 6V4 2532 2513 2552 2540 2554 2545 2546 StartButton UP UT QE Wood ACButton FB40 FA13 FA40 FC14 FC15 4T8 QC 28 CQ LA QD 040 1F7 202 8T5 E5 QB WB 32")
//        val lines = Parser.parseLines(clob)
//        val constraints = space.parser.parseLines(lines)
//
//        constraints.forEach {
//            println(it)
//        }


        val rough = csp.toDnnf()
        System.err.println("rough.get_vars().size()[" + rough.vars.size + "]")
        val smooth = rough.smooth
        System.err.println("smooth.get_vars().size()[" + smooth.vars.size + "]")
        val t1 = System.currentTimeMillis()
        val cubes = smooth.cubesSmooth
        val t2 = System.currentTimeMillis()
        System.err.println("computeCubesSmooth[trimColorOptions]  Delta: " + (t2 - t1))


        val satCountPL = csp.satCountPL()


        val sat = csp.isSat()
        println("sat = ${sat}")

        println("satCountPL:     ${satCountPL}")
        println("satCountSmooth  ${smooth.satCount}")
        println("expected        520128")


        //22472
        //44128

        // cam 520128


    }

    @kotlin.test.Test
    fun test() {
        val csp1 = Csp.parse(CspSample.Camry2011NoDc)
        val csp2 = Csp.parse(CspSample.TrimColorOptions)

        val ser1: Sequence<String> = csp1.complexConstraintsSer
        val ser2: Sequence<String> = csp2.complexConstraintsSer

        val set1 = ser1.toSet()
        val set2 = ser2.toSet()

        set1.minus(set2).joinToString(separator = "\n").apply { println(this) }
    }

    @Test
    fun camrySmooth() {

        val csp = Csp.parse(CspSample.Camry2011NoDc)

        val satCountPL = csp.satCountPL()
        println("satCountPL = ${satCountPL}")


        val rough = csp.toDnnf()

        assert(!rough.isSmooth)
        val smooth = rough.smooth
        assert(smooth.isSmooth)

        assertEquals(520128, smooth.satCount)

        System.err.println("Camry computeCubesSmooth start...")
        val t1 = System.currentTimeMillis()
        val cubes = smooth.cubesSmooth
        val t2 = System.currentTimeMillis()
        System.err.println("Camry computeCubesSmooth Delta: " + (t2 - t1))   //6511ms


        assertEquals(520128, cubes.size.toLong())

    }

    @Test
    @Throws(Exception::class)
    fun efcSatCount() {

        val expectedSatCount = 3460501125462739908

        val csp1 = Csp.parse(CspSample.EfcOriginal)


        val sp1 = csp1.space

        val d1 = csp1.toDnnf().smooth

        assertEquals(expectedSatCount, d1.satCount)

        val d2 = d1.gc()
        val sp2 = d2.sp()
        assert(sp2 != sp1)

        assertEquals(expectedSatCount, d2.satCount)

        val tinyDnnf = sp2.serializeTinyDnnf()

        val d3: Exp = Exp.parseTinyDnnf(tinyDnnf)



        assertEquals(expectedSatCount, d3.satCount)


    }


}