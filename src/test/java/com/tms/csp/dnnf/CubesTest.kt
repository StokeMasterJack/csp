package com.tms.csp.dnnf

import com.tms.csp.ast.Csp
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Space
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Assert.assertEquals
import org.junit.Test

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

        val rough = csp.toDnnf()
        System.err.println("rough.get_vars().size()[" + rough.vars.size + "]")
        val smooth = rough.smooth
        System.err.println("smooth.get_vars().size()[" + smooth.vars.size + "]")
        val t1 = System.currentTimeMillis()
        val cubes = smooth.cubesSmooth
        val t2 = System.currentTimeMillis()
        System.err.println("computeCubesSmooth[trimColorOptions]  Delta: " + (t2 - t1))

        assertEquals(22472, cubes.size.toLong())

        System.err.println("getSatCountSmooth[" + smooth.satCount + "]")
    }

    @Test
    fun camrySmooth() {

        val csp = Csp.parse(CspSample.Camry2011NoDc)
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

        val d3:Exp = Exp.parseTinyDnnf(tinyDnnf)



        assertEquals(expectedSatCount, d3.satCount)


    }


}