package com.smartsoft.csp.ast


import com.smartsoft.csp.fm.dnnf.products.Cube

class Rmp(space: Space, expId: Int, args: Array<Exp>) : PosComplexMultiVar(space, expId, args) {

    var or: Or? = null

    override val isBinaryType: Boolean
        get() = true

    override val posOp: PLConstants.PosOp
        get() = OP


    override val op: Op = Op.Rmp

    init {
        assert(isBinary)
    }

    override fun condition(ctx: Cube): Exp {
        if (isVarDisjoint(ctx)) return this

        val e1 = expr1
        val e2 = expr2

        val v1 = e1.condition(ctx)
        val v2 = e2.condition(ctx)

        if (v2.isFalse || v1.isTrue) {
            return mkTrue()
        }

        if (v2.isTrue) {
            return v1
        }

        if (v1.isFalse) {
            return v2.flip
        }

        if (v1 === e1 && v2 === e2) {
            return this
        }

        val retVal = mkRmp(v1, v2)



        return retVal

    }


    fun toCnf(): Exp {
        if (arg2.isAnd) {
            val a = arrayOfNulls<Exp?>(arg2.size())
            val e1 = arg1
            for (i in a.indices) {
                val e2 = arg2.getArg(i)
                a[i] = mkImp(e1, e2)
            }
            return mkAnd(*a.requireNoNulls())
        }


        //        if (arg1.isVar() && arg2.isCube()) {
        //            Exp[] a = new Exp[arg2.argCount() + 1];
        //            a[0] = arg1.flip;
        //            for (int i = 0; i < arg2.args().length; i++) {
        //                a[i + 1] = arg2.getArg(i);
        //            }
        //            return new Or(a);
        //        } else if (arg2.isVar() && arg1.isCube()) {
        //            Exp[] a = new Exp[arg1.argCount() + 1];
        //            a[0] = arg2;
        //            for (int i = 0; i < arg1.args().length; i++) {
        //                a[i + 1] = arg1.getArg(i).flip;
        //            }
        //            return new Or(a);
        //        }

        return mkOr(arg1.flip, arg2)
    }


    val toOr: Or
        get() {
            if (or == null) {
                this.or = space.mkOr(arg1, arg2.flip).asOr
            }
            return or!!
        }

    companion object {

        val OP: PLConstants.PosOp = PLConstants.PosOp.RMP
    }


}
