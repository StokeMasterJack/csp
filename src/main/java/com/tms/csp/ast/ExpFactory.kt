package com.tms.csp.ast

import com.google.common.collect.ImmutableSet
import com.tms.csp.*
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.argBuilder.IArgBuilder
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.Bit
import com.tms.csp.util.DynComplex
import com.tms.csp.util.varSets.VarSet

class ExpFactory(val sp: Space) {

    val _space: Space = sp
    val parser: Parser get() = sp.parser

    @JvmOverloads
    fun mkNCube(vars: VarSet): Exp {
        val bb = argBuilder(Op.Cube)
        for (vr in vars) {
            val lit = vr.mkNegLit()
            bb.addExp(lit.asExp())
        }
        return bb.mk()
    }

    @JvmOverloads
    fun mkIff(args: Iterable<Exp>): Exp {
        val iterator = args.iterator()
        val arg1: Exp = iterator.next()
        val arg2: Exp = iterator.next()
        require(!iterator.hasNext())
        return mkBinaryIff(arg1, arg2)
    }

    @JvmOverloads
    fun mkImp(args: Iterable<Exp>): Exp {
        val iterator = args.iterator()
        val arg1: Exp = iterator.next()
        val arg2: Exp = iterator.next()
        require(!iterator.hasNext())
        return mkBinaryImp(arg1, arg2)
    }

    @JvmOverloads
    fun mkNand(args: Iterable<Exp>): Exp {
        val iterator = args.iterator()
        val arg1: Exp = iterator.next()
        val arg2: Exp = iterator.next()
        require(!iterator.hasNext())
        return mkBinaryNand(arg1, arg2)
    }

    @JvmOverloads
    fun mkBinaryIff(arg1: Exp, arg2: Exp): Exp {

        if (arg1 === arg2) {
            return sp.mkTrue()
        }

        if (arg1.getExpId() == arg2.getExpId()) {
            System.err.println("arg1[$arg1]")
            System.err.println("arg2[$arg2]")
            System.err.println("arg1.getExpId()[" + arg1.getExpId() + "]")
            System.err.println("arg2.getExpId()[" + arg2.getExpId() + "]")
            throw IllegalStateException()
        }

        assert(arg1.getExpId() != arg2.getExpId()) { arg1.toString() + " = " + arg2 }

        if (arg1 === arg2.flip()) {
            return sp.mkFalse()
        }

        if (arg1.isFalse && arg2.isFalse) {
            return sp.mkTrue()
        }

        if (arg1.isTrue && arg2.isTrue) {
            return sp.mkTrue()
        }

        if (arg1.isFalse && arg2.isTrue) {
            return sp.mkFalse()
        }

        if (arg1.isTrue && arg2.isFalse) {
            return sp.mkFalse()
        }

        if (arg1.isTrue && arg2.isOpen) {
            return arg2
        }

        if (arg1.isOpen && arg2.isTrue) {
            return arg1
        }

        if (arg1.isFalse && arg2.isOpen) {
            return arg2.flip()
        }

        if (arg1.isOpen && arg2.isFalse) {
            return arg1.flip()
        }

        assert(arg1.isOpen && arg2.isOpen)
        assert(arg1 !== arg2)
        assert(arg1.flip() !== arg2)
        assert(!arg1.isConstant)
        assert(!arg2.isConstant)

        val imp1 = mkBinaryImp(arg1, arg2)
        val imp2 = mkBinaryImp(arg2, arg1)

        return mCube(imp1, imp2)

    }


    @JvmOverloads
    fun mkBinaryImp(lhs: Exp, rhs: Exp): Exp {
        return mkBinaryOr(lhs.flip(), rhs);
    }

    @JvmOverloads
    fun mkBinaryNand(a1: Exp, a2: Exp): Exp {
        val arg1 = a1.flip()
        val arg2 = a2.flip()
        return mkBinaryOr(arg1, arg2);
    }

    @JvmOverloads
    fun mkXor(vararg args: Exp, condition: Condition = Condition.identity): Exp {
        val b = this.argBuilder(Op.Xor)
        for (arg1 in args) {
            val arg2 = condition.condition(arg1)
            b.addExp(arg2);
        }
        return b.mk()
    }

    @JvmOverloads
    fun mkXor(args: ExpIt = It.emptyIt(), condition: Condition = Condition.identity): Exp {
        require(Exp.isAllLits(args))
        val b = xorBuilder()
        for (arg1 in args) {
            val arg2 = condition.condition(arg1)
            b.addExp(arg2);
        }
        return b.mk();
    }

    @JvmOverloads
    fun mkXor(args: VarSet, condition: Condition = Condition.identity): Exp {
        return argBuilder(Op.Xor).addVarSet(args, true, condition).mk()
    }

    @JvmOverloads
    fun mkExp(op: Op = Op.And, args: ExpIt = It.emptyIt(), condition: Condition = Condition.identity): Exp {
        return argBuilder(op).addExpIt(args, condition).mk()
    }

    @JvmOverloads
    fun mkExp(op: Op = Op.And, vararg args: Exp, condition: Condition = Condition.identity): Exp {
        return argBuilder(op).addExpArray(args, condition).mk()
    }

    @JvmOverloads
    fun mkCubeExp(args: ExpIt = It.emptyIt(), condition: Condition = Condition.identity): Exp {
        return andBuilder().addExpIt(args, condition).mk()
    }

    @JvmOverloads
    fun mCube(vararg args: Exp, condition: Condition = Condition.identity): Exp {
        return argBuilder(Op.Cube).addExpArray(args, condition).mk()
    }

    @JvmOverloads
    fun mkOr(args: ExpIt = It.emptyIt(), condition: Condition = Condition.identity): Exp {
        return orBuilder().addExpIt(args, condition).mk()
    }

    @JvmOverloads
    fun mkOr(vararg args: Exp, condition: Condition = Condition.identity): Exp {
        return argBuilder(Op.Or).addExpArray(args, condition).mk()
    }


    @JvmOverloads
    fun andBuilder(): ArgBuilder {
        return argBuilder(Op.And)
    }


    @JvmOverloads
    fun orBuilder(): ArgBuilder {
        return argBuilder(Op.Or)
    }

    @JvmOverloads
    fun cubeBuilder(): ArgBuilder {
        return argBuilder(Op.Cube)
    }

    @JvmOverloads
    fun xorBuilder(): ArgBuilder {
        return argBuilder(Op.Xor)
    }


    @JvmOverloads
    fun argBuilder(op: Op): ArgBuilder {
        require(op.isAndLike || op.isOrLike || op.isXor)
        return ArgBuilder(sp, op)
    }


    @JvmOverloads
    fun argBuilder(op: Op, args: ExpIt): ArgBuilder {
        require(op.isAndLike || op.isOrLike || op.isXor)
        return ArgBuilder(sp, op, args)
    }


    @JvmOverloads
    fun argBuilder(op: Op, cube: Cube): ArgBuilder {
        require(op.isAndLike || op.isOrLike || op.isXor)
        return ArgBuilder(sp, op, cube.argIt())
    }

    @JvmOverloads
    fun argBuilderPair(op: Op = Op.And, arg1: Exp, arg2: Exp): ArgBuilder {
        return ArgBuilder(sp, op).addExp(arg1).addExp(arg2);
    }

    @JvmOverloads
    fun mkPair(op: Op = Op.And, arg1: Exp, arg2: Exp): Exp {
        return argBuilderPair(op = op, arg1 = arg1, arg2 = arg2).mk()
    }

    fun mkPosComplex(op: PLConstants.PosOp, args: Iterable<Exp>): Exp {
        return when (op) {
            PLConstants.PosOp.AND -> mkCubeExp(args)
            PLConstants.PosOp.OR -> mkOr(args)
            PLConstants.PosOp.XOR -> mkXor(args)
            PLConstants.PosOp.IFF -> mkIff(args)
            PLConstants.PosOp.IMP -> mkImp(args)
            PLConstants.PosOp.NAND -> mkNand(args)
            PLConstants.PosOp.DC -> mkDontCare(args)
            else -> throw IllegalArgumentException(op.toString() + "")
        }

    }

    private fun mkDontCare(args: Iterable<Exp>): Exp {
        val it = args.iterator()
        assert(it.hasNext())
        val exp = it.next()
        assert(exp.isPosLit)
        assert(!it.hasNext())
        return exp.asLit().vr.mkDcOr()
    }


    @JvmOverloads
    fun mkCubeExp(vararg args: Exp, f: ExpFn = Fn.identity): Exp {
        assert(Exp.isAllLits(args))
        val b = cubeBuilder()
        for (arg in args) {
            b.addExp(f(arg))
        }
        return b.mk()
    }

    @JvmOverloads
    fun mkCubeExp(cube: Cube, condition: Condition = Condition.identity): Exp {
        return argBuilder(Op.Cube).addCube(cube, condition).mk()
    }


    @JvmOverloads
    fun mkCubeLitPair(arg1: Lit, arg2: Lit): Exp = LitPairCubeBuilder(arg1, arg2).mk()

    @JvmOverloads
    fun mkCubePair(arg1: Exp, arg2: Exp): Exp {
        return mkPair(op = Op.Cube, arg1 = arg1, arg2 = arg2)
    }

    @JvmOverloads
    fun mkCubeExp(args: VarSet, sign: Boolean, condition: Condition = Condition.identity): Exp {
        return argBuilder(Op.Cube).addVarSet(args, sign, condition).mk()
    }

    @JvmOverloads
    fun mkPCube(vars: VarSet): Exp = mkCubeExp(vars, true)


    fun mkCubeExp(vars: VarSet, trueVars: VarSet): Exp {
        val cube: DynCube = DynCube(sp, vars, trueVars);

        val exp: Exp = cube.mk()

        return exp
    }

    fun mkCubeExp(c: DynCube?, f: DynComplex?): Exp {

        return if (c == null && f == null) {
            sp.mkTrue()
        } else if ((c == null || c.isConstantTrue)) {
            checkNotNull(f)
            f.mkExp()
        } else if (f == null || f.isConstantTrue) {
            checkNotNull(c)
            c.mkExp()
        } else if (c.isConstantFalse) {
            sp.mkFalse()
        } else if (f.isConstantFalse) {
            sp.mkFalse()
        } else {

            //both c and f are now both "open"

            val cc = c.mkCubeExp()
            val ff = f.mkFormula()

            mCube(cc, ff)
        }


    }


    /**
     * args should be all complex
     *
     * @param args
     * @return
     */
    @JvmOverloads
    fun mkFormula(args: Iterable<Exp>, condition: Condition = Condition.identity): Exp {
        assert(Exp.isAllComplex(args))
        return argBuilder(Op.Formula).addExpIt(args, condition).mk()
    }

    fun mkBinaryAnd(arg1: Exp, arg2: Exp): Exp {
        return if (arg1.isFalse || arg2.isFalse) {
            sp.mkFalse()
        } else if (arg1 === arg2) {
            arg1
        } else if (arg1 === arg2.flip()) {
            sp.mkFalse()
        } else if (arg1.isTrue) {
            arg2
        } else if (arg2.isFalse) {
            arg1
        } else if (arg1.isLit && arg2.isLit) {
            mkCubeLitPair(arg1.asLit(), arg2.asLit());
        } else if (arg1.isCube) {
            val cube: Cube = arg1.asCube()
            val pair = cube.split(arg2)
            val s2: Exp = arg2.condition(pair.intersection)
            if (s2.isLit) {
                mkCube(cube = cube, lit = s2.asLit(), disjoint = Bit.TRUE);
            } else {
                mkCubeExp(cube = cube, arg = s2, disjoint = Bit.TRUE)
            }
        } else if (arg2.isCube) {
            val cube: Cube = arg2.asCube()
            val pair = cube.split(arg1)
            val s1: Exp = arg1.condition(pair.intersection)
            if (s1.isLit) {
                mkCube(cube = cube, lit = s1.asLit(), disjoint = Bit.TRUE);
            } else {
                mkCubeExp(cube = cube, arg = s1, disjoint = Bit.TRUE)
            }
        } else {
            mCube(arg1, arg2)
        }


    }


    fun intersection(vars1: VarSet, vars2: VarSet): VarSet {
        return vars1.intersection(vars2)
    }


    fun intersection(e1: Exp, e2: Exp): VarSet {
        val vars1 = e1.vars
        val vars2 = e2.vars
        return intersection(vars1, vars2)
    }

    fun mkBinaryOr(arg1: Exp, arg2: Exp): Exp {
        if (arg1.isTrue || arg2.isTrue) {
            return sp.mkTrue()
        }

        if (arg1 === arg2) {
            return arg1
        }

        if (arg1 === arg2.flip()) {
            return sp.mkTrue()
        }

        if (arg1.isFalse) {
            return arg2
        }

        if (arg2.isFalse) {
            return arg1
        }


        return mkOr(arg1, arg2)
    }


//    fun mkCsp(constraints: Iterable<Exp>): Csp {
//        return Csp(space, constraints)
//    }


//    fun mkCsp2(sConstraints: Iterable<String>): Csp {
//        val lines: List<Exp> = space.parseExpressions2(sConstraints)
//        return mkCsp(lines)
//    }

//    fun mkDnnf(raw: Raw,extractVars: Boolean): Dnnf {
//        val vars = raw.buildVarSet(extractVars)
//        val dLines = raw.dLines
//        val exp = parseDNodes(dLines)
//        val vars = raw.vars
//        raw.invVars
//        return Dnnf(exp)
//    }


    fun computeCubesForXor(xor: Xor): Set<Cube> {
        val bb = ImmutableSet.builder<Cube>()
        val litSet = xor.args.map { it.asLit() }.toSet()
        for (lit in litSet) {
            val others = litSet.minus(lit)
            val cc: DynCube = DynCube(sp, lit)
            for (other in others) cc.assignSafe(other.flip().asLit())
            bb.add(cc)
        }
        return bb.build()
    }


    fun mkCubeExp(cube: Cube, arg: Exp, disjoint: Bit): Exp {
        val ss = if (disjoint.isFalseOrOpen) {
            arg.condition(cube)
        } else {
            arg
        }
        return if (ss != arg) {
            when {
                ss.isFalse -> sp.mkFalse()
                ss.isTrue -> mkCubeExp(cube)
                ss.isLit -> mkCube(cube, ss.asLit(), Bit.TRUE)
                else -> throw IllegalStateException()
            }
        } else {
            argBuilder(Op.And, cube).addExp(ss).mk()
        }

    }

    fun mkCube(cube: Cube, lit: Lit, disjoint: Bit): Exp {
        val condition = if (disjoint.isFalseOrOpen) {
            val ss = lit.condition(cube)
            assert(ss != lit)
            ss

        } else {
//            disjoint.isTrue
            lit
        }

        return if (condition != lit) {
            when {
                condition.isFalse -> sp.mkFalse()
                condition.isTrue -> mkCubeExp(cube)
                else -> throw IllegalStateException()
            }
        } else {
            argBuilder(Op.Cube, cube).addExp(lit).mk()
        }
    }


    companion object {
        @JvmStatic
        fun maybeSimplifyComplex(cube: Cube, cc: Exp, disjoint: Bit): Exp {
            return if (disjoint.isOpen || disjoint.isFalse) {
                cc.condition(cube)
            } else {
                cc;
            }
        }


        @JvmStatic
        fun createExpArray(size: Int, argIt: ExpIt): Array<Exp> {
            val aa: Array<Exp?> = arrayOfNulls(size)
            for ((i, arg) in argIt.withIndex()) {
                aa[i] = arg
            }
            return aa.requireNoNulls()
        }

    }

    data class MinMax(val min: Exp, val max: Exp) {

        fun createExpArray(): Array<Exp> {
            return arrayOf(min, max)
        }

        companion object {
            @JvmStatic
            fun mk(e1: Exp, e2: Exp): MinMax {
                return if (e1.expId < e2.expId) {
                    MinMax(e1, e2)
                } else {
                    MinMax(e2, e1)
                }
            }

            @JvmStatic
            fun mkArray(e1: Exp, e2: Exp): Array<Exp> {
                return mk(e1, e2).createExpArray()
            }

        }


    }


    fun mkCsp(): Csp {
        return Csp(_space);
    }


}

val Cube?.isNullOrEmpty: Boolean get() = this == null || this.isEmpty

class LitPairCubeBuilder(val arg1: Lit, val arg2: Lit) : IArgBuilder {

    init {
        assert(op.isCube || op.isOrLike)
    }

    val a: Array<Exp> = ExpFactory.MinMax.mkArray(arg1, arg2)
    val space: Space = arg1.sp

    override val isFcc: Boolean? get() = null
    override val size: Int get() = 2
    override val op: Op get() = Op.Cube
    override val argIt: Iterable<Exp> get() = a.asIterable()

    override fun mk(): Exp {

        return if (arg1.sameVar(arg2)) {
            if (arg1.sameSign(arg2)) {
                arg1
            } else {
                space.mkFalse()
            }
        } else {
            space.mkPosComplex(this);
        }
    }

    override fun createExpArray(): Array<Exp> = a
}



