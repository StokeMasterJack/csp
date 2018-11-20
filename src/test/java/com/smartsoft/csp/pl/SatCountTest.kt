package com.smartsoft.csp.pl

import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.formula.KFormula
import com.smartsoft.csp.data.CspSample
import com.smartsoft.csp.ssutil.Console.prindent
import com.smartsoft.csp.ssutil.Strings
import com.smartsoft.csp.ssutil.millis
import com.smartsoft.csp.ssutil.tt
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

    //Csp: 19.5s
    //Csp1: 18s
    //17.666s
    //16.1s
    @Test
    fun testSimple() {
        CspSample.allSimplePL.forEach {
            val name = it.name
            println("Processing $name")
            val clob = it.loadText()
            val csp = Csp.parse(clob)

            val satCountPL = csp.satCountPL().toBigInteger()

            assertEquals(it.expectedSatCount, satCountPL)


            val rough1 = csp.toDnnf()
            val smooth1 = rough1.smooth
            val satCount1 = smooth1.satCount
            val cubeCount1 = smooth1.cubes.size.toBigInteger()

            assertEquals(it.expectedSatCount, satCount1)
            assertEquals(it.expectedSatCount, cubeCount1)

            val tinyDnnf = smooth1.gc().space.serializeTinyDnnf()
            val smooth2 = Exp.parseTinyDnnf(tinyDnnf)
            val satCount2 = smooth2.getSatCount(csp.vars)

            assertEquals(it.expectedSatCount, satCount2)

            println("   $name satCount: $satCount1")


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

    /*
Processing EfcOriginal:
  load rules:      94
  parse rules:     1231
  compile dnnf:    3096
  dnnf smooth:     84
  sat count:       15
  ser tiny dnnf:   878
  parse tiny dnnf: 76
  sat count:       9
Processing EfcProdFactoryRules:
  load rules:      8
  parse rules:     245
  compile dnnf:    1117
  dnnf smooth:     27
  sat count:       2
  ser tiny dnnf:   180
  parse tiny dnnf: 47
  sat count:       8
Processing Tundra:
  load rules:      1
  parse rules:     3
  compile dnnf:    338
  dnnf smooth:     8
  sat count:       1
  ser tiny dnnf:   301
  parse tiny dnnf: 6
  sat count:       1
     */

    // 7.8s
    //11
    //10.8s
    //11.2s
    //9.2
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


            assertEquals(it.expectedSatCount, satCount1, name)
            assertEquals(it.expectedSatCount, satCount2, name)


        }
    }

    //9s
    //8.8
    //49 wo toDnnfTopXorSplit before fcc
    //8.7 s
    //8.7
    //7.7s
    //7.55s

    @Test
    fun testAllCompileAndSatCount() {
        repeat(1) {
            CspSample.allPL.forEach {
                val name = it.name
                System.err.println("Processing $name")
                val clob = tt("  load rules") { it.loadText() }
                val csp = tt("  parse rules") { Csp.parse(clob) }
                val rough = tt("  compile dnnf") { csp.toDnnf() }
                val smooth = tt("  smooth dnnf") { rough.smooth }
                val satCount = tt("  sat count") { smooth.satCount }

                assertEquals(it.expectedSatCount, satCount)

                smooth.printNodeInfo()
                println("  Node count: ${csp.space._nodes.size}")
            }
        }

        println("computeLitImps KFormula.globalDelta = ${KFormula.globalDelta}")
    }


    @Test
    fun testCompileAndSatCountEfc() {
        repeat(1) {
            CspSample.EfcOriginal.let {
                val name = it.name
                prindent(0, "Processing $name")
                val clob = tt(Strings.indent(1) + "  load rules") { it.loadText() }
                val csp = tt(Strings.indent(1) + "  parse rules") { Csp.parse(clob) }

//                val satCountPL = csp.satCountPL().toBigInteger()
//
//                assertEquals(it.expectedSatCount, satCountPL)

                val rough = tt(Strings.indent(1) + "  compile dnnf") { csp.toDnnf() }
                val smooth = tt(Strings.indent(1) + "  smooth dnnf") { rough.smooth }
                val satCount = tt(Strings.indent(1) + "  sat count") { smooth.satCount }
                assertEquals(it.expectedSatCount, satCount)

                println("satCount = ${satCount}")
                smooth.printNodeInfo(1)
            }
        }
    }

}

