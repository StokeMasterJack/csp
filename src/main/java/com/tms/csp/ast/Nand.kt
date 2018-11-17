package com.tms.csp.ast

import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.fm.dnnf.products.Cube

class Nand(space: Space, expId: Int, args: Array<Exp>) : PosComplexMultiVar(space, expId, args) {

    override val isBinaryType: Boolean
        get() = true


    override val posOp: PLConstants.PosOp
        get() = OP

    init {
        assert(isBinary)
    }


    val toOr: Or
        get() {
            val e1 = arg1
            val e2 = arg2
            return space.mkOr(e1.flip, e2.flip).asOr
        }


    override fun condition(ctx: Cube): Exp {
        if (isVarDisjoint(ctx)) return this

        val e1 = expr1
        val e2 = expr2


        val v1 = e1.condition(ctx)
        val v2 = e2.condition(ctx)

        return if (v1.isFalse || v2.isFalse) {
            mkTrue()
        } else if (v1.isTrue && v2.isTrue) {
            mkFalse()
        } else if (v1.isTrue && v2.isOpen) {
            v2.flip
        } else if (v2.isTrue && v1.isOpen) {
            v1.flip
        } else if (v1.isOpen && v2.isOpen) {
            if (v1 === e1 && v2 === e2) {
                this
            } else {
                mkNand(v1, v2)
            }
        } else {
            throw IllegalStateException()
        }

    }

    /*
    x

     */
    fun flattenNand(): Exp {
        val vr: Lit
        val or: Or
        if (arg1.isPosLit && arg2.isOr) {
            vr = arg1.asLit
            or = arg2.asOr
        } else if (arg2.isPosLit && arg1.isOr) {
            vr = arg2.asLit
            or = arg1.asOr
        } else {
            return this
        }

        val andArgs = ArgBuilder(_space, Op.And)
        for (orArg in or.argIt) {
            val nand = _space.mkBinaryNand(vr, orArg)
            andArgs.addExp(nand)
        }

        return andArgs.mk()
    }

    companion object {

        val OP: PLConstants.PosOp = PLConstants.PosOp.NAND
    }


}
