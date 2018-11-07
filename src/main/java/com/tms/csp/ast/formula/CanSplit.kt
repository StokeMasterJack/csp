package com.tms.csp.ast.formula

import com.tms.csp.ast.HasSpace
import com.tms.csp.ast.Var

//simple split
interface CanSplit:HasSpace {

    fun decide(): Var


}
