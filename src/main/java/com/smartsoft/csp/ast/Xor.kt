package com.smartsoft.csp.ast

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.ImmutableSet
import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.dnnf.products.toStringDetail
import com.smartsoft.csp.util.Range
import com.smartsoft.csp.util.XorCube
import com.smartsoft.csp.varSet.VarSet
import java.math.BigInteger


class Xor(space: Space, expId: Int, args: Array<Exp>) : PosComplexMultiVar(space, expId, args), IXor {

    protected var _smooth: Exp? = null


    override val isDnnf: Boolean
        get() = true

    override//            Cube cube = arg.asLit.asCube;
    val cubesSmooth: Set<Cube>
        get() {
            val bb = ImmutableSet.builder<Cube>()
            for (arg in args) {
                assert(arg.isPosLit) { arg }
                val cube = XorCube(this, arg.vr)
                bb.add(cube)
            }
            return bb.build()
        }


    override val posOp: PLConstants.PosOp
        get() = OP


    override val op: Op
        get() = Op.Xor


    override val isSat: Boolean
        get() {

            if (isAllLits) {
                try {
                    DynCube(space, this.litItFromExpArray())
                    return true
                } catch (e: ConflictingAssignmentException) {
                    return false
                }

            }

            for (arg in argIt) {
                if (arg.isSat) {
                    return true
                }
            }

            return false
        }

    //    @Override
    //    public Set<Lit> getLits() {
    //        ImmutableSet.Builder<Lit> b = ImmutableSet.builder();
    //        for (Exp arg : args) {
    //            b.addAll(arg.getLits());
    //        }
    //        return b.build();
    //    }

    override val satCount: BigInteger
        get() = BigInteger.valueOf(argCount.toLong())

    override val cubeCount: Int
        get() = varCount


    //    @Override
    //    public boolean isLitMatched() {
    //        for (Exp arg : args) {
    //            if (!arg.isLitMatched()) {
    //                return false;
    //            }
    //        }
    //
    //        return true;
    //    }

    override val isSmooth: Boolean
        get() = false

    init {


        _args.forEach {
            assert(it.isPosLit)
            it.vr.xorParent = this
        }
    }

    override fun computeSat(lit: Lit): Boolean {
        assert(vars.size != 0)
        assert(vars.size != 1)
        assert(isAllLits)
        return true
    }

    override fun checkDnnf(): Boolean {
        return true
    }

    private fun conditionPosLit(vr: Var): Exp {
        val vs1 = vars
        val vs2 = vs1.minus(vr)
        assert(vs2.size == vs1.size - 1)
        return _space.mkNCube(vs2)
    }

    private fun conditionNegLit(vr: Var): Exp {
        val vs1 = vars
        val vs2 = vs1.minus(vr)
        if (vs2.size != vs1.size - 1) {
            println("$vs1 minus $vr")
            println(" vs1: ${vs1.size} $vs1")
            println(" vr: $vr")
            println(" vs2:  ${vs2.size} $vs2")
            throw AssertionError()
        }

        return _space.mkXor(vs2)
    }

    override fun condition(lit: Lit): Exp {
        val bContains = vars.containsVar(lit.vr)
        if (!bContains) {
            return this;
        }
        if (lit.toString() == "!MDL_6953" && vars.toString() == "XCOL_08S6 XCOL_0070 XCOL_0202 XCOL_0040 XCOL_01H1 XCOL_02KQ XCOL_08V1 XCOL_02KD XCOL_0058 XCOL_03L5 XCOL_03R0 XCOL_0218 XCOL_08T7 XCOL_0082 XCOL_01G3 XCOL_06T7 XCOL_06V2 XCOL_04T3 XCOL_0788 XCOL_04T8 XCOL_03P0 XCOL_08U6 XCOL_03R3 XCOL_03Q3 XCOL_08V5 XCOL_04V7 XCOL_01D6 XCOL_0781 XCOL_02KB XCOL_0785 XCOL_01E7") {
            println("contains: ${bContains}")
            println("  vars: ${vars.simpleName} $vars")
            println("  lit: ${lit}")
            println("  vr: ${lit.vr}")
            println("  varId: ${lit.varId}")

        }

        val ret = conditionInternal(lit)
        if (ret is Xor) {
            ret.score = this.score
        }
        return ret
    }

    private fun conditionInternal(lit: Lit): Exp {
        val vr = lit.vr
        val sign = lit.sign()
        return if (sign) {
            conditionPosLit(vr)
        } else {
            conditionNegLit(vr)
        }
    }

    override fun condition(ctx: Cube): Exp {
        val ret = conditionInternal(ctx)
        if (ret is Xor) {
            ret.score = this.score
        }
        return ret
    }

    private fun conditionInternal(ctx: Cube): Exp {
        if (isVarDisjoint(ctx)) {
            return this
        }


        var tVar: Var? = null

        var fCount = 0
        var oCount = 0

        assert(vars.size == _args.size)
        for (vr in vars) {
            val v = ctx.getValue(vr)

            if (v.isTrue) {
                if (tVar == null) {
                    tVar = vr
                } else {
                    //tCount>1 => return false
                    return mkFalse()
                }
            } else if (v.isFalse) {
                fCount++
                //skip
            } else {
                oCount++
            }
        }

        val tCount = argCount - (fCount + oCount)

        val tCount2 = if (tVar == null) 0 else 1
        assert(tCount == tCount2)

        assert(fCount + tCount + oCount == argCount)

        if (tCount > 1) {
            return mkFalse()
        }

        if (tCount == 1 && oCount == 0) {
            return mkTrue()
        }


        if (tVar == null) {
            //tCount = 0
            if (oCount == 0) {
                assert(fCount == vars.size)
                //all false
                return mkFalse()
            } else {
                //0 true, some opens
                assert(fCount > 0) {
                    "fCount > 0: ${this.toStringDetail()}  ${vars.toStringDetail()}   cube: ${ctx.toStringDetail()}"
                }
                val thisVars = vars
                val varsToRemove = ctx.vars
                val opens = thisVars.minus(varsToRemove)
                assert(opens.size < thisVars.size)
                return space.mkXor(opens)
            }
        } else {
            //tCount = 1
            if (oCount == 0) {
                //1 true, rest false => return true
                assert(tCount + fCount == vars.size)
                return mkTrue()
            } else {
                //1 true, some opens
                assert(oCount > 0)
                val thisVars = vars
                val varsToRemove = ctx.vars
                val opens = thisVars.minus(varsToRemove)
                return _space.mkNCube(opens)
            }

        }


    }


    fun toCnf(): Exp {
        return toOrAndConflicts()
    }


    fun toOrAndConflicts(): Exp {
        if (vars.size == 0) {
            throw IllegalStateException()
        } else if (vars.size == 1) {
            throw IllegalStateException()
        } else {
            val aa = ArgBuilder(_space, Op.And)
            val or = space.mkOr(vars)
            aa.addExp(or)
            getConflictsFromXorArgs(aa)
            return aa.mk()
        }
    }

    private fun getConflictsFromXorArgs(aa: ArgBuilder) {
        val range = Range(vars.size - 1)
        range.forEachPair { i, j ->
            val ai = vars.get(i)
            val aj = vars.get(j)
            val conflict = space.mkBinaryNand(ai, aj)
            aa.addExp(conflict)
        }
    }


    override fun litIterator(): Iterator<Lit> {
        return LitIterator(varIterator(), true)
    }

    //    final public void serialize(Ser a) {
    //        String token = "xor";
    //        a.append(token);
    //        a.append(LPAREN);
    //
    //        Iterator<Lit> it = litIterator();
    //        while (it.hasNext()) {
    //            Lit lit = it.next();
    //            lit.serialize(a);
    //            if (it.hasNext()) {
    //                a.argSep();
    //            }
    //        }
    //
    //        a.append(RPAREN);
    //    }

    override fun toDnnf(): Exp {
        return computeSmooth()
        //        return this;
    }


    override fun computeSatCount(): BigInteger {
        return BigInteger.valueOf(argCount.toLong())
    }


    override fun copyToOtherSpace(destSpace: Space): Exp {
        return if (space === destSpace) {
            this
        } else Exp.copyArgsExpToOtherSpace(destSpace, op, argIt)
    }


    override fun serializeTinyDnnf(a: Ser) {
        a.append('O')
        a.append(' ')
        serializeArgsTinyDnnf(a)
    }

    override fun project(outVars: VarSet): Exp {

        checkNotNull(outVars)

        val vars = this.vars
        if (outVars.containsAllVars(vars)) {
            return this
        }

        val b = ArgBuilder(_space, Op.Xor)


        for (i in 0 until _args.size) {
            val arg = _args[i]
            val s = arg.project(outVars)

            if (s.isTrue) {
                return mkTrue()
            } else if (s.isFalse) {
                //skip
            } else {
                assert(s.isOpen)
                b.addExp(s)

            }

        }

        return b.mk()
    }


    /**
     * replace unmatched lits with: lit or (!lit and false)
     */
    override fun litMatch(): Exp {
        val space = space

        val newArgs = ImmutableSet.builder<Exp>()

        for (arg in args) {
            val newArg = arg.litMatch()
            newArgs.add(newArg)
        }

        val retVal = space.mkDOr(newArgs.build())


        return retVal

    }

    override val smooth: Exp
        get() {
            if (_smooth == null) {
                _smooth = computeSmooth()
            }
            return _smooth!!
        }


    private fun computeSmooth(): Exp {
        val vars = vars
        val dOr = ArgBuilder(_space, Op.DOr)
        for (v1 in vars) {
            val dAnd = ArgBuilder(_space, Op.DAnd)
            for (v2 in vars) {
                dAnd.addExp(v2.mkLit(v2 === v1))
            }
            val and = dAnd.mk().asDAnd
            dOr.addExp(and)
        }


        val retVal = dOr.mk()
        assert(retVal.isSmooth) { retVal.toString() + " " + retVal.simpleName }
        return retVal
    }

    override fun computeCubesSmooth(): Set<Cube> {
        return smooth.computeCubesSmooth()
    }

    override fun computeCubesRough(): Set<Cube> {
        return _space.expFactory.computeCubesForXor(this)
    }


    override val satCountPL: Long
        get() {
            return argCount.toLong()
        }

    companion object {

        val OP: PLConstants.PosOp = PLConstants.PosOp.XOR
    }

    override val isXor: Boolean get() = true
    var score: Int = 0

    val isTop: Boolean
        get() {
            return prefix in space.topXorPrefixes
        }


}