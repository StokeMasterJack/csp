package com.smartsoft.csp.ast


import com.smartsoft.csp.fm.dnnf.products.Cube

class Imp(space: Space, expId: Int, args: Array<Exp>) : PosComplexMultiVar(space, expId, args) {

    var or: Exp? = null

    override val isBinaryType: Boolean
        get() = true

    override val posOp: PLConstants.PosOp
        get() = OP

    override fun condition(ctx: Cube): Exp {
        if (isVarDisjoint(ctx)) return this

        val e1 = expr1
        val e2 = expr2

        val v1 = e1.condition(ctx)
        val v2 = e2.condition(ctx)


        if (v1.isFalse || v2.isTrue) {
            return mkTrue()
        }


        if (v1.isTrue) {
            return v2.condition(ctx)
        }

        if (v2.isTrue) {
            return mkTrue()
        }


        if (v2.isFalse) {
            return v1.flip.condition(ctx)
        }
        return if (v1 === e1 && v2 === e2) {
            this
        } else mkImp(v1, v2).condition(ctx)

    }


    fun toCnf(): Exp {
        if (arg2.isAnd) {
            val a: Array<Exp?> = arrayOfNulls<Exp>(arg2.size())
            val e1 = arg1
            for (i in 0 until arg2.argCount) {
                val e2 = arg2.getArg(i)
                a[i] = mkOr(e1.flip, e2)
            }
            return mkAnd(*a.requireNoNulls())
        }

        return mkOr(arg1.flip, arg2)
    }


    val toOr: Or
        get() {
            if (or == null) {

                val a1 = arg1
                val a2 = arg2
                val na1 = a1.flip

                val space = space

                this.or = space.mkOr(na1, a2)
            }
            return or!!.asOr
        }

    companion object {

        val OP: PLConstants.PosOp = PLConstants.PosOp.IMP
    }


}
