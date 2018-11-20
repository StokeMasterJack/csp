package com.smartsoft.csp.argBuilder

import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Op
import com.smartsoft.csp.ast.Op1
import com.smartsoft.csp.ast.formula.FccState
import com.smartsoft.csp.ast.formula.Open


interface IArgBuilder {

    val fcc: FccState get() = Open()

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




