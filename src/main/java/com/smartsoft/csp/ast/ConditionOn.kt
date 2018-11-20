package com.smartsoft.csp.ast

import com.smartsoft.csp.dnnf.products.Cube

interface ConditionOn {

    fun conditionThat(that: Exp): Exp;

    val space: Space
        get() = when (this) {
            is Lit -> this.space
            is Cube -> this.space
            else -> throw IllegalStateException()
        }

}