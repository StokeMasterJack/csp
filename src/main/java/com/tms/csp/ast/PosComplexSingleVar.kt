package com.tms.csp.ast

abstract class PosComplexSingleVar(space: Space, expId: Int) : PosComplex(space, expId) {

    override fun getVr(): Var {
        return super.getVr()
    }
}