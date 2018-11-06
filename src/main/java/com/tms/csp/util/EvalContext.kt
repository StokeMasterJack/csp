package com.tms.csp.util

import com.tms.csp.ast.Var

interface EvalContext {

    fun isTrue(vr: Var): Boolean

    fun isFalse(vr: Var): Boolean

    fun isOpen(vr: Var): Boolean


}
