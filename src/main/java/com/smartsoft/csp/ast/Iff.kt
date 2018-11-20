package com.smartsoft.csp.ast


import com.smartsoft.csp.fm.dnnf.products.Cube

/**
 *
 * iff(a and(z y z)).toCnf =
 *
 * or(!a x)
 * or(!a y)
 * or(!a z)
 * or(!z !y !x a)
 *
 * all horn clauses
 *
 */
class Iff(space: Space, expId: Int, fixedArgs: Array<Exp>) : PosComplexMultiVar(space, expId, fixedArgs) {

    override val isBinaryType: Boolean
        get() = true


    override val posOp: PLConstants.PosOp
        get() = OP

    override val op: Op get() = Op.Iff

    init {
        assert(_args.size == 2)
    }


    override fun condition(ctx: Cube): Exp {

        if (isVarDisjoint(ctx)) return this


        val sExpr1 = expr1.condition(ctx)
        val sExpr2 = expr2.condition(ctx)

        if (sExpr1 === sExpr2) return mkTrue()

        return if (sExpr1.isFalse && sExpr2.isFalse)
            mkTrue()
        else if (sExpr1.isTrue && sExpr2.isTrue)
            mkTrue()
        else if (sExpr1.isFalse && sExpr2.isTrue)
            mkFalse()
        else if (sExpr1.isTrue && sExpr2.isFalse)
            mkFalse()
        else if (sExpr1.isTrue && sExpr2.isOpen)
            sExpr2
        else if (sExpr1.isOpen && sExpr2.isTrue)
            sExpr1
        else if (sExpr1.isFalse && sExpr2.isOpen)
            sExpr2.flip
        else if (sExpr1.isOpen && sExpr2.isFalse)
            sExpr1.flip
        else if (sExpr1 === expr1 && sExpr2 === expr2) {
            this
        } else {

            mkIff(sExpr1, sExpr2)
        }

    }

    override fun condition(lit: Lit): Exp {

        if (!containsVar(lit)) {
            return this
        }


        val sExpr1 = expr1.condition(lit)
        val sExpr2 = expr2.condition(lit)

        return if (sExpr1 === sExpr2) {
            mkTrue()
        } else if (sExpr1.isFalse && sExpr2.isFalse) {
            mkTrue()
        } else if (sExpr1.isTrue && sExpr2.isTrue) {
            mkTrue()
        } else if (sExpr1.isFalse && sExpr2.isTrue) {
            mkFalse()
        } else if (sExpr1.isTrue && sExpr2.isFalse) {
            mkFalse()
        } else if (sExpr1.isTrue && sExpr2.isOpen) {
            sExpr2
        } else if (sExpr1.isOpen && sExpr2.isTrue) {
            sExpr1
        } else if (sExpr1.isFalse && sExpr2.isOpen) {
            sExpr2.flip
        } else if (sExpr1.isOpen && sExpr2.isFalse) {
            sExpr1.flip
        } else if (sExpr1 === expr1 && sExpr2 === expr2) {
            this
        } else {
            mkIff(sExpr1, sExpr2)
        }

    }

    companion object {

        val OP: PLConstants.PosOp = PLConstants.PosOp.IFF
    }


}
