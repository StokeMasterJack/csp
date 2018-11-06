package com.tms.csp.ast

abstract class PosComplex(space: Space, expId: Int) : Complex(space, expId) {

    override fun isPosComplex(): Boolean {
        return true
    }

    override fun isComplex(): Boolean {
        return true
    }


}