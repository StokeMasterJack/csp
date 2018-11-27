package com.smartsoft.csp.ast

import com.google.common.collect.Iterators
import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.varSets.VarSet

class Not(pos: Exp, expId: Int) : Complex(pos.space, expId) {

    override val pos: PosComplexMultiVar

    override var isNew: Boolean = false
        private set

    override val isPos: Boolean
        get() = false

    override val arg: Exp get() = pos

    override val neg: Exp get() = this

    override val isNot: Boolean get() = true

    override val isComplex: Boolean get() = true

    override val isNegComplex: Boolean get() = true

    override val posOp: PLConstants.PosOp get() = pos.posOp

    override val firstVar: Var get() = pos.firstVar

    override val op: Op get() = Op.Not

    override val argCount: Int get() = 1

    override val vars: VarSet get() = pos.vars


    override val isSat: Boolean get() = toNnf().isSat

    init {
        assert(pos is PosComplexMultiVar)
        this.pos = pos as PosComplexMultiVar
        this.isNew = true
    }

//    fun flip(): Exp {
//        return pos
//    }

    override val flip: Exp get() = pos

    override fun anyVarOverlap(exp: Exp): Boolean {
        return pos.anyVarOverlap(exp)
    }

    override fun pushNotsIn(): Exp {
        val ret = pushNotsInInternal()
        //        System.err.println("Not.pushNotsIn: ");
        //        System.err.println("  Before: " + this);
        //        System.err.println("  After: " + ret);

        return ret
    }

    private fun pushNotsInInternal(): Exp {
        if (pos.isAnd) {
            pos.asAnd.createHardFlip()
            return pos.asAnd.createHardFlip()
        } else return if (pos.isOr) {
            pos.asOr.createHardFlip()
        } else {
            this
        }
    }


    override fun containsVarId(varId: Int): Boolean {
        return pos.containsVarId(varId)
    }

    override fun condition(ctx: Cube): Exp {
        assert(!isConstant)

        val sPos = pos.condition(ctx)

        return if (sPos === pos) {
            this
        } else {
            sPos.flip
        }

    }

    fun simplify(): Exp {
        throw UnsupportedOperationException()
        //        EvalContext ctx = null;
        //        Exp simplify = pos.simplify(ctx);
        //        return simplify.flip;
    }


    override fun condition(lit: Lit): Exp {

        //        System.err.println("Not.condition(lit)");
        if (!containsVar(lit)) {
            return this
        }


        val sPos = pos.condition(lit)

        if (sPos.isNot) {
            if (true) throw IllegalStateException()
            return sPos.arg
        } else {
            return sPos.flip
        }


    }

    override fun getArg(index: Int): Exp {
        if (index == 0) return pos
        throw IndexOutOfBoundsException()
    }


    fun argIter(): Iterator<Exp> {
        val pp = pos
        return Iterators.singletonIterator<Exp>(pp)
    }

    override fun serialize(a: Ser) {
        if (pos.isConstantTrue) {
            a.constantFalse()
        } else if (pos.isPosComplex) {
            a.bang()
            pos.serialize(a)
        } else {
            System.err.println("Should never get here: Not.serialize")
            throw IllegalStateException()
        }
    }

    fun hardFlip(): Exp {
        System.err.println("Not.hardFlip")

        val b = pos.flipArgs()
        if (pos.isAnd) {
            assert(b.op.isAnd)
            return b.mk()
        }
        if (pos.isOr) {
            assert(b.op.isOr)
            return b.mk()
        }

        return this
    }

    fun toNnf(): Exp {
        assert(!isXorOrContainsXor)
        val pNnf = pos.toNnf(false)
        assert(pNnf.isOr || pNnf.isAnd)

        val newOp: Op
        if (pos.isOr) {
            newOp = Op.And
        } else {
            newOp = Op.Or
        }
        val b = ArgBuilder(_space, newOp)
        for (arg in pNnf.argIt) {
            b.addExp(arg.flip)
        }

        return b.mk()
    }

    fun hasFlip(): Boolean {
        return true
    }


    override fun notNew() {
        isNew = false
    }

    fun canPushNotsIn(): Boolean {
        return pos.isOr || pos.isAnd
    }

    fun samePos(that: Not): Boolean {
        return pos == that.pos
    }

    override fun toDnnf(): Exp {
        val nnf = toNnf()
        assert(!nnf.isNot)
        return nnf.toDnnf()
    }

    override val satCountPL: Long get() {
        if(true) throw IllegalStateException()
        return toNnf().satCountPL
    }

    override val argIter: Iterator<Exp> get() = Iterators.singletonIterator(pos)

}
