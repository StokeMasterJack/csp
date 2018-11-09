package com.tms.csp.dnnf

import com.tms.csp.ast.Csp
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Assert.*
import org.junit.Test

class DnnfCondition : CspBaseTest2() {

    @Test
    @Throws(Exception::class)
    fun testLit() {
        val n = Csp.compileDnnf("b")

        assertTrue(n.con("b").isConstantTrue)
        assertTrue(n.con("!b").isConstantFalse)

        assertEquals(1, n.satCount)

        val bt = n.con("b")
        val bf = n.con("!b")

        assertEquals(1, bt.satCount)
        assertEquals(0, bf.satCount)
    }


    @Test
    @Throws(Exception::class)
    fun testTrim() {
        //        Exp nn = loadDnnfTrim();
        val nn = Csp.compileDnnf(CspSample.Trim)
        assertEquals(11, nn.satCount)
    }

    @Test
    @Throws(Exception::class)
    fun testGetVars() {

        val b = Csp.compileDnnf("b")
        assert(b.isPosLit)
        assertEquals(1, b.varCount.toLong())


        val t = b.con("b")
        assertTrue(t.isConstantTrue)
        assertEquals(0, t.varCount.toLong())


        val f = b.con("!b")
        assertTrue(f.isConstantFalse)
        assertEquals(0, f.varCount.toLong())

    }


    /**
     * Note that the satCounts differ between camryRough and camrySmooth.
     * This is due to to don'tCon cares.
     *
     *
     * smoothSatCount = roughSatCount * 2 ^ (smoothVarCount - roughVarCount)
     */
    @Test
    @Throws(Exception::class)
    fun camrySmooth() {
        val csp = CspSample.Camry2011NoDc.csp()

        var n = csp.toDnnf().smooth
        assertTrue(n.isSat)

        assertEquals(520128, n.satCount)

        n = n.con("LE")
        assert(n.isSmooth)

        assertTrue(n.isSat)
        assertEquals(4800, n.satCount)     //4800

        n = n.con("V6")
        assert(n.isSmooth)

        assertTrue(n.isSat)
        assertEquals(1920, n.satCount)

        n = n.con("Ash")
        assert(n.isSmooth)

        assertTrue(n.isSat)
        assertEquals(896, n.satCount)

        n = n.con("776").smooth
        assert(n.isSmooth)

        assertFalse(n.isSat)
        assertEquals(0, n.satCount)


    }


    @Test
    fun testConditionSatCount() {
        assertConditionSatCount("YR_2013")
//        assertConditionSatCount("SER_tundra")
        assertConditionSatCount("YR_2013 SER_tundra")
    }

    fun assertConditionSatCount(sLits: String) {

        val clob = CspSample.EfcOriginal.loadText()
//        val clob = CspSample.Efc.loadText()
        val efcCsp = Csp.parse(clob)
        efcCsp.simplifyAlwaysTrueVars()

        val conditionedCsp = efcCsp.condition(sLits)

        val efcDnnf = efcCsp.toDnnfSmooth()
        val conditionedDnnf1 = efcDnnf.condition(sLits)
        val conditionedDnnf2 = conditionedCsp.toDnnfSmooth()

        val efcSatCount = efcDnnf.satCount
        val conditionedDnnf1SatCount1 = conditionedDnnf1.satCount
        val conditionedDnnf1SatCount2 = conditionedDnnf2.satCount


        assertEquals(conditionedDnnf1SatCount1, conditionedDnnf1SatCount2)
        assertTrue(conditionedDnnf1SatCount2 >= 0)
        assertTrue(conditionedDnnf1SatCount2 < efcSatCount)


    }


}
