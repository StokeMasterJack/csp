package com.smartsoft.csp.dnnf

import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.data.CspSample
import com.smartsoft.csp.util.CspBaseTest2
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class DnnfCondition : CspBaseTest2() {

    @Test
    fun testLit() {
        val n = Csp.compileDnnf("b")

        assertTrue(n.con("b").isConstantTrue)
        assertTrue(n.con("!b").isConstantFalse)

        assertEquals(1, n.satCountLong)

        val bt = n.con("b")
        val bf = n.con("!b")

        assertEquals(1, bt.satCountLong)
        assertEquals(0, bf.satCountLong)
    }


    @Test
    fun testGetVars() {

        val b = Csp.compileDnnf("b")
        assert(b.isPosLit)
        assertEquals(1, b.varCount)


        val t = b.con("b")
        assertTrue(t.isConstantTrue)
        assertEquals(0, t.varCount)


        val f = b.con("!b")
        assertTrue(f.isConstantFalse)
        assertEquals(0, f.varCount)

    }


    /**
     * Note that the satCounts differ between camryRough and camrySmooth.
     * This is due to to don'tCon cares.
     *
     *
     * smoothSatCount = roughSatCount * 2 ^ (smoothVarCount - roughVarCount)
     */
    @Test
    fun camrySmooth() {
        val csp = Csp.parse(CspSample.Camry2011NoDc)

        var n = csp.toDnnf().smooth
        assertTrue(n.isSat)

        assertEquals(520128, n.satCountLong)

        n = n.con("LE")
        assert(n.isSmooth)

        assertTrue(n.isSat)
        assertEquals(4800, n.satCountLong)     //4800

        n = n.con("V6")
        assert(n.isSmooth)

        assertTrue(n.isSat)
        assertEquals(1920, n.satCountLong)

        n = n.con("Ash")
        assert(n.isSmooth)

        assertTrue(n.isSat)
        assertEquals(896, n.satCountLong)

        n = n.con("776").smooth
        assert(n.isSmooth)

        assertFalse(n.isSat)
        assertEquals(0, n.satCountLong)


    }






}
