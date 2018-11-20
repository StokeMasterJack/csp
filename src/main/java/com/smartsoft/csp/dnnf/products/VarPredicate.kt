package com.smartsoft.csp.dnnf.products


import com.smartsoft.csp.ast.Var

interface VarPredicate {

    fun isTrue(vr: Var): Boolean


}
