package com.tms.csp.pl

import com.google.common.collect.ImmutableSet
import com.tms.csp.Vars
import com.tms.csp.ast.Csp
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Parser
import com.tms.csp.ast.Space
import com.tms.csp.data.CspSample
import com.tms.csp.data.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ParserTest : PLTestBase() {

    lateinit var space: Space
    lateinit var f: Parser


    @Before
    fun setUp() {
        val vars = "a b Base LE SE XLE Hyb red blue v6 LA Ash Bisque 1F7 776 4T8".split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val varSet = ImmutableSet.copyOf(vars)
        space = Space(varSet)
        f = space.parser
    }


    @Test
    fun test_parsePosConstant() {
        val e = f.parseExp("true")
        assertTrue(e.isPos)
        assertTrue(e.isConstant)
        assertTrue(e.isConstantTrue)
        assertEquals("true", e.toString())
    }

    @Test
    fun test_parseNegConstant() {
        val e = f.parseExp("false")
        assertTrue(e.isConstant)
        assertTrue(e.isConstantFalse)

        assertEquals("false", e.toString())
    }

    @Test
    fun test_parsePosSimple() {
        val e = f.parseExp("red")
        assertTrue(e.isPos)
        assertTrue(e.isPosLit)
        assertEquals("red", e.toString())

    }

    @Test
    fun test_parseImp() {
        val expText = "imp(red v6)"
        val csp = Csp.parse(expText)
    }

    @Test
    fun test_parsePosBinary() {
        var e: Exp = f.parseExp("imp(red v6)")

        assert(f.space === e.sp())

        assertTrue(e.isPos && e.isPair)
        assertEquals("or", e.opTag())
        assertEquals("or(!red v6)", e.toString())

        e = f.parseExp("nand(red v6)")
        assertTrue(e.isPos)
        assertTrue(e.isPair)
        assertEquals("or", e.opTag())
        assertEquals("or(!red !v6)", e.toString())

        //        e = fCon.parseExp("iff(red v6)");
        //        assertTrue(e.isPos() && e.isPair());
        //        assertEquals("iff", e.opTag());
        //        assertEquals("iff(red v6)", e.toString());

        e = f.parseExp("xor(red blue)")
        assertTrue(e.isPos && e.isPair)
        assertEquals("xor", e.opTag())
        assertEquals("xor(blue red)", e.toString())

        e = f.parseExp("or(red blue)")
        assertTrue(e.isPos && e.isPair)
        assertEquals("or", e.opTag())
        assertEquals("or(blue red)", e.toString())

        e = f.parseExp("and(red blue)")
        assertTrue(e.isPos && e.isPair)
        assertEquals("and", e.opTag())
        assertEquals("and(blue red)", e.toString())
    }

    @Test
    fun testSort() {
        val e = f.parseExp("or(red blue)")
        assertTrue(e.isPos && e.isPair)
        assertEquals("or", e.opTag())
        assertEquals("or(blue red)", e.toString())

    }


    @Test
    fun test_parseNegSimple() {
        val e = f.parseExp("!red")
        assertTrue(e.isNegated)
        assertTrue(e.isLit)
        assertTrue(e.isNegLit)
        assertEquals("!red", e.toString())
    }


    private fun flp(sExpIn: String, expected: String) {
        val inExp = f.parseExp(sExpIn)
        val flip1 = inExp.flip
        val flip2 = flip1.flip

        assertEquals(f.parseExp(expected), flip1)
        assertEquals(inExp, flip2)
    }

    @Test
    fun test_Flip2() {
        flp("!imp(red v6)", "imp(red v6)")
        flp("!nand(red v6)", "nand(red v6)")
    }


    @Test
    fun testXor() {
        val text = "xor(Base LE SE XLE Hyb)"
        val e = f.parseExp(text)

        assertTrue(e.isPos)
        assertTrue(e.isXor)
        assertEquals(5, e.argCount.toLong())
        assertEquals("xor", e.opTag())
        assertEquals("xor(Base Hyb LE SE XLE)", e.toString())
        assertEquals("Base", e.arg(0).toString())

    }

    @Test
    fun test_Composition1() {
        val text1 = "or(and(Ash 4T8) and(Ash 776) and(Bisque !1F7))"
        val e = f.parseExp(text1)

        assertTrue(e.isPos)
        assertTrue(e.isOr)
        assertEquals(3, e.argCount.toLong())
        assertEquals("or", e.opTag())

        val expected = f.parseExp(e.toString())
        val actual = e

        assertEquals(expected, actual)


    }


    @Test
    fun parseThenSerializeThenCompareCamry2011() {
        parseThenSerializeThenCompare1(CspSample.Camry2011NoDc)
    }

    @Test
    fun parseThenSerializeThenCompareTundra2013() {
        parseThenSerializeThenCompare1(CspSample.Tundra)
    }

    @Test
    fun parseThenSerializeThenCompareEfc() {
        parseThenSerializeThenCompare1(CspSample.EfcOriginal)
    }


    private fun parseThenSerializeThenCompare1(cspSample: CspSample) {

        val sp = Space()
        val lines = TestData.loadCspAsTextLines(cspSample)

        for (line in lines) {
            if (Parser.isComment(line)) {
                continue
            }
            if (Vars.isVarsLine(line)) {
                continue
            }

            val exp1 = sp.parseExp(line) ?: continue

            val sExp1 = exp1.toString()

            val exp2 = space.parseExp(sExp1)

            assertEquals(exp1, exp2)
            assertEquals(exp1.toString(), exp2.toString())


        }
    }


}
