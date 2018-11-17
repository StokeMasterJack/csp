package com.tms.csp.misc

import com.tms.csp.ast.Csp
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Test
import java.util.*

class AnyIntersectionTest : CspBaseTest2() {


    @Test
    fun testUFWithCamry() {

        val csp = Csp.parse(CspSample.Camry2011Dc)

        val formula = csp.mkFormula().asFormula

        val vars = formula.vars
        System.err.println("formula _complexVars[" + vars.size + "]")
        System.err.println("space _complexVars  [" + csp.space.vars.size + "]")

        val cc = formula.constraintCount
        System.err.println("cc[$cc]")
        System.err.println()
        System.err.println()

        val r = Random()

        for (i in 0..9) {


            val i1 = r.nextInt(105)
            val i2 = r.nextInt(105)

            System.err.println("i1 i2[$i1 $i2]")

            val e1 = formula.get(i1)
            val e2 = formula.get(i2)

            System.err.println("e1[$e1]")
            System.err.println("e2[$e2]")

            val directlyRelated = formula.isDirectlyRelated(i1, i2)
            System.err.println("directlyRelated[$directlyRelated]")


            System.err.println()
        }


        //        UnionFind unionFind = formula.computeUnionFind();
        //
        //        int fccCount = unionFind.getFccCount();
        //        System.err.println("unionFind[" + unionFind + "]");
        //
        //        assertEquals(2, fccCount);
        //
        //        System.err.println("ss");

    }

    internal fun loadCamry(): Csp {
        return CspSample.Camry2011Dc.csp()
    }
}
