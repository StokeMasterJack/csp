package com.smartsoft.csp.ast

import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import com.google.common.collect.Multimap
import com.smartsoft.csp.Vars
import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.data.CspSample
import com.smartsoft.csp.dnnf.Dnnf
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.ssutil.Console.prindent
import com.smartsoft.csp.transforms.Transformer
import com.smartsoft.csp.util.*
import com.smartsoft.csp.util.it.ExpFn
import com.smartsoft.csp.util.it.ExpFnJvm
import com.smartsoft.csp.util.it.Fn
import com.smartsoft.csp.util.it.It
import com.smartsoft.csp.varSet.EmptyVarSet
import com.smartsoft.csp.varSet.VarSet
import com.smartsoft.csp.varSet.VarSetBuilder
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList

/*
parent:Csp
space:Space

simple complex dontCares
    simple/complex disjoint: yes/no/unknown


action:
    int
    deciion-simple


possible actons:
    init
        mixed list of constraints
    decision (simpe or xor)
        level advances
        no mutation (evertthing copied)
        decision lit or cube
    propagate
        propagation lit
        level doen npt advances




 */

class Csp @JvmOverloads constructor(
        override val space: Space,
        val parent: Formula? = null,
        var simple: DynCube? = null,
        var complex: DComplex = DComplex(),
        var dontCares: VarSet? = null


) : HasSpace, HasIsSat {

    private var _failed: Boolean = false

    private var q: MutableList<Lit>? = null

    val parser: Parser get() = space.parser


    //initial:
    constructor(space: Space, constraints: Sequence<Exp>) : this(space = space) {
        addConstraintsExpSeq(constraints)
    }

    //initial:
    constructor(space: Space, constraints: Iterable<Exp>) : this(space = space) {
        addConstraintsExpIt(constraints)
    }

    //initial:
    constructor(space: Space, constraintsClob: String) : this(space = space) {
        val expSeq = space.parsePL(constraintsClob)
        addConstraintsExpSeq(expSeq)
    }

    constructor(mixed: And) : this(mixed.space, mixed.argIt) {
    }

    //simple or xor decision: formula - condition are disjoint
    constructor(formula: Iterable<Exp>, condition: ConditionOn) : this(space = condition.space) {
        _assignSafe(condition)
        addConstraintsExpIt(formula, condition)
        throw IllegalStateException()
    }

    //simple or xor decision: formula - condition are disjoint
    constructor(formula: Formula, condition: ConditionOn) : this(space = condition.space, parent = formula) {
        _assignSafe(condition)
        addConstraintsExpIt(formula.argIt, condition)
    }

    //copy
    constructor(csp: Csp) : this(space = csp.space, simple = csp.copySimple(), complex = csp.copyComplex(), dontCares = csp.copyDontCares()) {
    }


//    constructor(space:Space) : this(space = space)
//
//
//    constructor(c: Iterable<Exp>, cc: Lit) : this(Constraints(c), Condition(cc))
//    constructor(c: DynComplex, cc: Cube) : this(Constraints(c), Condition(cc))
//
//


    //constructor(mixed: And) : this(mixed.space, add = Add(c = mixed.complex, cc = mixed.simple))

    fun addConstraintsExpSeq(expSeq: Sequence<Exp>, ctx: ConditionOn? = null) {
        expSeq.forEach {
            if (isFailed) return
            addConstraint(it, ctx)
            if (isFailed) return
        }
//        complex.dedup()
        propagate()
    }

    fun addConstraintsExpIt(argIt: Iterable<Exp>, ctx: ConditionOn? = null) {
        argIt.forEach {
            if (isFailed) return
            addConstraint(it, ctx)
            if (isFailed) return
        }
//        complex.dedup()
        propagate()
    }


    fun copySimple(): DynCube? = simple?.copy()

    fun copyComplex(): DComplex = complex.copy()

    fun copyDontCares(): VarSet? = dontCares?.copy()

//    fun containsConstraint(e: Exp): Boolean {
//        return if (e.isSimple) {
//            if (simple != null) {
//                simple!!.containsLit(e.asLit)
//            } else {
//                false
//            }
//
//        } else if (e.isComplex) {
//            complex.contains(e)
//        } else {
//            throw IllegalStateException()
//        }
//    }


    val isSolved: Boolean get() = !isFailed && complex.isNullOrEmpty

    val isFailed: Boolean get() = _failed


//    fun assertCareVars(constraintJustAdded: String? = null): Boolean = if (c.complex != null) {
//        c.complex.assertVars(constraintJustAdded);
//    } else {
//        true;
//    }

    fun addConstraint(constraint: Exp, ctx: ConditionOn? = null) {
        assert(!isFailed && space === constraint.space)

        if (ctx != null) {
            val conditioned = constraint.condition(ctx)

            if (conditioned !== constraint && Space.config.log.condition) {
                println("$depth Conditioned: $constraint")
                println("     ctx: $ctx")
                println("     to: $conditioned")
            }


            addConstraint(conditioned)
        } else {
            addConstraint(constraint)
        }

    }


    @JvmOverloads
    @Throws(AlreadyFailedException::class)
    fun addConstraint(expText: String?, ctx: ConditionOn? = null) {
        assert(!isFailed)

        val exp: Exp? = parser.parseExpOrNull(expText)
        if (exp != null) {
            addConstraint(exp, ctx = ctx)
        }

    }

//    fun addConstraint(constraint: Exp, condition: Condition) {
//        assert(!isFailed && space === constraint.space)
//        val simplified: Exp = condition.condition(constraint, log = false, depth = depth)
//        addConstraint(simplified)
//    }

    fun addConstraint(constraint: Exp) {
        assert(!isFailed)
        assert(space === constraint.space)
        when {
            constraint.isTrue -> Unit
            constraint.isFalse -> fail()
            constraint.isLit -> assign(constraint.asLit)
            constraint.isCube -> assignAll(constraint.asCube)
            constraint.isDcOr -> addDontCare(constraint.asDcOr)
            constraint.isAndLike -> {
                constraint.args.forEach {
                    if (isFailed) return
                    addConstraint(it)
                    if (isFailed) return
                }
            }

            else -> addComplexConstraint(constraint)


        }
    }

    private fun addComplexConstraint(cc: Exp) {
        assert(!cc.isAnd)

        if (cc is Xor) {
            addConstraintXor(cc)
            return
        }

        if (false && cc.isNot) {
            val flip = cc.toNnf(true);
//            val flip = constraint.pushNotsIn();
            assert(flip.isPos)
            addConstraint(flip);
            return

        }

        if (false && cc.isFlattenableNand) {
            val and = cc.asNand.flattenNand()
            println("isFlattenableNand")
            println("  before: $cc")
            println("  after:  $and")
            addConstraint(and)
            return
        }

        //!clause
        if (cc is Not && cc.pos is Or && cc.pos.isAllLits) {
            cc.pos._args.forEach {
                if (isFailed) return
                assign(it.asLit.flp)
                if (isFailed) return
            }
            return
        }
//
//        if (cc is Or && cc._args.size == 2) {
//            val a1: Exp = cc._args[0]
//            val a2: Exp = cc._args[1]
//            if (a1 is Lit && a2 is Lit) {
//                addConstraintOrVv(cc)
//            } else if (a1 is Lit && a2 is Cube) {
//                addConstraintOrVvs(a1, a2)
//            } else if (a1 is Cube && a2 is Lit) {
//                addConstraintOrVvs(a2, a1)
//            } else if (a1 is Lit && a2 is Not && a2.pos.isClause) {
//                addConstraintOrVvs(a1, a2)
//            } else if (a1 is Not && a1.pos.isClause && a2 is Lit) {
//                addConstraintOrVvs(a2, a1)
//            } else {
//                complex.add(cc)
//            }
//            return
//        }

//        if (cc.isLitImp) {
//            println("LitImp: $cc")
//            throw IllegalStateException(cc.simpleName + "  " + cc)
//        }
//
//        if (cc.isVv) {
//            throw IllegalStateException(cc.simpleName + "  " + cc)
//        }

        complex.add(cc)


    }

    private fun addConstraintXor(cc: Xor) {
        complex.add(cc)
    }

    //lit imp
    private fun addConstraintOrVvs(lit1: Lit, cube: Cube) {
        for (lit2: Lit in cube) {
            addConstraintOrVv(lit1, lit2)
        }
    }

    //lit imp
    private fun addConstraintOrVvs(cube: Cube, lit: Lit) {
        for (lit2: Lit in cube) {
            addConstraintOrVv(lit, lit2)
        }
    }

    private fun addConstraintOrVvs(lit1: Lit, notClause: Not) {
        if (notClause.pos !is Or) throw IllegalArgumentException()
        for (lit2: Exp in notClause.pos._args) {
            if (lit2 !is Lit) throw IllegalArgumentException()
            addConstraintOrVv(lit1, lit2.flp)
        }
    }

    private fun addConstraintOrVv(lit1: Lit, lit2: Lit) {
        val vv = space.expFactory.mkOrLitPair(lit1, lit2)
        addConstraint(vv)
    }

    private fun addConstraintOrVv(vv: Or) {
        complex.add(vv)
    }


    private val mkSimple: DynCube
        get() {
            if (simple == null) {
                simple = space.mkSimple()
            }
            return simple!!
        }


    private val mkDontCares: VarSet
        get() {
            if (dontCares == null) {
                dontCares = space.varSetBuilder()
            }
            return dontCares!!
        }

//    fun satCountPL(): Long {
//        return satCountPL(vars.plusVarSet(dontCares))
//    }


    fun satCountPL(): Long {

        propagate()

        return when {
            isFailed -> 0L
            isSolved -> 1L
            else -> {
                val baseSatCount = mkFormula().satCountPL
                if (hasDontCares) {
                    baseSatCount * BoolMath.permCount(dontCares!!.size)

                } else {
                    baseSatCount
                }
            }
        }


    }

    val simpleVars: VarSet get() = simple?.vars ?: space.mkEmptyVarSet()

    val complexVars: VarSet
        get() = complex.vars


    val anyVarOverlap: Boolean
        get() {
            if (simple.isNullOrEmpty) return false
            if (complex.isNullOrEmpty) return false
            return complexVars.anyVarOverlap(simpleVars)
        }


    val isSimpleComplexDisjoint: Boolean get() = !anyVarOverlap


    fun getSatCount(): BigInteger {
        return toDnnf().smooth.satCount
    }

    @JvmOverloads
    fun mkFormula(parent: PosComplexMultiVar? = null): Exp {
        val ff = complex.mkFormula()
        if (ff is PosComplexMultiVar && parent != null) {
            ff.addParent(parent)
        }
        return ff
    }

    fun computeUnionFind(): UnionFind<Exp> {
        return mkFormula().computeUnionFind();
    }

    fun computeFccs(): List<List<Exp>> {
        return mkFormula().computeUnionFind().fccs;
    }

    val isDirty: Boolean
        get() {
            return when {
                q == null || q!!.isEmpty() -> false
                isFailed -> {
                    assert(q == null)
                    false
                }
                else -> true
            }

        }


    val isStable: Boolean get() = !isDirty


    val isAllSimple: Boolean get() = hasSimple && !hasComplex;
    val isAllComplex: Boolean get() = !hasSimple && hasComplex;


    fun propagate() {
        if (q == null || q!!.isEmpty() || isFailed) return
        val lit = q!!.removeAt(0)
        val before = complex
        complex = DComplex()
        addConstraintsExpIt(before.argIt, lit)
    }


    fun propagateIntersection(): Boolean {
        if (complex.isNullOrEmpty || simple.isNullOrEmpty) return false;


        val simpleVars = simple!!.vars
        val complexVars = complex.vars

        val overlap = simpleVars.overlap(complexVars)
        return if (overlap.isEmpty()) {
            false
        } else {
            for (v in overlap) enqueue(v, simple!!.isTrue(v))
            propagate()
            true
        }


    }


    fun assignAll(ass: Cube): Boolean {
        var anyChange = false
        for (lit in ass.litIt()) {
            if (isFailed) return true
            val ch = assign(lit)
            if (isFailed) return true
            if (ch) anyChange = true
        }
        return anyChange
    }


//    private var simple: DynCube?
//        get() = c.simple
//        set(value) {
//            c.simple = value
//        }
//
//
//    private var complex: DynComplex?
//        get() = c.complex
//        set(value) {
//            c.complex = value
//        }
//
//    private var dontCares: VarSet?
//        get() = c.dontCares
//        set(value) {
//            c.dontCares = value
//        }

    val isEmpty: Boolean get() = !hasSimple && !hasComplex

    fun toDnnfInternal(): Exp {
        propagate()

        if (anyVarOverlap) {
            throw IllegalStateException()
        }

        return if (isFailed) {
            space.mkFalse()
        } else if (!hasSimple && !hasComplex) {
            space.mkTrue()
        } else if (!hasSimple && hasComplex) {
            complex.toDnnf(parent)
        } else if (hasSimple && !hasComplex) {
            simple!!.toDnnf()
        } else if (hasSimple && hasComplex) {
            val dSimple = simple!!.toDnnf()   //T|F|Lit|Cube
            val dComplex = complex.toDnnf(parent)

            space.expFactory.mkDAnd(dSimple, dComplex)
        } else {
            throw IllegalStateException()
        }
    }

    fun toPLExp(): Exp {
        propagate()

        return if (isFailed) {
            space.mkFalse()
        } else if (!hasSimple && !hasComplex) {
            space.mkTrue()
        } else if (!hasSimple && hasComplex) {
            complex.mkExp()
            mkFormula()
        } else if (hasSimple && !hasComplex) {
            simple!!.mkCubeExp()
        } else if (hasSimple && hasComplex) {

            assert(simple != null)


            val ss = simple!!.mkCubeExp()
            val cc = complex.mkExp()


            return if (ss.isFalse || cc.isFalse) {
                space.mkFalse()
            } else if (ss.isTrue) {
                cc
            } else if (cc.isTrue) {
                ss
            } else {
                return space.mkAnd(ss, cc)
            }
        } else {
            throw IllegalStateException()
        }


    }


    fun addDcOrs(d: Exp, dontCares: VarSet): Exp {
        val b = space.argBuilder(Op.DAnd)
        b.addExp(d)
        for (dc: Var in dontCares) {
            val dcOr = dc.mkDcOr()
            b.addExp(dcOr)
        }
        return b.mk()
    }


    val depth: Int get() = if (parent == null) 1 else parent.depth + 1


    fun assertDisjointSimpleComplex(): Boolean {
        assert(isSimpleComplexDisjoint)
        return true
    }

    fun assertDisjointDc() {
        assert(isDcDisjoint)
    }

    fun anySimpleComplexOverlap(): Boolean {
        return !isSimpleComplexDisjoint;
    }

    val xorsDeep: List<Exp> get() = computeXorsDeep()

    fun computeXorsDeep(): List<Exp> {
        if (complex.isNullOrEmpty) return emptyList()
        val b = mutableListOf<Exp>()
        for (exp in complex.argIt) {
            if (exp.isConstant || exp.isLit)
                continue
            else if (exp.isXor) {
                b.add(exp)
            } else {
                b.addAll(exp.xorsDeep)
            }
        }
        return b
    }

    fun computeXorConstraints(): List<Exp> {
        return Csp.computeXorConstraints(complexIt)
    }

    /**
     * Combines Space.getCoreXorsFromSpace() with Csp.getXorConstraints
     */
    fun computeAllXorConstraints(): Set<Exp> {
        val xors = mutableSetOf<Exp>()
        val xors1 = space.coreXorsFromSpace;
        val xors2 = computeXorConstraints();
        xors.addAll(xors1);
        xors.addAll(xors2);
        return xors1.union(xors2)
    }


//    fun getConstraintsContainingXor(): List<Exp> {
//        if (complex.isNullOrEmpty) return ImmutableList.of()
//        val b = mutableListOf<Exp>()
//        for (exp in complex!!) {
//            if (exp.isXorOrContainsXor) {
//                b.add(exp)
//            }
//        }
//        return b
//    }

    val isDcDisjoint: Boolean
        get() {
            return if (dontCares == null) true
            else dontCares!!.isVarDisjoint(simple) || dontCares!!.isVarDisjoint(complex.vars)
        }


    /**
     * copies only the complex constraints to a new csp
     * and creates a new trimmed the space that references only unassigned _complexVars
     */
    fun reduce(): Csp {
        propagateIntersection()
        val space = Space(complexVars)
        val cc = DComplex(complex.size)
        complex.forEach {
            val line = it.serialize()
            val exp = space.parser.parseExp(line)
            cc.add(exp)
        }
        return Csp(space, complex = cc)
    }

    fun checkState() {
        if (isFailed) {
            assert(simple == null && dontCares == null)
        }
    }


    fun addConstraint(imp: Imp) {
        System.err.println("**** addConstraint-Imp")
        val arg1 = imp.arg1
        val arg2 = imp.arg2
        addBinaryOr(arg1.flip, arg2)
    }

    fun addConstraint(rmp: Rmp) {
        System.err.println("**** addConstraint-Rmp")
        val arg1 = rmp.arg1
        val arg2 = rmp.arg2
        addBinaryOr(arg1, arg2.flip)
    }

    fun addConstraint(iff: Iff) {
        System.err.println("**** addConstraint-IFF")
        val arg1 = iff.arg1
        val arg2 = iff.arg2
        addImp(arg1, arg2)
        addImp(arg2, arg1)
    }

    fun addImp(arg1: Exp, arg2: Exp) {
        System.err.println("**** addImp")
        addBinaryOr(arg1.flip, arg2)
    }

    fun addRmp(arg1: Exp, arg2: Exp) {
        System.err.println("**** addRmp")
        addBinaryOr(arg1, arg2.flip)
    }

    fun addIff(arg1: Exp, arg2: Exp) {
        System.err.println("**** addIff")
        if (arg1 === arg2) return


        if (arg1.isFalse && arg2.isFalse) return
        if (arg1.isTrue && arg2.isTrue) return

        if (arg1.isFalse && arg2.isTrue) {
            fail()
            return
        }

        if (arg1.isTrue && arg2.isFalse) {
            fail()
            return
        }

        if (arg1.isTrue && arg2.isOpen) {
            addConstraint(arg2)
            return
        }


        if (arg1.isOpen && arg2.isTrue) {
            addConstraint(arg1)
            return
        }

        if (arg1.isFalse && arg2.isOpen) {
            addConstraint(arg2.flip)
            return
        }

        if (arg1.isOpen && arg2.isFalse) {
            addConstraint(arg1.flip)
            return
        }

        addImp(arg1, arg2)
        addImp(arg2, arg1)

    }

    fun addBinaryOr(arg1: Exp, arg2: Exp) {
        System.err.println("**** addBinaryOr")

        if (arg1.isTrue || arg2.isTrue) {
            return
        }

        if (arg1 === arg2) {
            addConstraint(arg1)
            return
        }

        if (arg1 === arg2.flip) {
            return
        }

        if (arg1.isFalse) {
            addConstraint(arg2)
        }
        if (arg2.isFalse) {
            addConstraint(arg1)
        }

        if (arg1.isAnd) {
            for (a1 in arg1.args) {
                addBinaryOr(arg2, a1)
            }
            return
        }

        if (arg2.isAnd) {
            for (a2 in arg2.args) {
                addBinaryOr(arg1, a2)
            }
            return
        }

        val b = ArgBuilder(space, Op.Or)
        b.addExp(arg1)
        b.addExp(arg2)
        val or: Exp = b.mk()
        addConstraint(or)

    }


    private fun fail() {
        if (_failed) {
            throw AlreadyFailedException()
        }
        _failed = true
        dontCares = null
        complex = DComplex()
        simple = null
        q = null
    }


    fun assign(sLit: String): Boolean {
        val lit = space.mkLit(sLit)
        return assign(lit)
    }

    fun assign(vr: Var, value: Boolean): Boolean {
        return assign(vr.vrId, value)
    }

    fun assign(varId: Int, value: Boolean): Boolean {
        val vr: Var = getVar(varId)
        val lit = vr.lit(value)
        return assign(lit)
    }


    fun getVar(varId: Int): Var {
        return space.getVar(varId)
    }

    fun getVar(varCode: String): Var {
        return space.getVar(varCode)
    }


    fun <T> notNull(vv: T?): T {
        if (vv == null) throw IllegalArgumentException()
        return vv
    }

    fun assignCon(con: ConditionOn): Boolean {
        assert(!isFailed && space === con.space)
        return when (con) {
            is Lit -> assign(con)
            is Cube -> assignAll(con)
            else -> throw IllegalStateException()
        }
    }

    fun assign(lit: Lit): Boolean {
        assert(!isFailed && space === lit.space)
        val ch = try {
            mkSimple.assign(lit)
        } catch (e: ConflictingAssignmentException) {
            fail()
            true
        }



        if (ch) {
            dontCares?.removeVarId(lit.varId)
        }

        assert(dontCares?.containsVar(lit) != true)



        if (ch) {
            enqueue(lit)
        }



        return ch
    }

    private fun enqueue(vr: Var, sign: Boolean) {
        enqueue(vr.lit(sign))
    }

    private fun enqueue(lit: Lit) {
        if (q == null) q = mutableListOf<Lit>()
        q!!.add(lit)
    }

    private fun enqueue(cube: Cube) {
        cube.forEach { enqueue(it) }
    }

    private fun enqueue(con: ConditionOn) {
        when (con) {
            is Lit -> enqueue(con)
            is Cube -> enqueue(con)
            else -> throw IllegalStateException()
        }
    }


    @Throws(ConflictingAssignmentException::class)
    fun assign(signedVarId: Int): Boolean {
        val lit = space.mkLit(signedVarId)
        return assign(lit)
    }

    val hasComplex: Boolean get() = !complex.isNullOrEmpty
    val hasDontCares: Boolean get() = !dontCares.isNullOrEmpty

    val hasSimple: Boolean get() = !simple.isNullOrEmpty

    val cube: Cube = if (hasSimple) {

        simple!!
    } else {
        space.emptyCube
    }


    fun addDontCare(varCode: String): Boolean {
        return mkDontCares.add(space.getVar(varCode))
    }

    fun addDontCare(vr: Var): Boolean {
        return mkDontCares.addVar(vr)
    }

    fun addDontCare(dcOr: DcOr): Boolean {
        return addDontCare(dcOr.vr)
    }


    fun addDontCares(dontCares: VarSet) {
        for (dontCare in dontCares) {
            addDontCare(dontCare)
        }
    }

    fun addDontCares(sVarCodes: String) {
        val varSet: VarSet = space.createVarSet(sVarCodes)
        addDontCares(varSet)
    }

    fun checkDisjoint(): Boolean {
        return isSimpleComplexDisjoint
    }

    fun copy(): Csp {
        return Csp(this)
    }

    fun condition(sLits: String): Csp {
        val conditionOn = parser.parseLitsToConditionOn(sLits)
        return condition(conditionOn)
    }

//    fun condition(c: ConditionOn): Csp {
//        return when (c) {
//            is Lit -> condition(c as Lit)
//            is Cube -> condition(c as Cube)
//            else -> throw IllegalStateException()
//        }
//    }
//
//    fun condition(cube: Cube): Csp {
//        //        val copy = copy()
//        copy.assignAll(cube)
//        return copy
//    }

    fun condition(con: ConditionOn): Csp {
        propagate()
        val copy = copy()
        val vars1 = copy.vars
        assert(copy == this)
        copy.assignCon(con)
        copy.propagate()
        val vars2 = copy.vars
        val vars3 = vars1.minus(vars2)
        copy.removeSimpleCon(con)
        copy.addDontCares(vars3)
        return copy
    }

    fun condition(lit: Lit): Csp {
        propagate()
        val copy = copy()
        val vars1 = copy.vars
        assert(copy == this)
        copy.assign(lit)
        copy.propagate()
        val vars2 = copy.vars
        val vars3 = vars1.minus(vars2)
        copy.removeSimple(lit)
        copy.addDontCares(vars3)
        return copy
    }


    val complexD: DComplex? get() = complex

    val complexIt: Iterable<Exp>
        get() {
            return if (hasComplex) {
                complex.argIt
            } else {
                It.emptyIt()
            }
        }


    val dontCareIt: Iterable<Var>
        get() {
            return if (hasDontCares) {
                dontCares!!.varIt()
            } else {
                It.emptyIt()
            }
        }
    val simpleIt: Iterable<Exp>
        get() {
            return if (hasSimple) {
                simple!!.argIt()
            } else {
                It.emptyIt()
            }
        }

    val complexList: List<Exp> get() = complex.toList()

    val complexConstraintsSer: Iterable<String> get() = complex.toStringIt


    val careVars: VarSet get() = complexVars;

    val simpleAndComplexVars: VarSet get() = complexVars.plus(simpleVars);

    fun simplifySeriesModelAnds() {
        if (!hasComplex) return;
        if (isFailed) return
        val tmp = complex;
        complex = DComplex()
        for (e in tmp.argIt) {
            if (isFailed) {
                return
            }
            val s = e.simplifySeriesModelAnd();
            addConstraint(s);
            if (isFailed) {
                return
            }
        }
    }

    override fun toString(): String {
        return "SpaceCsp";
    }

    @JvmOverloads
    fun print(depth: Int = 0) {
        val sDontCares = if (dontCares.isNullOrEmpty) "" else dontCares.toString()
        val sSimple = if (simple.isNullOrEmpty) "" else simple.toString()
        prindent(depth, "<csp>");
        prindent(depth, "  <dontCares>$sDontCares</dontCares>");
        prindent(depth, "  <simple>$sSimple</simple>");
        val cc = complexConstraintCount
        if (cc == 0) {
            prindent(depth, "  <complex></complex>");
        } else if (cc == 1) {
            prindent(depth, "  <complex>" + complexIt.first() + "</complex>");
        } else {
            prindent(depth, "  <complex>");
            for (exp: Exp in complexIt) {
                prindent(depth, "    $exp");
            }
            prindent(depth, "  </complex>");
        }
        prindent(depth, "  <vars>$vars</vars>");
        prindent(depth, "  <sat>" + isSat() + "</sat>");
        prindent(depth, "<csp>");
    }


    fun toString(a: Ser): String {
        serialize(a);
        return a.toString();
    }


    fun serialize(a: Ser) {
        serializeVarsLine(a)
        serializeSimpleConstraints(a);
        serializeComplexConstraints(a);
    }

//    fun serializeConstraints(): String {
//        val a = Ser();
//        serializeComplexConstraints(a);
//        return a.toString();
//    }

    fun serializeComplexConstraints(a: Ser) {
        serializeConstraints(complexIt, a);
    }

    fun serializeSimpleConstraints(a: Ser) {
        serializeConstraints(simpleIt, a);
    }

    fun serializeVarsLine(a: Ser) {
        a.append(Vars.HEAD_VARS_LINE)
        vars.serialize(a)
        a.append(Vars.FOOT)
    }


    fun serialize(): String {
        val a = Ser();
        serialize(a);
        return a.toString().trim();
    }


    override fun isSat(): Boolean {
        propagate()

        if (isFailed) {
            return false
        }

        if (isSolved) {
            return true
        }

        val formula = mkFormula()

        val vars = formula.vars

        if (vars.size == 0) {
            throw IllegalStateException()
        }


        return formula.isSat

    }


    fun getBB(): DynCube {
        propagate()
        val bb = mkFormula().bb
        return DynCube.union(space, simple, bb)
    }

//    fun getBBLite(): DynCube {
//        propagate()
//        val bb: DynCube = mkFormula().asFormula.computeBBLite()
//        return if (bb.isNullOrEmpty && simple.isNullOrEmpty) {
//            space.mkSimple()
//        } else if (bb.isNullOrEmpty && !simple.isNullOrEmpty) {
//            simple!!
//        } else if (!bb.isNullOrEmpty && simple.isNullOrEmpty) {
//            bb
//        } else if (!bb.isNullOrEmpty && !simple.isNullOrEmpty) {
//            DynCube.union(space, bb, simple!!)
//        } else {
//            throw IllegalStateException()
//        }
//    }


    fun refineFormulaOnly(sLit: String): Exp {
        if (!hasComplex) {
            return space.mkTrue()
        }

        return mkFormula().condition(sLit)


//        val a = Add(c = copyComplex()!!, cc = space.mkLit(sLit))
//        val csp = Csp(space, add = a)
//        csp.propagate()
//        csp.propagateIntersection()
//        return csp.toPLExp()
    }

    fun _assignSafe(ctx: ConditionOn, enqueue: Boolean = true) {
        mkSimple.assignSafe(ctx)
        if (enqueue) this.enqueue(ctx)
    }


    fun getLargestNonXorConstraint(): Exp {
        var largest: Exp? = null;
        var largestStrLen = -1;
        for (e in complexIt) {
            if (e.isXor) continue;

            val strLen: Int = e.toString().length;
            if (strLen > largestStrLen) {
                largest = e;
                largestStrLen = strLen;
            }
        }
        return largest!!;
    }


    fun simplifyBasedOnVvs(): Boolean {
        var masterChange = false;
        while (true) {

            propagate()

            var anyChange = false

            if (complex.isNullOrEmpty) {
                return false
            }


            val vvs = vvConstraints

            val copy = complex.copy();

            complex = DComplex()

            for (e in copy) {
                if (e.isXorOrContainsXor || e.isIffOrContainsIff) {
                    addConstraint(e)
                } else if (e.isVVPlus) {
                    val subsumedVVs = findSubsumedVVs(e, vvs)

                    if (subsumedVVs.isEmpty()) {
                        addConstraint(e)
                    } else {
                        val s = e.conditionVVs(subsumedVVs)
                        if (s !== e) {
                            anyChange = true
                            addConstraint(s)
                            logSimplified(subsumedVVs, e, s)
                            if (isFailed) throw IllegalStateException("Failed after adding constraint[$e]")
                        } else {
                            addConstraint(e)
                        }
                    }
                } else if (e.isVv) {
                    addConstraint(e)
                } else {
                    throw IllegalStateException()
                }

            }

            if (!anyChange) {
                break
            } else {
                masterChange = true
            }
        }//while

        return masterChange

    }

    fun logSimplified(lit: Exp, before: Exp, after: Exp) {
        if (false && before !== after) {
            System.err.println("Simplified from lit: $lit")
            System.err.println("\t before: $before")
            System.err.println("\t after:  $after")
        }
    }

    fun logSimplified(ctx: EvalContext, before: Exp, after: Exp) {
        if (false && before !== after) {
            System.err.println("Simplified from ctx: $ctx")
            System.err.println("\t before: $before")
            System.err.println("\t after:  $after")
        }
    }

    fun logSimplified(ctx: Any, before: Exp, after: Exp) {
        if (!(before === after || !Space.config.log.vv)) {
            System.err.println("Simplified from ctx: $ctx")
            System.err.println("\t before: $before")
            System.err.println("\t after:  $after")
        }
    }

    fun toDnnfSmooth(): Exp {
        return toDnnf().smooth
    }

    fun toDnnfCsp(): Dnnf {
        return Dnnf(toDnnf());
    }

    fun toDnnf(): Exp {
        val dnnf = toDnnfInternal()
        assert(dnnf.isDnnf)

        return if (!dnnf.isFalse && Space.config.dnnfCompile.includeDcs) {
            val cspVars = this.vars
            val dnnfVars = dnnf.vars
            val dnnfDontCares = cspVars.minus(dnnfVars)
            addDcOrs(dnnf, dnnfDontCares)
        } else {
            dnnf
        }
    }

    fun getCubeVars(): VarSet = if (simple.isNullOrEmpty) space.mkEmptyVarSet() else simple!!.vars


    val vars: VarSet
        get() = when {
            simple.isNullOrEmpty && dontCares.isNullOrEmpty && complex.isNullOrEmpty -> EmptyVarSet

            simple.isNullOrEmpty && dontCares.isNullOrEmpty -> complex.vars
            simple.isNullOrEmpty && complex.isNullOrEmpty -> dontCares!!
            complex.isNullOrEmpty && dontCares.isNullOrEmpty -> simple!!.vars

            dontCares.isNullOrEmpty -> complex.vars.plus(simple!!.vars)
            complex.isNullOrEmpty -> dontCares!!.plus(simple!!.vars)
            simple.isNullOrEmpty -> dontCares!!.plus(complex.vars)
            !simple.isNullOrEmpty && !dontCares.isNullOrEmpty && !complex.isNullOrEmpty -> {
                val bb = space.varSetBuilder()
                bb.addVarSet(complex.vars)
                bb.addVarSet(simple!!.vars)
                bb.addVarSet(dontCares!!)
                bb
            }
            else -> throw IllegalStateException()
        }


    val size: Int get() = simpleConstraintCount + complexConstraintCount

    val simpleConstraintCount: Int get() = simple?.size ?: 0
    val complexConstraintCount: Int get() = complex.size

    fun simplifyAlwaysTrueVars() {
        maybeAddAlwaysTrueVars()
        propagate()
        propagateIntersection()

    }

    fun containsVarCode(varCode: String): Boolean {
        if (!space.containsVarCode(varCode)) return false;
        return simple?.containsVar(varCode) ?: false || complex.containsVar(varCode) || dontCares?.containsVarCode(varCode) ?: false
    }

    fun maybeAddAlwaysTrueVars() {
        for (atVarCode in Space.alwaysTrueVars1) {
            if (containsVarCode(atVarCode)) {
                val lit = space.getVar(atVarCode).pLit()
                addConstraint(lit)
            }
        }
    }


    fun newMutableVarSet(): VarSetBuilder {
        return space.newMutableVarSet()
    }


    fun serializeSimpleConstraints(): String {
        val a = Ser()
        serializeSimpleConstraints(a)
        return a.toString()
    }

    fun sortComplexConstraintsByStrLen(): List<Exp> {

//        val comparator: Comparator<Exp> = Comparator<Exp> { e1, e2 ->
//            val s1 = e1.toString().length
//            val s2 = e2.toString().length
//            s1.compareTo(s2)
//        }

        val selector: (Exp) -> Int = { it.toString().length }

        return sortComplexConstraints(selector)
    }

    fun sortComplexConstraints(selector: (Exp) -> Int): List<Exp> {
        return if (hasComplex) {
            complex.argIt.sortedBy(selector)
        } else {
            emptyList()
        }
    }


    fun getExpWithLargestAnd(): Exp? {
        return if (!hasComplex) {
            null
        } else {
            var best: Exp? = null
            var bestAnd: Exp? = null
            for (exp in complex) {
                if (bestAnd == null) {
                    best = exp
                    bestAnd = exp.andWithHighestLitArgCount
                } else {
                    val and = exp.andWithHighestLitArgCount
                    if (and != null && and.andLitArgCount > bestAnd.andLitArgCount) {
                        best = exp
                        bestAnd = and
                    }
                }
            }
            System.err.println("best:    " + best!!)
            System.err.println("bestAnd: " + bestAnd!!)
            System.err.println("bestAnd.litCount: " + bestAnd.andLitArgCount)
            best
        }
    }


    fun getModelCodesForSeries(seriesNameArg: String): Set<String> {
        var seriesName = seriesNameArg
        if (!seriesName.startsWith("SER")) {
            seriesName = "SER_$seriesName"
        }
        val fCon = refineFormulaOnly(seriesName)
        val n = fCon.toDnnf()
        val nn = n.copyToOtherSpace()

        val outVars = nn.space.getVars(Prefix.MDL)

        val projection = nn.project(outVars)
        val cubes = projection.cubesSmooth

        val s = HashSet<String>()
        for (cube in cubes) {
            val firstTrueVar = cube.trueVars.firstVar
            s.add(firstTrueVar.varCode)
        }
        return s
    }

    fun litIt(): Iterable<Lit> {
        return DynCube.litIt(simple)
    }

    fun litIterator(): Iterator<Lit> {
        return DynCube.litIterator(simple)
    }

    fun atRefine(): Csp {
        val copy = copy()
        copy.maybeAddAlwaysTrueVars()
        copy.propagate();
        return copy
    }


    fun transform(t: Transformer) {
        val ff = mkFormula()

        this.complex = DComplex()

//        System.err.println("Transforming " + tmp.size() + " sentences!");
        var current = 0;
        for (e: Exp in ff.argIt) {
            if (Space.config.log.transform) {
                Space.log.info("  Transforming [" + current + "] of " + ff.size())
            }
            if (isFailed) {
                return;
            } else {
                val a = t.transform(e);
                addConstraint(a);
                current++;
            }

            current++;

        }
    }


    fun toBnf() {
        transform(Transformer.BNF);
    }


    fun toBnfKeepXors() {
        transform(Transformer.BNF_KEEP_XORS);
    }

    fun bnfToNnf() {
        transform(Transformer.BNF_TO_NNF);
    }


    fun toNnfKeepXors() {
        toBnfKeepXors();
        bnfToNnf();
    }

    fun serializeTiny(): String {
        val a = Ser();
        serializeTinyCnf(a);
        return a.toString();
    }

    fun serializeTinyCnf(a: Ser) {
        space.serializeVarMap(a);
        a.newLine()
        //        serializeTinyCnfSimple(a);
        serializeTinyCnfComplex(a);
    }


    fun serializeTinyCnfComplex(a: Ser) {
        if (complex.isNullOrEmpty) return;
        for (e: Exp in complexIt) {
            e.serializeTinyCnf(a);
            a.newLine();
        }
    }


/*
 Formula tmp = this.getFormula().asFormula;

    complex = new DynFormula(space);
    for (Exp b : tmp) {
        if (isFailed()) {
            return;
        }
        Exp a = tCon.transform(b);
        addConstraint(a);
    }
 */

    fun toNnf(keepXors: Boolean = false) {
        propagate();
        assertDisjointSimpleComplex()

        if (!hasComplex) return;

        val tmp = complex

        complex = DComplex()

        for (e in tmp) {
            val bnf = e.toBnf(keepXors)
            val nnf = bnf.transform(Transformer.BNF_TO_NNF);
            addConstraint(nnf);
        }

    }


    fun toNnf2() {
        propagate()
        assertDisjointSimpleComplex()
        toBnf();
        bnfToNnf();
    }

    fun nnfToCnf() {
        transform(Transformer.NNF_TO_CNF);
    }

    fun toCnf() {
        transform(Transformer.CNF);
        assert(checkCnf())
    }


    fun checkCnf(): Boolean {
        if (!hasComplex) return true;
        for (constraint in complex.argIt) {
            if (!constraint.isClause) {
                throw IllegalStateException("constraint is not CNF[" + constraint + "]");
            }
        }
        return true;
    }


    fun serializeDimacs(): String {
        val a = Ser()
        serializeDimacs(a)
        return a.toString()
    }

    fun serializeDimacs(a: Ser) {

        //p cnf 1978 49232

        val clauseCount = size

        a.append("p cnf ")
        a.append(space.varCount)
        a.append(' ')
        a.append(clauseCount)
        a.newLine()

        serializeDimacsSimple(a)
        serializeDimacsComplex(a)

    }

    fun serializeDimacsComplex(a: Ser) {
        if (hasComplex) {
            for (e in complexIt) {
                e.serializeDimacs(a)
            }
        }
    }

    fun serializeDimacsSimple(a: Ser) {
        if (hasSimple) {
            for (e in simpleIt) {
                e.serializeDimacs(a)
            }
        }
    }
//


    fun getXor(prefix: String): Xor? {
        val xors: List<Exp> = computeXorConstraints()
        for (xor in xors) {
            if (xor.argCount == 0) {
                throw IllegalStateException()
            }
            val arg = xor.getArg(0)
            if (arg.isVarWithPrefix(prefix)) {
                return xor.asXor
            }
        }
        return null
    }


    val yearXor: Exp?
        get() = getXor(Mod.PREFIX_YR)

    val interiorColorXor: Exp?
        get() = getXor(Mod.PREFIX_ICOL)

    val exteriorColorXor: Exp?
        get() = getXor(Mod.PREFIX_XCOL)

    val seriesXor: Exp?
        get() = getXor(Mod.PREFIX_SER)

    val modelXor: Exp?
        get() = getXor(Mod.PREFIX_MDL)

    fun printYearSeriesModels1() {
        val yearXor = yearXor
        for (yearExp in yearXor!!.argIt) {
            val yearVarCode = yearExp.varCode

            val map = computeSeriesModelMultiMap()

            val keys = map.keySet()
            for (series in keys) {
                System.err.println("$yearVarCode $series")
                val models = map.get(series)
                for (model in models) {
                    System.err.println("  $model")
                }

            }
        }

    }

    /**
     * Note: this only works c a "year" cofactor
     */
    fun computeSeriesModelMultiMap(): Multimap<Var, Var> {
        val map = HashMultimap.create<Var, Var>()
        for (vv in vvConstraints) {
            if (vv.isSeriesModelVV) {
                val seriesVar = vv.seriesVar
                val modelVar = vv.modelVar
                map.put(seriesVar, modelVar)
            }
        }
        return map
    }

    val vvConstraints: List<Exp>
        get() {
            if (!hasComplex) return emptyList()
            val b = mutableListOf<Exp>()
            for (exp in complexIt) {
                if (exp.isVv) {
                    b.add(exp)
                }
            }
            return b
        }


    val isCnf: Boolean
        get() {
            if (!hasComplex) return true;
            for (cc in complexIt) {
                if (!cc.isClause) {
                    return false;
                }
            }
            return true;
        }

    val isOpen: Boolean get() = !isSolved && !isFailed


    fun toCnfSortedSet(): TreeSet<TreeSet<String>> {
        assert(isCnf)

        val clauseSortedSetComparator = ClauseSortedSetComparator()
        val ss = TreeSet<TreeSet<String>>(clauseSortedSetComparator);

        if (hasComplex) {
            for (e: Exp in complexIt) {
                assert(e.isClause)
                val s: TreeSet<String> = e.clauseToSortedSet();
                ss.add(s);
            }


        }

        return ss

    }

    companion object {

        @JvmStatic
        fun toString(vararg constraints: Exp): String {
            return serializeConstraints(*constraints)
        }

        @JvmStatic
        fun toString(constraints: Iterable<Exp>): String {
            return serializeConstraints(constraints)
        }

        @JvmStatic
        fun serializeConstraints(vararg constraints: Exp): String {
            val a = Ser()
            for (e in constraints) {
                e.serialize(a)
                a.newLine()
            }
            return a.toString()
        }

        @JvmStatic
        fun serializeConstraints(constraints: Iterable<Exp>): String {
            val a = Ser()
            for (e in constraints) {
                e.serialize(a)
                a.newLine()
            }
            return a.toString()
        }

        @JvmStatic
        fun serializeConstraints(constraints: Iterable<Exp>, a: Ser) {
            for (e in constraints) {
                e.serialize(a)
                a.newLine()
            }
        }

        @JvmStatic
        fun conditionFunctionJvm(lit: Lit?, cube: Cube?): ExpFnJvm {
            assert(lit == null || cube == null)
            return when {
                lit != null -> conditionFunctionJvm(lit)
                cube != null -> conditionFunctionJvm(cube)
                else -> identityFunctionJvm()
            }
        }

        @JvmStatic
        fun conditionFunctionJvm(lit: Lit): java.util.function.Function<Exp, Exp> {
            return Fn.conditionJvm(lit)
        }

        @JvmStatic
        fun conditionFunctionJvm(cube: Cube): java.util.function.Function<Exp, Exp> {
            return Fn.conditionJvm(cube)
        }

        @JvmStatic
        fun identityFunctionJvm(): java.util.function.Function<Exp, Exp> {
            return Fn.identityJvm
        }

        @JvmStatic
        fun conditionFunctionKotlin(cube: Cube): ExpFn {
            return Fn.condition(cube)
        }

        @JvmStatic
        fun conditionFunctionKotlin(lit: Lit): ExpFn {
            return Fn.condition(lit)
        }

        @JvmStatic
        fun conditionFunctionKotlin(lit: Lit?, cube: Cube?): ExpFn {
            assert(lit == null || cube == null)
            assert(lit != null || cube != null)
            return when {
                lit != null -> conditionFunctionKotlin(lit)
                cube != null -> conditionFunctionKotlin(cube)
                else -> identityFunctionKotlin()
            }
        }

        @JvmStatic
        fun identityFunctionKotlin(): ExpFn {
            return Fn.identity
        }

        @JvmStatic
        fun asSeq1(complex: Iterable<Exp>): Sequence<Exp> = complex.asSequence()

        @JvmStatic
        fun asSeq2(complex: Iterable<String?>): Sequence<String?> = complex.asSequence()


        @JvmStatic
        fun computeXorConstraints(args: Iterable<Exp>): List<Exp> {
            val aa = mutableListOf<Exp>()
            for (exp in args) {
                if (exp.isXor) {
                    aa.add(exp)
                }
            }
            return aa
        }

        @JvmStatic
        fun compileDnnf(clob: String): Exp {
            return parse(clob).toDnnf()
        }

        @JvmStatic
        fun compileDnnf(sample: CspSample): Exp {
            return compileDnnf(sample.loadText())
        }

        @JvmStatic
        fun toDnnfCsp(clob: String): Dnnf {
            val d = compileDnnf(clob)
            return Dnnf(d);
        }


        //create

        @JvmStatic
        fun parse(clob: String): Csp = Parser.parseCsp1(clob);

        @JvmStatic
        fun parse(cspSample: CspSample): Csp {
            return parse(cspSample.loadText());
        }

        @JvmStatic
        fun computeDcVars(satCountPL: Long, parentVars: VarSet, childVars: VarSet): Long {
            val dcVars = parentVars.minus(childVars)
            return satCountPL * BoolMath.pow(dcVars.size)
        }

        @JvmStatic
        fun computeDcVars(satCount: BigInteger, parentVars: VarSet, childVars: VarSet): BigInteger {
            val dcVars = parentVars.minus(childVars)
            return satCount.multiply(BoolMath.pow(dcVars.size).toBigInteger())
        }

        @JvmStatic
        fun computeDcVars(baseSatCount: BigInteger, dcCount: Int): BigInteger {
            val multiplier = BoolMath.pow(dcCount).toBigInteger()
            return baseSatCount.multiply(multiplier)
        }


    }//end companion

    @JvmOverloads
    fun printVarInfo(depth: Int = 0) {
        val varMap = space.getVarSpace();
        varMap.printVarInfo(depth);
    }


    fun conditionOutAtVars() {
        simplifyAlwaysTrueVars();
    }
//
//    fun removeConstraint(constraint: Exp): Boolean {
//        //        System.err.println("Removing constraint: " + constraint);
//        return if (constraint.isSimple) {
//            removeSimple(constraint)
//        } else if (constraint.isComplex) {
//            removeComplex(constraint)
//        } else {
//            throw IllegalArgumentException()
//        }
//    }

    fun removeSimpleCon(con: ConditionOn): Boolean {
        return when (con) {
            is Lit -> removeSimple(con)
            is Cube -> removeSimple(con)
            else -> throw IllegalStateException()
        }
    }

    fun removeSimple(cube: Cube): Boolean {
        return if (simple.isNullOrEmpty) {
            false
        } else {
            var anyChange = false
            for (lit in cube.litIt()) {
                val ch = simple!!.removeLit(lit)
                if (ch) {
                    anyChange = true
                }
            }
            if (simple!!.isEmpty) {
                simple = null
            }
            anyChange
        }
    }

    fun removeSimple(lit: Lit): Boolean {
        return if (simple.isNullOrEmpty) {
            false
        } else {
            val ch = simple!!.removeLit(lit.asLit)
            if (simple!!.isEmpty) {
                simple = null
            }
            ch
        }
    }

//    fun removeComplex(exp: Exp): Boolean {
//        if (complex.isNullOrEmpty) return false
//        assert(exp.isComplex)
//        val contains = complex!!.contains(exp)
//        val removed = complex!!.remove(exp)
//        assert(contains == removed)
//
//        return removed
//    }

//    fun removeComplex(expText: String): Boolean {
//        val exp = space.parseExp(expText)
//        return removeComplex(exp)
//    }

    fun parseExp(expText: String): Exp {
        val parser = space.parser
        return parser.parseExp(expText)
    }

//    fun moveDontCaresToComplex() {
//        if (dontCares != null) {
//            val dcs: VarSet = dontCares!!
//            dontCares = null
//            for (dc in dcs) {
//                mkComplex.add(dc.mkDcOr())
//            }
//        }
//    }


    fun findSubsumedVVs(vvp: Exp, vvs: Iterable<Exp>): List<Exp> {
        val subsumedVVs = ArrayList<Exp>()
        for (vv in vvs) {
            if (vvp.vvpSubsumesVV(vv)) {
                subsumedVVs.add(vv)
            }
        }
        return subsumedVVs

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Csp

        return when {
            simple != other.simple -> {
                println("Simples dont match")
                false
            }
            complex != other.complex -> {
                println("complex dont match")
                println(this.complex)
                println(other.complex)
                false
            }
            dontCares != other.dontCares -> {
                println("dontCares dont match")
                false
            }
            else -> true
        }
    }

    override fun hashCode(): Int {
        var result = simple?.hashCode() ?: 0
        result = 31 * result + (complex.hashCode())
        result = 31 * result + (dontCares?.hashCode() ?: 0)
        return result
    }

    fun removeComplexConstraints(factoryConstraintsToRelax: Set<Exp>) {
        complex.removeAll(factoryConstraintsToRelax)
    }


    fun getInvAcyVars(): VarSet {
        space.checkVarInfo();

        val varMeta: VarMeta = space.varMeta
        val formulaVars = mkFormula().vars;
        val b = space.varSetBuilder();
        for (vr in formulaVars.varIt()) {
            if (varMeta.isInvAcyVar(vr)) {
                b.addVar(vr);
            }
        }
        return b.build();
    }


    fun getAllConstraints(): List<Exp> {
        val b = mutableListOf<Exp>()
        b.addAll(complexIt)
        b.addAll(simpleIt)
        return b
    }

    fun getAllNonXorConstraints(): Set<Exp> {
        val b = mutableSetOf<Exp>()

        for (exp in complex) {
            if (!exp.isXor) {
                b.add(exp);
            }
        }

        if (simple != null) {
            b.addAll(simple!!.litIt());
        }
        return b
    }

    fun getVVPlusConstraints(): List<Exp> {
        if (complex.isNullOrEmpty) return emptyList()
        val b = mutableListOf<Exp>()
        for (exp in complexIt) {
            if (exp.isVVPlus) {
                b.add(exp);
            }
        }
        return b
    }

    fun scoreXors() {
        val mm = HashMultiset.create<ExpId>()
        complexIt.forEach {
            val prefixes: Set<ExpId> = it.xorPrefixes()
            mm.addAll(prefixes)
        }

        for (e in mm.entrySet()) {
            val xorExpId = e.element
            val xor = space.getExp(xorExpId).asXor
            xor.score = e.count
        }
    }

    fun printXorInfo() {
        println("csp xor info")
        println("  xors = ${computeXorConstraints()}")
        println("  xorsDeep = ${xorsDeep.minus(space.topXors)}")
        println("  xorsDeepMinusTop = ${xorsDeep.minus(space.topXors)}")
        println("  xorsDeepMinusTop = ${xorsDeep.map { it as Xor }.filterNot { it.isTop }}")
    }


}//end csp


fun DynComplex?.asStringSequence(): Sequence<String> = asSeq().map { it.serialize() }


val DynCube?.nonNull: DynCube get() = this!!
val DynComplex?.nonNull: DynComplex get() = this!!
val VarSet?.nonNull: VarSet get() = this!!

fun DynCube?.nullToEmpty(sp: Space): DynCube = if (this == null) sp.mkSimple() else this
fun DynComplex?.nullToEmpty(sp: Space): DynComplex = if (this == null) sp.mkComplex() else this
fun VarSet?.nullToEmpty(sp: Space): VarSet = if (this == null) sp.mkEmptyVarSet() else this

val VarSet.empty: Boolean get() = this.isEmpty()

//val Cube?.isNullOrEmpty: Boolean get() = this == null || this.isEmpty
val DynComplex?.isNullOrEmpty: Boolean get() = this == null || this.isEmpty
val DComplex?.isNullOrEmpty: Boolean get() = this == null || this.isEmpty
val VarSet?.isNullOrEmpty: Boolean get() = this == null || this.empty

val Exp.sp: Space get() = this.sp()
val Cube.sp: Space get() = this.space

//fun String.toCubes(space: Space): Set<Cube> = space.expFactory.parseCubesToDynCubeSet(this)
//fun String.toCubes(space: Space): Set<Cube> = space.parser.expFactory.parseCubesToDynCubeSet(this)

fun String.toCubes(sp: Space): Set<Cube> = sp.parser.parseCubes(this)


fun String?.toCube(sp: Space): Cube {
    return if (this == null || this.trim().isEmpty()) sp.mkEmptyCube()
//    else space.expFactory.parseDynCube(this)
    else sp.parser.parseLitsToDynCube(this)
}

fun String.toCubes(hasSpace: HasSpace): Set<Cube> = toCubes(hasSpace.space)

fun String?.toCube(hasSpace: HasSpace): Cube = toCube(hasSpace.space)

