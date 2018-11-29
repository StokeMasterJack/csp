package com.smartsoft.csp.ast

import com.smartsoft.csp.dnnf.products.Cube

interface ConditionOn {

    val space: Space
        get() = when (this) {
            is Lit -> this.space
            is Cube -> this.space
            else -> throw IllegalStateException()
        }


    val litCount: Int
        get() = when (this) {
            is Lit -> 1
            is Cube -> this.size
            else -> throw IllegalStateException()
        }


}

