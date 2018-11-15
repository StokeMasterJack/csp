package com.tms.csp.ast


import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.formula.CanSplit
import com.tms.csp.fm.dnnf.products.Cube

open class Or(space: Space, expId: Int, fixedArgs: Array<Exp>) : PosComplexMultiVar(space, expId, fixedArgs), CanSplit {

    init {
        assert(fixedArgs.size > 1) { this }
        assert(Exp.checkFixedArgs(fixedArgs))
        assert(!isNestedOr)

        if (false) {
            if (args.size == 2) {
                if (Exp.isFlip(args[0], args[1])) {
                    System.err.println("DontCare[" + args[0].vr + "]")
                }
            }
        }

    }

    override fun toDnnf(): Exp = with(space.expFactory) {
                val decisionVar = decide()
        val tLit = decisionVar.pLit()
        val fLit = decisionVar.nLit()

        val tCon: Exp = this@Or.condition(tLit)
        val tDnnf = tCon.toDnnf()
        val tDAnd = mkDAnd(tDnnf, tLit)
        if (tDAnd.isTrue) return tDnnf


        val fCon: Exp = this@Or.condition(fLit)
        val fDnnf = fCon.toDnnf()
        val fDAnd = mkDAnd(fDnnf, fLit)

        mkDOr(tDAnd, fDAnd)
    }

    override fun serializeGiantOr(a: Ser) {
        val token = getPosComplexOpToken(a)
        a.append(token)
        a.append(PLConstants.LPAREN)
        a.newLine()

        val argsCopy = argIt()
        //        argsCopy = ExpComparator.sortCopy(args);

        val it = argsCopy.iterator()
        while (it.hasNext()) {
            val arg = it.next()
            a.append("  ")
            arg.serialize(a)
            a.newLine()
        }

        a.append(PLConstants.RPAREN)
    }

    override fun decide(): Var {
                return vars.getFirstVar()
    }

    override fun condition(lit: Lit): Exp {
                return if (!containsVar(lit)) {
                        this
        } else {
                        val after = argBuilder(op()).addExpArray(args, Condition.fromLit(lit)).mk()
            assert(this !== after)
            after
        }
    }

    override fun condition(cube: Cube): Exp {
                return if (!anyVarOverlap(cube)) {
                        this
        } else {
                        argBuilder(op())
                    .addExpArray(args, Condition.fromCube(cube))
                    .mk()
        }
    }

    override fun asPosComplex(): PosComplexMultiVar {
        return this
    }


    override fun asOr(): Or {
        return this
    }

    override fun isOr(): Boolean {
        return true
    }


    override fun conditionVV(vv: Exp): Exp {
                if (vv === this) return vv

        if (!this.anyVarOverlap(vv)) {
            return this
        }

        val vvVars = vv.vars
        val it = vvVars.intIterator()
        val var1 = it.next()
        val var2 = it.next()

        val skip: Exp? = null

        if (containsLocalLitWithVar(var1, var2)) {
            val ll = containsVVArgs(vv)

            if (ll != null) {
                if (ll.tt()) {
                    return vv
                }
                //                else if (ll.ft()) {
                //                    skip = vv.arg1().flip();
                //                } else if (ll.tf()) {
                //                    skip = vv.arg2().flip();
                //                }
            }

        }

        //todo
        //2 bugs here:
        //  UNFIXED: 1. if !containsLocalLitWithVar it shouldn'tCon try to create a new Exp
        //  FIXED:   2. if space is allowing dup expressions (much bigger bug)

        val a = ArgBuilder(_space, Op.Or)
        val argIt = argIter()
        while (argIt.hasNext() && !a.isShortCircuit) {
            val e = argIt.next()
            if (e !== skip) {
                val v = e.conditionVV(vv)
                a.addExp(v)
            }
        }
        return a.mk()

    }


    fun canVVSimplifyLocal(ll: PosComplexMultiVar.LL?): Boolean {
        if (ll == null) return false
        if (ll.tt()) {
            return true
        } else if (ll.ft()) {
            return true
        } else if (ll.tf()) {
            return true
        }
        return false
    }


    override fun getPosOp(): PLConstants.PosOp {
        return OP
    }


    override fun getOp(): Op {
        return Op.Or
    }

    override fun isNegationNormalForm(): Boolean {
                for (arg in args()) {
            if (!arg.isNegationNormalForm) {
                return false
            }
        }
        return true
    }


    fun createEquivAnd(): Exp {
                val hardFlip = createHardFlip()
        return hardFlip.flip()
    }

    override fun createHardFlip(): Exp {
                val b = flipArgs()
        return b.mk()
        //        return getSpace().mkAnd(b);
    }

    override fun flipArgs(): ArgBuilder {
                return ArgBuilder(_space, Op.And, argItFlipped())
    }


    override fun computeIsSat(): Boolean {
                val decisionVar = decide()
        val tCon = condition(decisionVar.pLit())


        val tSat = tCon.isSat
        if (tSat) return true


        val fCon = condition(decisionVar.nLit())
        return fCon.isSat
    }

    override fun pushNotsIn(): Exp {
                val b = ArgBuilder(_space, op())
        for (arg in args) {
            val s = arg.pushNotsIn()
            b.addExp(s)
        }
        return b.mk()
    }

    override fun satCountPL(): Long {
                val decisionVar = decide()

        val tCon: Exp = condition(decisionVar.pLit())
        val fCon: Exp = condition(decisionVar.nLit())

        val tSatCount = Csp.computeDcVars(tCon.satCountPL(), vars, tCon.vars.union(decisionVar))
        val fSatCount = Csp.computeDcVars(fCon.satCountPL(), vars, fCon.vars.union(decisionVar))
        return tSatCount + fSatCount

    }

    companion object {

        val OP: PLConstants.PosOp = PLConstants.PosOp.OR
    }


    //
    //    public long satCountPL(VarSet parentVars) {
    //        return KExp.satCountPL(this, parentVars);
    //    }


}
