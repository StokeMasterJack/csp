package com.smartsoft.csp.ast

import com.smartsoft.csp.fm.dnnf.products.Cube
import com.smartsoft.csp.util.varSets.VarSet

abstract class Constant(space: Space, expId: Int) : Exp(space, expId) {

    override val isConstant: Boolean get() = true

    override val isLeaf: Boolean get() = true

    override val posOp: PLConstants.PosOp get() = PLConstants.PosOp.TRUE

    override val firstVar: Var get() = throw IllegalStateException()


    override val isNew: Boolean
        get() = false

    //    @Override
    //    public void print(int depth) {
    //        Exp.prindent(depth, serialize());
    //    }

    override val varCount: Int
        get() = 0

    override val isDnnf: Boolean
        get() = true

    override val isSmooth: Boolean
        get() = true

    //    @Override
    //    public Set<Lit> getLits() {
    //        return ImmutableSet.of();
    //    }

    override val vars: VarSet
        get() {
//            if (isTrue) {
//                return space.mkEmptyVarSet()
//            } else {
//                throw IllegalStateException(simpleName + "  " + this.toString())
//            }
            return space.mkEmptyVarSet()
        }

    override val smooth: Exp
        get() = this

    override val argCount: Int
        get() = 0

    override fun isConstant(sign: Boolean): Boolean {
        return sign == sign()
    }

    override fun checkDnnf(): Boolean {
        return true
    }

    override fun containsVarId(varId: Int): Boolean {
        return false
    }

    fun simplify(): Exp {
        return this
    }

    override fun condition(lit: Lit): Exp {
        return this
    }

    override fun condition(ctx: Cube): Exp {
        return this
    }

    override fun project(outVars: VarSet): Exp {
        return this
    }

    override fun anyVarOverlap(exp: Exp): Boolean {
        return false
    }

    override fun toDnnf(): Exp {
        return this
    }

    fun hasFlip(): Boolean {
        return true
    }

    override fun litMatch(): Exp {
        return this
    }

    override fun notNew() {
        throw UnsupportedOperationException()
    }
}
