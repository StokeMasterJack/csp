package com.tms.csp.pl

import com.tms.csp.ast.Csp
import com.tms.csp.ast.Exp
import com.tms.csp.data.CspSample
import com.tms.csp.ssutil.millis
import junit.framework.Assert
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests:
 *      parse
 *      dnnf compile
 *      satCount
 *      cubes
 *      serialize tiny dnnf
 *      parse tiny dnnf
 */
class SatCountTest {


    @Test
    fun testSimple() {
        CspSample.allSimplePL.forEach {
            val name = it.name
            val clob = it.loadText()
            val csp = Csp.parse(clob)


            val satCountPL = csp.satCountPL().toBigInteger()
            val smooth1 = csp.toDnnfSmooth()
            val satCount1 = smooth1.satCount
            val cubeCount1 = smooth1.cubes.size.toBigInteger()

            assertEquals(it.expectedSatCount, satCountPL)
            assertEquals(it.expectedSatCount, satCount1)
            assertEquals(it.expectedSatCount, cubeCount1)

            val tinyDnnf = smooth1.gc().space.serializeTinyDnnf()

            val smooth2 = Exp.parseTinyDnnf(tinyDnnf)
            val satCount2 = smooth2.getSatCount(csp.vars)
            assertEquals(it.expectedSatCount, satCount2)

            println("$name satCount: $satCount1")

        }
    }


    /*
  load rules: 52
  parse rules: 820
  dnnf compile: 3770
  dnnf sat count: 22

  load rules: 52
  parse rules: 773
  dnnf compile: 3841
  dnnf sat count: 18
   */

    @Test
    fun testComplex() {
        CspSample.allComplexPL.forEach {


            val name = it.name
            println("Processing $name:")


            val t0 = millis

            val clob = it.loadText()
            val t1 = millis

            val csp = Csp.parse(clob)  //parse rules: 1446, 1410,1600
            val t2 = millis

            val rough1 = csp.toDnnf()   //11014
            val t3 = millis

            val smooth1 = rough1.smooth
            val t4 = millis

            val satCount1 = smooth1.satCount
            val t5 = millis

            val tinyDnnf = smooth1.gc().space.serializeTinyDnnf()
            val t6 = millis

            val smooth2 = Exp.parseTinyDnnf(tinyDnnf)
            val t7 = millis

            val satCount2 = smooth2.getSatCount(csp.vars)
            val t8 = millis

            println("  load rules:      " + (t1 - t0))
            println("  parse rules:     " + (t2 - t1))
            println("  compile dnnf:    " + (t3 - t2))
            println("  dnnf smooth:     " + (t4 - t3))
            println("  sat count:       " + (t5 - t4))
            println("  ser tiny dnnf:   " + (t6 - t5))
            println("  parse tiny dnnf: " + (t7 - t6))
            println("  sat count:       " + (t8 - t7))


            assertEquals(it.expectedSatCount, satCount1,name)
            assertEquals(it.expectedSatCount, satCount2,name)



        }
    }
}