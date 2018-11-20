package com.smartsoft.csp.ast

import com.smartsoft.csp.Vars
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

//
////import com.tms.csp.ast.Op
////import com.tms.csp.ast.Space
//import kotlin.test.BeforeTest
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue

class ArgBuilderTest {

    lateinit var sp: Space;

    @Before
    fun setup() {
        val vars:Iterable<String> = Vars.parseVarList("a b c d e")
        sp = Space(vars)

    }

    @Test
    fun testAndDups() {

        val e1 = sp.mkAnd("a b c c")
        val e2 = sp.mkAnd("a a b c")

        val e3 = sp.argBuilder(Op.And).addExp("a").addExp("b").addExp("c").addExp("c").mk()

        assertTrue(e1.isAnd)
        assertEquals(Op.Cube, e1.op)
        assertEquals(3, e1.argCount)
        assertEquals(e1, e2)
        assertEquals(e2, e3)

        assertTrue { e1.expId == e2.expId }
        assertTrue { e2.expId == e3.expId }


    }


    @Test
    fun testOrDups() {

        val e1 = sp.mkOr("a b c c")
        val e2 = sp.mkOr("a a b c")

        val e3 = sp.argBuilder(Op.Or).addExp("a").addExp("b").addExp("c").addExp("c").mk()

        assertTrue(e1.isOr)
        assertTrue(e1.isClause)
        assertEquals(Op.Or, e1.op)
        assertEquals(3, e1.argCount)
        assertEquals(e1, e2)
        assertEquals(e2, e3)

        assertTrue { e1.expId == e2.expId }
        assertTrue { e2.expId == e3.expId }


    }

    @Test
    fun testAndFlips() {

        val b = sp.argBuilder(Op.And)
        b.addComplex("a")
        b.addExp("b")
        b.addExp("c")
        b.addExp("!a")
        b.addExp("!a")

        val e1 = sp.mkAnd("a b c !a")
        val e2 = sp.mkAnd("a a b !b")

        assertTrue(e1.isFalse)
        assertTrue(e2.isFalse)


    }

}