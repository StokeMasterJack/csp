package com.tms.csp.ast


import com.google.common.collect.ImmutableSet
import com.tms.csp.ast.PLConstants.FALSE_CHAR
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.varSets.VarSet

import java.math.BigInteger

class False internal constructor(override val pos: Exp) : Constant(pos.space, PLConstants.FALSE_EXP_ID) {

    override val isConstantFalse: Boolean get() = true

    override val isSat: Boolean get() = false

    override val satCount: BigInteger get() = BigInteger.ZERO

    override val cubeCount: Int get() = 0

    override val isPos: Boolean get() = false

    override val neg: Exp get() = this

    override val cubesSmooth: Set<Cube> get() = ImmutableSet.of()

    override fun serialize(a: Ser) {
        a.constantFalse()
    }

    override val flip: Exp get() = pos

    override fun serializeTinyDnnf(a: Ser) {
        a.append(FALSE_CHAR)
    }

    override fun toXml(a: Ser, depth: Int) {
        a.indent(depth)
        a.constantFalse()
    }

    fun prindent(a: Ser, depth: Int) {
        a.prindent(depth, toString())
    }


    override val asFalse: False get() = this

    override val satCountPL: Long get() = 0L

}