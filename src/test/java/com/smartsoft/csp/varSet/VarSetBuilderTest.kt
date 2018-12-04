package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.parse.VarSpaceK
import kotlin.test.Test
import kotlin.test.assertEquals

class VarSetBuilderTest {


    @Test
    fun test() {
        val varCodes = VarSpaceK.generateSampleVarCodes(wordCount = 3)
        val space = Space(varCodes)
        val a0 = space.getVar("a0")
        val a1 = space.getVar("a1")
        val a2 = space.getVar("a2")

        val b0 = space.getVar("b0")
        val b1 = space.getVar("b1")
        val b2 = space.getVar("b2")

        assertEquals(3,space.varSpace.wordCount)

        val n = space.varSetBuilder()

        assert(n.isEmpty())
        assert(n.size == 0)
        assert(n.awi.size == 0)
        assertEquals("", n.sr)
        assertEquals("", n.awi.sr)

        val cha0 = n.addVar(a0)

        assert(cha0)
        assert(n.isNotEmpty())
        assert(n.size == 1)
        assert(n.awi.size == 1)
        assertEquals("a0", n.sr)
        assertEquals("0", n.awi.ser)

        val cha1 = n.addVar(a1)

        assert(cha1)
        assert(n.isNotEmpty())
        assert(n.size == 2)
        assert(n.awi.size == 1)
        assertEquals("a0 a1", n.sr)
        assertEquals("0", n.awi.ser)

        val cha2 = n.addVar(a2)

        assert(cha2)
        assert(n.isNotEmpty())
        assert(n.size == 3)
        assert(n.awi.size == 1)
        assertEquals("a0 a1 a2", n.sr)
        assertEquals("0", n.awi.ser)

        val chb0 = n.addVar(b0)

        assert(chb0)
        assert(n.isNotEmpty())
        assert(n.size == 4)
        assert(n.awi.size == 2)
        assertEquals("a0 a1 a2 b0", n.sr)
        assertEquals("0 1", n.awi.ser)

        val chb1 = n.addVar(b1)

        assert(chb1)
        assert(n.isNotEmpty())
        assert(n.size == 5)
        assert(n.awi.size == 2)
        assertEquals("a0 a1 a2 b0 b1", n.sr)
        assertEquals("0 1", n.awi.ser)


        val chb2 = n.addVar(b2)

        assert(chb2)
        assert(n.isNotEmpty())
        assert(n.size == 6)
        assert(n.awi.size == 2)
        assertEquals("a0 a1 a2 b0 b1 b2", n.sr)
        assertEquals("0 1", n.awi.ser)

//        println("${a0.index} ${a0.wordIndex}")
//        println("${a1.index} ${a1.wordIndex}")
//        println("${a2.index} ${a2.wordIndex}")
//        println("${b0.index} ${b0.wordIndex}")
//        println("${b1.index} ${b1.wordIndex}")
//        println("${b2.index} ${b2.wordIndex}")


        val vsa = space.mkVarSetBuilder("a0 a1 a2")
        assert(vsa.isNotEmpty())
        assertEquals(3, vsa.size)
        vsa.assertSer("a0 a1 a2")
        vsa.assertAwi("0")


        val vsb = space.mkVarSetBuilder("b0 b1 b2")
        assert(vsb.isNotEmpty())
        assertEquals(3, vsb.size)
        vsb.assertSer("b0 b1 b2")
        vsb.assertAwi("1")


        val copy = vsa.copy() as VarSetBuilder
        assert(copy.isNotEmpty())
        assertEquals(3, copy.size)
        assertEquals("a0 a1 a2", copy.sr)
        assertEquals("0", copy.awi.ser)

        copy.addVarSet(vsb)
        assertEquals(6, copy.size)
        assertEquals("a0 a1 a2 b0 b1 b2", copy.sr)
        assertEquals("0 1", copy.awi.ser)


        copy.removeVar("a0")
        assertEquals(5, copy.size)
        assertEquals("a1 a2 b0 b1 b2", copy.sr)
        assertEquals("0 1", copy.awi.ser)

        copy.removeVar("a1")
        assertEquals(4, copy.size)
        assertEquals("a2 b0 b1 b2", copy.sr)
        assertEquals("0 1", copy.awi.ser)

        copy.removeVar("a2")
        assertEquals(3, copy.size)
        assertEquals("b0 b1 b2", copy.sr)
        assertEquals("1", copy.awi.ser)

        copy.removeVar("b0")
        assertEquals(2, copy.size)
        assertEquals("b1 b2", copy.sr)
        assertEquals("1", copy.awi.ser)

        copy.removeVar("b1")
        assertEquals(1, copy.size)
        assertEquals("b2", copy.sr)
        assertEquals("1", copy.awi.ser)

        copy.removeVar("b2")
        assertEquals(0, copy.size)
        assertEquals("", copy.sr)
        assertEquals("", copy.awi.ser)

        vsa.assertSer("a0 a1 a2");
        vsa.assertAwi("0")
        vsb.assertSer("b0 b1 b2");
        vsb.assertAwi("1")
        copy.assertSer("");
        copy.assertAwi("")



        copy.addVarSet(vsa)
        assertEquals(3, copy.size)
        assertEquals("a0 a1 a2", copy.sr)
        assertEquals("0", copy.awi.ser)

        copy.addVarSet(vsb)
        assertEquals(6, copy.size)
        assertEquals("a0 a1 a2 b0 b1 b2", copy.sr)
        assertEquals("0 1", copy.awi.ser)

        copy.removeVarSet(vsb)
        assertEquals(3, copy.size)
        assertEquals("a0 a1 a2", copy.sr)
        assertEquals("0", copy.awi.ser)

        copy.removeVarSet(vsa)
        assertEquals(0, copy.size)
        assertEquals("", copy.sr)
        assertEquals("", copy.awi.ser)

        vsa.overlap(vsb).assertSer("")

        copy.addVarSet(vsa)
        copy.addVarSet(vsb)
        assertEquals(6, copy.size)
        assertEquals("a0 a1 a2 b0 b1 b2", copy.sr)
        assertEquals("0 1", copy.awi.ser)

        val ola = copy.overlap(vsa) as VarSetBuilder
        ola.assertSer("a0 a1 a2")
        ola.assertAwi("0")

        val olb = copy.overlap(vsb) as VarSetBuilder
        olb.assertSer("b0 b1 b2")
        olb.assertAwi("1")


    }
}