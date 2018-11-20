package com.smartsoft.csp.ast


import com.smartsoft.csp.ast.PLConstants.TRUE_CHAR
import java.math.BigInteger

class True internal constructor(space: Space, expId: Int) : Constant(space, expId) {

    private var _neg: Exp? = null

    override val isConstantTrue: Boolean get() = true

    override val isSat: Boolean get() = true

    override val satCount: BigInteger get() = BigInteger.ONE

    override val cubeCount: Int get() = 1

    override val isPos: Boolean get() = true

    override val pos: Exp get() = this

    override fun serialize(a: Ser) {
        a.constantTrue()
    }

    override val flip: Exp get() = neg

    override val neg: Exp get() {
        if (this._neg == null) {
            this._neg = _space.mkFalse()
        }
        return this._neg!!
    }


    override fun serializeTinyDnnf(a: Ser) {
        a.append(TRUE_CHAR)
    }

    override fun toXml(a: Ser, depth: Int) {
        a.indent(depth)
        a.constantTrue()
    }

    override val satCountPL: Long get() = 1

}