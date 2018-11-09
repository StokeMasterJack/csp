package com.tms.csp.pl

import com.tms.csp.ast.Space
import kotlin.test.Test

class ConditionTest{
    @Test
    fun test() {
        /*
        Or.condition: 2540
          Before: or(!and(6AT L4 XLE) 2540)
          After:  true
         */


        val space = Space.withVars("2540 6AT L4 XLE")
        val or = space.parseExp("or(!and(6AT L4 XLE) 2540)")
        val conditioned = or.condition("2540")
        println("conditioned = ${conditioned}")

    }
}