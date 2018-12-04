package com.smartsoft.csp.ast

import com.google.common.collect.ImmutableSet
import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.argBuilder.IArgBuilder
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.util.Bit
import com.smartsoft.csp.util.DynComplex
import com.smartsoft.csp.util.it.ExpFn
import com.smartsoft.csp.util.it.ExpIt
import com.smartsoft.csp.util.it.Fn
import com.smartsoft.csp.util.it.It
import com.smartsoft.csp.varSet.VarSet

class ExpFactory(val space: Space) {

    val parser: Parser get() = space.parser

    fun mkNCube(vars: VarSet): Exp {
        val bb = argBuilder(Op.Cube)
        for (vr in vars) {
            val lit = vr.mkNegLit()
            bb.addExp(lit.asExp)
        }
        return bb.mk()
    }

    fun mkIff(args: Iterable<Exp>): Exp {
        val iterator = args.iterator()
        val arg1: Exp = iterator.next()
        val arg2: Exp = iterator.next()
        require(!iterator.hasNext())
        return mkBinaryIff(arg1, arg2)
    }

    fun mkImp(args: Iterable<Exp>): Exp {
        val iterator = args.iterator()
        val arg1: Exp = iterator.next()
        val arg2: Exp = iterator.next()
        require(!iterator.hasNext())
        return mkBinaryImp(arg1, arg2)
    }

    fun mkNand(args: Iterable<Exp>): Exp {
        val iterator = args.iterator()
        val arg1: Exp = iterator.next()
        val arg2: Exp = iterator.next()
        require(!iterator.hasNext())
        return mkBinaryNand(arg1, arg2)
    }

    fun mkBinaryIff(arg1: Exp, arg2: Exp): Exp {

        if (arg1 === arg2) {
            return space.mkTrue()
        }

        if (arg1.expId == arg2.expId) {
            System.err.println("arg1[$arg1]")
            System.err.println("arg2[$arg2]")
            System.err.println("arg1.expId[" + arg1.expId + "]")
            System.err.println("arg2.expId[" + arg2.expId + "]")
            throw IllegalStateException()
        }

        assert(arg1.expId != arg2.expId) { arg1.toString() + " = " + arg2 }

        if (arg1 === arg2.flip) {
            return space.mkFalse()
        }

        if (arg1.isFalse && arg2.isFalse) {
            return space.mkTrue()
        }

        if (arg1.isTrue && arg2.isTrue) {
            return space.mkTrue()
        }

        if (arg1.isFalse && arg2.isTrue) {
            return space.mkFalse()
        }

        if (arg1.isTrue && arg2.isFalse) {
            return space.mkFalse()
        }

        if (arg1.isTrue && arg2.isOpen) {
            return arg2
        }

        if (arg1.isOpen && arg2.isTrue) {
            return arg1
        }

        if (arg1.isFalse && arg2.isOpen) {
            return arg2.flip
        }

        if (arg1.isOpen && arg2.isFalse) {
            return arg1.flip
        }

        assert(arg1.isOpen && arg2.isOpen)
        assert(arg1 !== arg2)
        assert(arg1.flip !== arg2)
        assert(!arg1.isConstant)
        assert(!arg2.isConstant)

        val imp1 = mkBinaryImp(arg1, arg2)
        val imp2 = mkBinaryImp(arg2, arg1)

        return mCube(imp1, imp2)

    }


    fun mkBinaryImp(lhs: Exp, rhs: Exp): Exp {
        return mkBinaryOr(lhs.flip, rhs);
    }

    fun mkBinaryNand(a1: Exp, a2: Exp): Exp {
        val arg1 = a1.flip
        val arg2 = a2.flip
        return mkBinaryOr(arg1, arg2);
    }

    fun mkXor(vararg args: Exp, condition: ConditionOn): Exp {
        val b = this.argBuilder(Op.Xor)
        for (arg1 in args) {
            val arg2 = arg1.condition(condition)
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

//    @JvmOverloads
//    fun mkExp(op: Op = Op.And, vararg args: Exp, condition: Condition = Condition.identity): Exp {
//        return argBuilder(op).addExpArray(args, condition).mk()
//    }

    @JvmOverloads
    fun mkAnd(args: ExpIt = It.emptyIt(), condition: Condition = Condition.identity, flatten: Boolean = true): Exp {
        val bb = ArgBuilder(space, Op.And, flatten = flatten)
        bb.addExpIt(args, condition)
        return bb.mk()
    }

    @JvmOverloads
    fun mCube(vararg args: Exp, condition: Condition = Condition.identity): Exp {
        return argBuilder(Op.Cube).addExpArray(args, condition).mk()
    }

    @JvmOverloads
    fun mkOr(args: ExpIt = It.emptyIt(), condition: Condition = Condition.identity, flatten: Boolean = true): Exp {
        val bb = ArgBuilder(space, Op.Or, flatten = flatten)
        bb.addExpIt(args, condition)
        return bb.mk()
    }

    @JvmOverloads
    fun mkOr(vararg args: Exp, condition: Condition = Condition.identity, flatten: Boolean = true): Exp {
        val bb = ArgBuilder(space, Op.Or, flatten = flatten)
        bb.addExpArray(args, condition)
        return bb.mk()
    }

    fun andBuilder(): ArgBuilder {
        return argBuilder(Op.And)
    }


    fun orBuilder(): ArgBuilder {
        return argBuilder(Op.Or)
    }

    fun cubeBuilder(): ArgBuilder {
        return argBuilder(Op.Cube)
    }

    fun xorBuilder(): ArgBuilder {
        return argBuilder(Op.Xor)
    }

    fun argBuilder(op: Op): ArgBuilder {
        require(op.isAndLike || op.isOrLike || op.isXor)
        return ArgBuilder(space, op)
    }

    fun argBuilder(op: Op, args: ExpIt): ArgBuilder {
        require(op.isAndLike || op.isOrLike || op.isXor)
        return ArgBuilder(space, op, args)
    }

    fun argBuilder(op: Op, cube: Cube): ArgBuilder {
        require(op.isAndLike || op.isOrLike || op.isXor)
        return ArgBuilder(space, op, cube.argIt())
    }

    @JvmOverloads
    fun argBuilderPair(op: Op = Op.And, arg1: Exp, arg2: Exp): ArgBuilder {
        return ArgBuilder(space, op).addExp(arg1).addExp(arg2);
    }

    @JvmOverloads
    fun mkPair(op: Op = Op.And, arg1: Exp, arg2: Exp): Exp {
        return argBuilderPair(op = op, arg1 = arg1, arg2 = arg2).mk()
    }

    @JvmOverloads
    fun mkPosComplex(op: PLConstants.PosOp, args: Iterable<Exp>, flatten: Boolean = true): Exp {
        return when (op) {
            PLConstants.PosOp.AND -> mkAnd(args, flatten = flatten)
            PLConstants.PosOp.OR -> mkOr(args, flatten = flatten)
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
        return exp.asLit.vr.mkDcOr()
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

//
//    @JvmOverloads
//    fun mkCubePair(arg1: Exp, arg2: Exp): Exp {
//        return mkPair(op = Op.Cube, arg1 = arg1, arg2 = arg2)
//    }

    fun mkCubeExp(args: VarSet, sign: Boolean): Exp {
        val bb = argBuilder(Op.Cube)
        args.recomputeSize()
        bb.addVarSet(args, sign)
        return bb.mk()
    }

//    @JvmOverloads
//    fun mkPCube(vars: VarSet): Exp = mkCubeExp(vars, true)


    fun mkCubeExp(vars: VarSet, trueVars: VarSet): Exp {
        val cube = DynCube(space, vars, trueVars);

        val exp: Exp = cube.mk()

        return exp
    }

    fun mkCubeExp(c: DynCube?, f: DynComplex?): Exp {
        return if (c.isNullOrEmpty && f.isNullOrEmpty) space.mkTrue()
        else if (c.isNullOrEmpty) f!!.mk()
        else if (f.isNullOrEmpty) c!!.mk()
        else {

            //both c and fCon are now both "open"

            val cc = c!!.mk()
            val ff = f!!.mkFormula()

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
            space.mkFalse()
        } else if (arg1 === arg2) {
            arg1
        } else if (arg1 === arg2.flip) {
            space.mkFalse()
        } else if (arg1.isTrue) {
            arg2
        } else if (arg2.isTrue) {
            arg1
        } else {
            argBuilder(Op.And).addExp(arg1).addExp(arg2).mk()
        }

    }


    fun intersection(vars1: VarSet, vars2: VarSet): VarSet {
        return vars1.overlap(vars2)
    }


    /**
     * Args are already known to be disjoint and dnnf
     */
    fun mkDAnd(lit: Lit, f: Exp): Exp {
        return when (f) {
            is False -> space.mkFalse();
            is True -> lit
            is Lit -> mkDCubeLitPair(lit, f)
            is Cube -> mkDAnd(lit, f.asCubeExp)
            else -> argBuilder(Op.DAnd).addExp(lit).addExp(f).mk()
        }
    }

    /**
     * Args are already known to be disjoint and dnnf
     */
    fun mkDAnd(cube: CubeExp, f: Exp): Exp {
        return when (f) {
            is False -> space.mkFalse();
            is True -> cube
            is Lit -> mkDAnd(f.asLit, cube)
            is CubeExp -> mkDAnd(cube, f.asCubeExp)
            else -> argBuilder(Op.DAnd).addExp(cube).addExp(f).mk()
        }
    }

    /**
     * Args are already known to be disjoint and dnnf
     */
    fun mkDAnd(cube1: CubeExp, cube2: CubeExp): Exp {
        return CubeCubeDAndBuilder(cube1, cube2).mk()
    }


    /**
     * Args are already known to be disjoint
     */
    fun mkDAnd(arg1: Lit, arg2: CubeExp): Exp {
        return LitCubeDAndBuilder(arg1, arg2).mk()
    }

    /**
     * Args are already known to be disjoint
     */
    fun mkDCubeLitPair(arg1: Lit, arg2: Lit): Exp {
        return LitPairDCubeBuilder(arg1, arg2).mk()
    }

    /**
     * Args are already known to be disjoint
     */
    fun mkLitPair(op: Op, arg1: Lit, arg2: Lit): Exp {
        return LitPairBuilder(op, arg1, arg2).mk()
    }

    fun mkOrLitPair(arg1: Lit, arg2: Lit): Exp {
        return when {
            arg1 === arg2 -> arg1
            arg1.sameVar(arg2) -> {
                if (arg1.sameSign(arg2)) {
                    throw IllegalStateException("should have been ===");
                } else {
                    mkTrue()
                }
            }
            else -> LitPairBuilder(Op.Or, arg1, arg2).mk()
        }

    }


    /**
     * Args are already known to be disjoint and dnnf
     */
    fun mkDAnd(c: Exp, f: Exp): Exp {
        return if (c.isFalse || f.isFalse) {
            space.mkFalse()
        } else if (c.isTrue) {
            f
        } else if (f.isTrue) {
            c
        } else if (c === f) {
            c
        } else if (f.hasFlip && c == f.flip) {
            space.mkFalse()
        } else if (c.isLit) {
            mkDAnd(c.asLit, f)
        } else if (f.isLit) {
            mkDAnd(f.asLit, c)
        } else {
            val b = argBuilder(Op.DAnd)
            b.addExp(c)
            b.addExp(f)
            return b.mk();
        }

    }


    fun intersection(e1: Exp, e2: Exp): VarSet {
        val vars1 = e1.vars
        val vars2 = e2.vars
        return intersection(vars1, vars2)
    }

    fun mkBinaryOr(arg1: Exp, arg2: Exp): Exp {
        if (arg1.isTrue || arg2.isTrue) {
            return space.mkTrue()
        }

        if (arg1 === arg2) {
            return arg1
        }

        if (arg1 === arg2.flip) {
            return space.mkTrue()
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
//        val _complexVars = raw.buildVarSet(extractVars)
//        val dLines = raw.dLines
//        val exp = parseDNodes(dLines)
//        val _complexVars = raw._complexVars
//        raw.invVars
//        return Dnnf(exp)
//    }


    fun computeCubesForXor(xor: Xor): Set<Cube> {
        val bb = ImmutableSet.builder<Cube>()
        val litSet = xor.args.map { it.asLit }.toSet()
        for (lit in litSet) {
            val others = litSet.minus(lit)
            val cc: DynCube = DynCube(space, lit)
            for (other in others) cc.assignSafe(other.flip.asLit)
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
                ss.isFalse -> space.mkFalse()
                ss.isTrue -> mkCubeExp(cube)
                ss.isLit -> mkCube(cube, ss.asLit, Bit.TRUE)
                else -> throw IllegalStateException()
            }
        } else {
            argBuilder(Op.And, cube).addExp(ss).mk()
        }

    }

    fun mkFalse() = space.mkFalse()
    fun mkTrue() = space.mkTrue()

    fun mkDOr(arg1: Exp, arg2: Exp): Exp {
        assert(arg1.isDnnf)
        assert(arg2.isDnnf)
        return if (arg1.isTrue || arg2.isTrue) {
            space.mkTrue()
        } else if (arg1.isFalse) {
            arg2
        } else if (arg2.isFalse) {
            arg1
        } else if (arg1 == arg2) {
            arg1
        } else if (arg1.flip == arg2) {
            space.mkTrue()
        } else {
            argBuilder(Op.DOr).addExp(arg1).addExp(arg2).mk()
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
                condition.isFalse -> space.mkFalse()
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

    data class ExpMinMax(val min: Exp, val max: Exp) {

        fun createExpArray(): Array<Exp> {
            return arrayOf(min, max)
        }

        companion object {
            @JvmStatic
            fun mk(e1: Exp, e2: Exp): ExpMinMax {
                return if (e1.expId < e2.expId) {
                    ExpMinMax(e1, e2)
                } else {
                    ExpMinMax(e2, e1)
                }
            }

            @JvmStatic
            fun mkArray(e1: Exp, e2: Exp): Array<Exp> {
                return mk(e1, e2).createExpArray()
            }

        }


    }


    fun mkCsp(): Csp {
        return Csp(space);
    }


}

val Cube?.isNullOrEmpty: Boolean get() = this == null || this.isEmpty
val List<Exp>?.isNullOrEmpty: Boolean get() = this == null || this.isEmpty()


//class LitPairCubeBuilder(val arg1: Lit, val arg2: Lit) : IArgBuilder {
//
//    init {
//        assert(op.isCube || op.isOrLike)
//    }
//
//    val a: Array<Exp> = ExpFactory.MinMax.mkArray(arg1, arg2)
//    val space: Space = arg1.space
//
//    override val isFcc: Boolean? get() = null
//    override val size: Int get() = 2
//    override val op: Op get() = Op.Cube
//    override val argIt: Iterable<Exp> get() = a.asIterable()
//
//    override fun mk(): Exp {
//
//        return if (arg1.sameVar(arg2)) {
//            if (arg1.sameSign(arg2)) {
//                arg1
//            } else {
//                space.mkFalse()
//            }
//        } else {
//            space.mkPosComplex(this);
//        }
//    }
//
//    override fun createExpArray(): Array<Exp> = a
//}
//

/**
 * Args are already known to be disjoint
 */
class LitPairDCubeBuilder(val arg1: Lit, val arg2: Lit) : IArgBuilder {

//    init {
//        assert(arg1.vr != arg2.vr)
//    }

    val a: Array<Exp> = ExpFactory.ExpMinMax.mkArray(arg1, arg2)
    val space: Space = arg1.space

    override val fcc: FccState get() = Open()
    override val size: Int get() = 2
    override val op: Op get() = Op.Cube
    override val argIt: Iterable<Exp> get() = a.asIterable()
    override fun mk(): Exp = space.mkPosComplex(this);
    override fun createExpArray(): Array<Exp> = a
}

/**
 * Args are already known to be disjoint
 */
class LitPairBuilder(override val op: Op, val arg1: Lit, val arg2: Lit) : IArgBuilder {

//    init {
//        assert(arg1.vr != arg2.vr)
//    }

    val a: Array<Exp> = ExpFactory.ExpMinMax.mkArray(arg1, arg2)
    val space: Space = arg1.space

    override val fcc: FccState get() = Open()
    override val size: Int get() = 2
    override val argIt: Iterable<Exp> get() = a.asIterable()
    override fun mk(): Exp = space.mkPosComplex(this);
    override fun createExpArray(): Array<Exp> = a
}


/**
 * Args are already known to be disjoint
 */
class LitCubeDAndBuilder(val lit: Lit, val cube: CubeExp) : IArgBuilder {

//    init {
//        assert(!cube.containsVar(lit.vr))
//    }

    val a: Array<Exp> = ExpFactory.ExpMinMax.mkArray(lit, cube)
    val space: Space = lit.space

    override val fcc: FccState get() = Open()
    override val size: Int get() = 2
    override val op: Op get() = Op.DAnd
    override val argIt: Iterable<Exp> get() = a.asIterable()
    override fun mk(): Exp = space.mkPosComplex(this);
    override fun createExpArray(): Array<Exp> = a
}

/**
 * Args are already known to be disjoint
 */
class CubeCubeDAndBuilder(val cube1: CubeExp, val cube2: CubeExp) : IArgBuilder {

    init {
//        assert(cube1.isVarDisjoint(cube2.vars))
    }

    val a: Array<Exp> = ExpFactory.ExpMinMax.mkArray(cube1, cube2)
    val space: Space = cube2.space

    override val fcc: FccState get() = Open()
    override val size: Int get() = 2
    override val op: Op get() = Op.DAnd
    override val argIt: Iterable<Exp> get() = a.asIterable()
    override fun mk(): Exp = space.mkPosComplex(this);
    override fun createExpArray(): Array<Exp> = a
}

