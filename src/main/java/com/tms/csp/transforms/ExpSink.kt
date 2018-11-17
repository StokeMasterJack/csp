package com.tms.csp.transforms

import com.tms.csp.ast.Exp

interface ExpSink {

    fun addConstraint(constraint: Exp)

}
