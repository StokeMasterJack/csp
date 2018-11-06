package com.tms.csp.argBuilder

import com.tms.csp.ast.Exp
import com.tms.csp.ast.Op
import com.tms.csp.ast.Op1


interface IArgBuilder {

    val isFcc: Boolean?

    val size: Int

    val op: Op
    val op1: Op1 get() = op.op1

    val argIt: Iterable<Exp>

    fun mk(): Exp

    fun createExpArray(): Array<Exp> {
        val aa: Array<Exp?> = arrayOfNulls(size)
        for ((i, arg) in argIt.withIndex()) {
            aa[i] = arg
        }
        return aa.requireNoNulls()
    }


}




