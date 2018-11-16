package com.tms.csp.ast

import com.tms.csp.fm.dnnf.products.Cube

interface ConditionOn {

    fun conditionThat(that: Exp): Exp;

    val space: Space
        get() = when (this) {
            is Lit -> this.space
            is Cube -> this.space
            else -> throw IllegalStateException()
        }

}