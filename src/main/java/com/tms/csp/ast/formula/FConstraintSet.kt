package com.tms.csp.ast.formula


import com.tms.csp.ast.Exp

/**
 * represents a set of complex fact
 */
interface FConstraintSet  {

    /**
     * Does not include simple fact
     */
    val constraintCount: Int

    fun isDirectlyRelated(c1: Int, c2: Int): Boolean

    val argIt: Iterable<Exp>

}
