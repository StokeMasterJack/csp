package com.smartsoft.csp

import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.data.CspSample
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


        assertEquals(1, v1.vrId)
        assertEquals(2, v2.vrId)

        val i1 = v1.vrId
        val i2 = v2.vrId


        val vv1 = sp.getVar(v1.vrId)
        val vv2 = sp.getVar(v2.vrId)

        assertEquals(v1, sp.getVar(v1.vrId))
        assertEquals(v2, sp.getVar(v2.vrId))


        assert(vv1 === sp.getVar(v1.vrId))
        assert(vv2 === sp.getVar(v2.vrId))


        assertEquals(v1, sp.getVar(v1.varCode))
        assertEquals(v2, sp.getVar(v2.varCode))


    }


    @Test
    fun test() {


        val csp = CspSample.Camry2011NoDc.parseCsp()
        println(csp.toDnnf().smooth.satCount)


    }
}