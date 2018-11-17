package com.tms.csp.ast

class LitPair(val lit1: Exp, val lit2: Exp) {

    override fun toString(): String {
        return lit1.toString() + " " + lit2
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is LitPair) return false

        val litPair = o as LitPair?

        if (lit1 != litPair!!.lit1) return false
        return if (lit2 != litPair.lit2) false else true

    }

    override fun hashCode(): Int {
        var result = lit1.hashCode()
        result = 31 * result + lit2.hashCode()
        return result
    }

    /**
     * Assuming this is failed LitPair, convert to a cnf clause.
     * aka convert
     * from: !and(e1 e2)
     * to:   or(!e1 !2e)
     */
    fun toClause(): Exp {
        val space = lit1.space
        return space.mkOr(lit1.flip, lit2.flip)
    }
}
