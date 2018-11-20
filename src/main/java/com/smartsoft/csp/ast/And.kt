package com.smartsoft.csp.ast

import com.google.common.collect.ImmutableSet
import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.ast.PLConstants.PosOp
import com.smartsoft.csp.ast.formula.FConstraintSet
import com.smartsoft.csp.fm.dnnf.products.Cube
import com.smartsoft.csp.util.UnionFind

open class And(space: Space, expId: Int, fixedArgs: Array<Exp>) : PosComplexMultiVar(space, expId, fixedArgs), FConstraintSet {


    override val constraintCount: Int
        get() = argCount

    val isDisjoint: Boolean
        get() = checkDisjointConjuncts(false)


    override fun computeIsSat(): Boolean {
        if(true) throw IllegalStateException()
        return if (isCube) {
            true
        } else {
            //convert and to csp
            Csp(this).isSat()
        }
    }


    //    and(x y z)  cond or(x y)   => adds nothing
    //    and(x y z)  cond or(!x y)  => skip y
    //    and(x y z)  cond or(x !y)  => y
    //    and(x y z)  cond or(!x !y) => false
    override fun conditionVV(vv: Exp): Exp {
        if (!anyVarOverlap(vv)) {
            return this
        }

        val vars = vv.vars
        val it = vars.intIterator()
        val var1 = it.next()
        val var2 = it.next()

        var skip: Exp? = null

        if (containsLocalLitWithVar(var1, var2)) {
            val ll = containsVVArgs(vv)

            if (ll != null) {
                if (ll.ft()) {
                    skip = vv.arg2
                } else if (ll.tf()) {
                    skip = vv.arg1
                } else if (ll.ff()) {
                    return mkFalse()
                }
            }

        }

        val and = this.op  //could be And or DAnd
        val a = ArgBuilder(_space, and)
        val aIt = argIter
        while (aIt.hasNext()) {

            if (a.isShortCircuit) {
                break
            }
            val e = aIt.next()
            if (e !== skip) {
                val v = e.conditionVV(vv)
                a.addExp(v)
            }
        }

        return a.mk()

    }

    override fun computeUnionFind(): UnionFind {
        val unionFind = UnionFind(this)
        unionFind.processAllUniquePairs()
        return unionFind
    }

    override fun createHardFlip(): Exp {
        val argBuilder = flipArgs()
        return argBuilder.mk()
    }

    override fun flipArgs(): ArgBuilder {
        val b = argBuilder(Op.Or)
        for (arg in args) {
            b.addExp(arg.flip)
        }
        return b
    }

    fun canVVSimplifyLocal(ll: PosComplexMultiVar.LL?): Boolean {
        if (ll == null) return false
        return if (ll.ft()) {
            true
        } else if (ll.tf()) {
            true
        } else if (ll.ff()) {
            true
        } else {
            false
        }
    }


    override val isAndOfClauses: Boolean get() = isAllClauses

    override val isNegationNormalForm: Boolean
        get() {
            for (arg in args) {
                if (!arg.isNegationNormalForm) {
                    return false
                }
            }
            return true
        }


    override val posOp: PosOp get() = OP

    override val op: Op get() = Op.And


    override fun flatten(): Exp {


        if (isFlat) {
            return this
        }

        val b = ImmutableSet.builder<Exp>()
        for (arg in args) {
            val argf = arg.flatten()
            if (argf.isAnd) {
                for (aa in arg.args) {
                    val aaf = aa.flatten()
                    b.add(aaf)
                }
            } else {
                b.add(arg)
            }
        }

        val retVal = b.build()

        for (arg in retVal) {
            assert(!arg.isNested(this))
        }

        return space.mkAnd(retVal)
    }


    override fun pushNotsIn(): Exp {
        val b = ArgBuilder(_space, op())
        for (arg in args) {
            val s = arg.pushNotsIn()
            b.addExp(s)
        }
        return b.mk()
    }

    override fun isDirectlyRelated(c1: Int, c2: Int): Boolean {
        val arg1 = getArg(c1)
        val arg2 = getArg(c2)
        return arg1.anyVarOverlap(arg2)
    }

    override fun getFirstConjunctContaining(varCode: String): Exp? {
        for (arg in args) {
            if (arg.containsVar(varCode)) {
                return arg
            }
        }
        return null
    }

    @JvmOverloads
    fun checkDisjointConjuncts(logging: Boolean = true): Boolean {
        val v = _space.newMutableVarSet()
        for (arg in args) {
            for (argVar in arg.varIt()) {
                val varId = argVar.vrId
                val added = v.addVarId(varId)
                if (!added) {
                    if (logging) {
                        System.err.println("args are not disjoint: ")
                        System.err.println("    Var[$argVar] appears formula more than one conjunct")
                        System.err.println("  " + this)
                    }
                    return false
                }
            }
        }
        return true
    }


    companion object {

        val OP: PosOp = PosOp.AND
    }

    override fun condition(lit: Lit): Exp {
        return if (!anyVarOverlap(lit)) {
            this
        } else {
            val b = argBuilder(op()).addExpArray(_args, lit)
            b.mk()
        }
    }

    override fun condition(ctx: Cube): Exp {
        return if (!anyVarOverlap(ctx)) {
            this
        } else {
            val b = argBuilder(op()).addExpArray(_args, ctx)
            b.mk()
        }
    }

    override fun toDnnf(): Exp {
        return when {
            size == 0 -> {
                throw IllegalStateException("PosComplex disallows empty")
            }
            size == 1 -> {
                arg.toDnnf()
            }
            isAllLits -> {
                assert(size > 1)
                space.mkCube(argIt)
            }
            isAllComplex -> {
                assert(size > 1)
                val f = space.mkFormula(argIt)
                f.toDnnf()
            }
            isAllConstants -> {
                throw UnsupportedOperationException("all constants: " + javaClass.getName())
            }
            else -> {
//                val csp = Add.mixed(this).mkCsp()
                val csp = Csp(mixed = this)
                csp.toDnnf()
                //            throw new UnsupportedOperationException(getClass().getName() + ": " + getContentModel() + ":" + toString());
            }
        }


    }
}
