package com.smartsoft.csp.ast

abstract class PosComplex(space: Space, expId: Int) : Complex(space, expId) {

    override val isPosComplex: Boolean get() = true
    override val isComplex: Boolean get() = true



}