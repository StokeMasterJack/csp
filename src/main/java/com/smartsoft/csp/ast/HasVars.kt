package com.smartsoft.csp.ast

import com.smartsoft.csp.fm.dnnf.products.Cube
import com.smartsoft.csp.util.varSets.VarSet

interface HasVars : HasSpace {

    val vars: VarSet

    fun containsVar(lit: Exp): Boolean = vars.containsVar(lit.vr)

    fun containsVar(vr: Var): Boolean = vars.containsVar(vr)

    fun containsVar(lit: Lit): Boolean = vars.containsVar(lit)

    fun containsVar(varCode: String): Boolean = vars.containsVarCode(varCode)


    fun anyVarOverlap(exp: Exp): Boolean = vars.anyVarOverlap(exp)
    fun anyVarOverlap(cube: Cube): Boolean = vars.anyVarOverlap(cube.vars)
    fun anyVarOverlap(vs: VarSet): Boolean = vars.anyVarOverlap(vs)


    fun isVarDisjoint(exp: Exp): Boolean = vars.isVarDisjoint(exp)
    fun isVarDisjoint(vs: VarSet): Boolean = vars.isVarDisjoint(vs)
    fun isVarDisjoint(cube: Cube): Boolean = vars.isVarDisjoint(cube)


    fun containsVarId(varId: Int): Boolean = vars.containsVarId(varId)

    fun varIterator(): Iterator<Var> = vars.varIter()

    fun varIt(): Iterable<Var> = vars.varIt()

    val varCount: Int get() = vars.size


}
