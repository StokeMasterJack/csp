package com.tms.csp.ast

interface ConditionOn {

    fun conditionThat(that: Exp): Exp;

    fun assignSafe(csp: Csp)
}