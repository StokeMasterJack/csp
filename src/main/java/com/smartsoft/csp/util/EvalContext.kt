package com.smartsoft.csp.util

import com.smartsoft.csp.ast.Var

interface EvalContext {

    fun isTrue(vr: Var): Boolean

    fun isFalse(vr: Var): Boolean

    fun isOpen(vr: Var): Boolean


}
