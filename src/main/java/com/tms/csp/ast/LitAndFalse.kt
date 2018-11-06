package com.tms.csp.ast

import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.varSets.VarSet


/**
 * This is for the smooth transformation
 * replace unmatched lits with: lit or (!lit and false)
 */
class LitAndFalse(val lit: Lit, expId: Int) : PosComplexSingleVar(lit.space, expId) {

//    val cubes = emptySet<Cube>()

    override fun isPos(): Boolean = false

    override fun getPos(): Exp = throw UnsupportedOperationException()

    override fun getArgCount(): Int = 2

    override fun hasFlip(): Boolean = false

    override fun getFirstVar(): Var = lit.vr

    override fun isDnnf(): Boolean = true

    override fun checkDnnf(): Boolean = true

    override fun getPosOp() = PLConstants.PosOp.AND

    override fun isComplex(): Boolean = true

    override fun isOr(): Boolean = false

    override fun isAnd(): Boolean = true

    override fun computeCubesSmooth(): Set<Cube> = cubes

//    override fun computeSatCountSmooth(): Long = 2

    override fun computeSatCount(): Long = 0

    override fun isSmooth(): Boolean = true

    override val vars: VarSet get() = lit.vars

//    override val cubesRough: Set<Cube> get() = cubes
//
//    override val cubesSmooth: Set<Cube> get() = cubes

    override fun getCubesRough(): Set<Cube> = cubes

    override fun getCubesSmooth(): Set<Cube> = cubes

    override fun getArg(i: Int): Exp = when (i) {
        0 -> lit
        1 -> sp().mkFalse()
        else -> throw IllegalStateException()
    }

    override fun condition(c: Cube): Exp = if (c.containsVar(_vr)) mkFalse() else this

    override fun condition(lit: Lit): Exp = if (lit.varId == _vr.varId) mkFalse() else this


    override fun toString(): String {
        val a = Ser()
        serialize(a)
        return a.toString()
    }

    override fun serialize(aa: Ser) {
        val token = getPosComplexOpToken(aa)
        aa.append(token)
        aa.append(PLConstants.LPAREN)
        arg1.serialize(aa)
        aa.argSep()
        arg2.serialize(aa)
        aa.append(PLConstants.RPAREN)
    }

    override fun isDcOr(): Boolean = true

    override fun isSat(): Boolean = true

    override fun copyToOtherSpace(destSpace: Space): Exp {
        if (_vr.space === destSpace) return this
        val destVar = destSpace.getVar(varCode)
        assert(destVar.space === destSpace)
        return destVar.mkDcOr()
    }

    override fun getVarCode(): String = vr.varCode

    override fun getVr(): Var = lit.getVr()

    private val _vr: Var get() = lit.vr

    override fun serializeTinyDnnf(a: Ser) {
        a.append('A')
        a.append(' ')
        a.append(arg1.serialize(a))
        a.append(' ')
        a.append(arg2.serialize(a))
    }

    override fun isNew(): Boolean {
        TODO()
    }

    override fun notNew() {
        TODO()
    }
}

