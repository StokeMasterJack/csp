package com.smartsoft.csp.ast.formula

import com.smartsoft.csp.ast.HasSpace
import com.smartsoft.csp.ast.Var

//simple split
interface CanSplit:HasSpace {

    fun decide(): Var


}
