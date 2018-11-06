package com.tms.csp

import com.tms.csp.ast.Space
import com.tms.csp.ast.Var
import com.tms.csp.data.CspSample
import kotlin.test.Test
import kotlin.test.assertEquals

class SpaceTest {


//    @Test
//    fun test() {
//        val space: Space = Space()
//
//
//        val clob = CspSample.Camry2011NoDc.loadText()
//        val lines = space.parseClob(clob)
//
//
//    }


    @Test
    fun testMkVar() {


        val sp = Space()
        val v1: Var = sp.mkVar("aaa")
        val v2: Var = sp.mkVar("bbb")


        assertEquals(1, v1.varId)
        assertEquals(2, v2.varId)

        val i1 = v1.varId
        val i2 = v2.varId


        val vv1 = sp.getVar(v1.varId)
        val vv2 = sp.getVar(v2.varId)

        assertEquals(v1, sp.getVar(v1.varId))
        assertEquals(v2, sp.getVar(v2.varId))


        assert(vv1 === sp.getVar(v1.varId))
        assert(vv2 === sp.getVar(v2.varId))


        assertEquals(v1, sp.getVar(v1.varCode))
        assertEquals(v2, sp.getVar(v2.varCode))


    }


    @Test
    fun test() {


        val csp = CspSample.Camry2011NoDc.csp()
        println(csp.toDnnf().smooth.satCount)


    }
}