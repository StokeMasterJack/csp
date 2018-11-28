package com.smartsoft.csp.ast


/**
 * represents a set of complex fact
 * Used by union find (compute fccs)
 */
interface FConstraintSet<T> {

    /**
     * Does not include simple fact
     */
    val constraintCount: Int

    fun isDirectlyRelated(c1: Int, c2: Int): Boolean

    val argIt: Iterable<Exp>

    fun getConstraint(index: Int): T

}
