package com.tms.csp.pl

import com.tms.csp.ast.Csp
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Space
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import kotlin.test.assertTrue

class VVConditionTest : CspBaseTest2() {

    @Test
    @Throws(Exception::class)
    fun testConditionOr() {
        val expText = "and(or(x y z) or(a b c) or(j k m))"


        val csp = Csp.parse("vars(x y z a b c m j k)")
        val space = csp.space

        System.err.println("csp[$csp]")

        val vvp = space.parseExp(expText)

        val vvpExpected: Exp
        val vvpActual: Exp

        vvpExpected = space.parseExp("and(or(x y) or(a b c) or(j k m))")
        vvpActual = vvp.conditionVV("or(x y)")
        assertEquals(vvpExpected, vvpActual)

        //        vvpExpected = space.parseExp("and(or(x y z) or(b c) or(m j k))");
        //        vvpActual = vvp.conditionVV("or(!a b)");
        //        assertEquals(vvpExpected, vvpActual);
        //
        //        vvpExpected = space.parseExp("and(or(x y z) or(a c) or(m j k))");
        //        vvpActual = vvp.conditionVV("or(a !b)");
        //        assertEquals(vvpExpected, vvpActual);
        //
        //        vvpExpected = space.parseExp("and(or(x y z) or(a b c) or(m j k))");
        //        vvpActual = vvp.conditionVV("or(!a !b)");
        //        assertEquals(vvpExpected, vvpActual);


        System.err.println("pass")

    }


    @Test
    fun testConditionAnd() {


        val space = Space.withVars("x y z a b c m j k")

        val vvp = space.parseExp("or(and(x y z) and(a b c) and(m j k))")

        var vvpExpected: Exp
        var vvpActual: Exp


        vvpExpected = space.parseExp("or(and(x z) and(a b c) and(m j k))")
        vvpActual = vvp.conditionVV("or(!x y)")

        assert(vvpExpected == vvpActual)

        vvpExpected = space.parseExp("or(and(y z) and(a b c) and(m j k))")
        vvpActual = vvp.conditionVV("or(x !y)")
        assertEquals(vvpExpected, vvpActual)

        vvpExpected = space.parseExp("or(and(a b c) and(m j k))")
        vvpActual = vvp.conditionVV("or(!x !y)")
        assertEquals(vvpExpected, vvpActual)

        vvpExpected = space.parseExp("or(and(x y z) and(a b c) and(m j k))")
        vvpActual = vvp.conditionVV("or(x y)")
        assertEquals(vvpExpected, vvpActual)


    }


    @Test
    fun testCondition() {


        val clob = """
            vars(a b c d e fCon j k l m n o p)
            or(and(a b c) and(d e fCon))
            or(!a b)
        """.trimIndent().trim()


        val csp = Csp.parse(clob)


        csp.simplifyBasedOnVvs()


        val aa = ArrayList(csp.getVVPlusConstraints())

        aa.sortWith(Exp.COMPARATOR_BY_VAR_COUNT)

        val vvpExpected0 = csp.space.parseExp("or(and(a c) and(d e fCon))")
        assertEquals(vvpExpected0, aa[0])

        csp.print()

    }

    @Test
    fun testCondition2() {

        val space = Space.withVars("vars(a b c d e fCon j k l m n o p)")

        val csp = space.getCsp()


        csp.addConstraint("or(and(a b c) and(d e fCon))")
        csp.addConstraint("or(!a b)")

        csp.propagate()


    }

    /*

        56s
        42s
        42s
        5s
        57s
        */
    @Test
    fun test_simplifyBasedOnVvs() {
        val csp = Csp.parse(CspSample.Efc)

        csp.toNnf()
        csp.simplifyBasedOnVvs()
        assertTrue(csp.isSat())
    }


}
