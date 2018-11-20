package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

interface ExpSink {

    fun addConstraint(constraint: Exp)

}
