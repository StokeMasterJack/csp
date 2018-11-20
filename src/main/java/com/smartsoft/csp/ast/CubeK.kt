package com.smartsoft.csp.ast

import com.smartsoft.csp.fm.dnnf.products.Cube
import com.smartsoft.csp.util.varSets.VarSet

object CubeK {

    fun eq(c1: Cube, c2: Cube): Boolean {
        if (!VarSet.eq(c1.vars, c2.vars)) return false;
        if (!VarSet.eq(c1.trueVars, c2.trueVars)) return false;
        return true;
    }

}