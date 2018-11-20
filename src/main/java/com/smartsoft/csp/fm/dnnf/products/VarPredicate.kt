package com.smartsoft.csp.fm.dnnf.products


import com.smartsoft.csp.ast.Var

interface VarPredicate {

    fun isTrue(vr: Var): Boolean


}
