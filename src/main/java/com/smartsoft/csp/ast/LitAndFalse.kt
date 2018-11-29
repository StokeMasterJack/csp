package com.smartsoft.csp.ast

import com.smartsoft.csp.ast.PLConstants.PosOp
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.varSet.VarSet
import java.math.BigInteger


/**
 * This is for the smooth transformation
 * replace unmatched lits with: lit or (!lit and false)
 */
class LitAndFalse(val lit: Lit, expId: Int) : PosComplexSingleVar(lit.space, expId) {

//    val cubes = emptySet<Cube>()

    override val isPos: Boolean get() = false

    override val pos: Exp get() = throw UnsupportedOperationException()

    override val argCount: Int get() = 2

    override val hasFlip: Boolean get() = false

    override val firstVar: Var get() = lit.vr

    override val isDnnf: Boolean get() = true

    override fun checkDnnf(): Boolean = true

    override val posOp: PosOp get() = PosOp.AND

    override val isComplex: Boolean get() = true

    override fun computeCubesSmooth(): Set<Cube> = cubes

//    override fun computeSatCountSmooth(): Long = 2

    override fun computeSatCount(): BigInteger = BigInteger.ZERO

    override val isSmooth: Boolean get() = true

    override val vars: VarSet get() = lit.vars

//    override val cubesRough: Set<Cube> get() = cubes
//
//    override val cubesSmooth: Set<Cube> get() = cubes

    override val cubesRough: Set<Cube> get() = cubes

    override val cubesSmooth: Set<Cube> get() = cubes

    override fun getArg(index: Int): Exp = when (index) {
        0 -> lit
        1 -> sp().mkFalse()
        else -> throw IllegalStateException()
    }

    override fun condition(ctx: Cube): Exp {
        return if (ctx.containsVar(_vr)) {
            mkFalse()
        } else {
            this
        }
    }

    override fun condition(lit: Lit): Exp {
        return if (lit.varId == _vr.vrId) {
            mkFalse()
        } else {
            this
        }
    }


    override fun toString(): String {
        val a = Ser()
        serialize(a)
        return a.toString()
    }

    override fun serialize(a: Ser) {
        val token = getPosComplexOpToken(a)
        a.append(token)
        a.append(PLConstants.LPAREN)
        arg1.serialize(a)
        a.argSep()
        arg2.serialize(a)
        a.append(PLConstants.RPAREN)
    }

    override val isSat: Boolean get() = true

    override fun copyToOtherSpace(destSpace: Space): Exp {
        if (_vr.space === destSpace) return this
        val destVar = destSpace.getVar(varCode)
        assert(destVar.space === destSpace)
        return destVar.mkDcOr()
    }

    override val varCode: String get() = vr.varCode

    override val vr: Var get() = lit.vr


    private val _vr: Var get() = lit.vr

    override fun serializeTinyDnnf(a: Ser) {
        a.append('A')
        a.append(' ')
        a.append(arg1.serialize(a))
        a.append(' ')
        a.append(arg2.serialize(a))
    }

    override val isNew: Boolean get() =  TODO()

    override fun notNew() { TODO() }
}

