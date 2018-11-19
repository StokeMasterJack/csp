package com.tms.csp.pl

import com.tms.csp.ast.Csp
import com.tms.csp.ast.Space
import kotlin.test.Test

class KTransformTest{

    @Test
    fun testIsAllLits() {
        val sExp = "!or(and(1F7 Bisque) and(4T8 Ash) and(776 Ash))"

        val space = Space.withVars("1F7 Bisque 4T8 Ash 776")
        val notOr = space.parseExp(sExp)
        val pos = notOr.pos
        val pArgs = pos.args

        println(notOr)
        println(pos)

        println(notOr.isAllLits)
        println(pos.isAllLits)

        for (pArg in pArgs) {
            println(pArg)
        }



    }

}