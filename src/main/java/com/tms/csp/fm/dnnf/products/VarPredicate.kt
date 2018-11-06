package com.tms.csp.fm.dnnf.products


import com.tms.csp.ast.Var

interface VarPredicate {

    fun isTrue(vr: Var): Boolean


}
