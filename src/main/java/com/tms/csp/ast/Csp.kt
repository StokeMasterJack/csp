package com.tms.csp.ast

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableList
import com.google.common.collect.Multimap
import com.tms.csp.*
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.formula.Formula
import com.tms.csp.data.CspSample
import com.tms.csp.fm.dnnf.Dnnf
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.transforms.Transformer
import com.tms.csp.util.*
import com.tms.csp.util.varSets.VarSet
import com.tms.csp.util.varSets.VarSetBuilder
import java.util.*
import java.util.logging.Logger


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

        var simple: DynCube? = null,
        var complex: DynComplex? = null,
        var dontCares: VarSet? = null,

        add: Add? = null

) : HasSpace {


    var _failure: Any? = null

    private var q: MutableList<Lit>? = null

    val parser: Parser get() = space.parser

    var formula: Exp? = null


    init {
        if (add != null) {
            add.addConstraints(this)
        }
    }

    //initial:

//    constructor(constraints: String, tiny: Boolean) : this(space = space)

    //    constructor(parent = null, space, mixed) : this(mixed.space, add = Action(c = mixed.complex, cc = mixed.simple))
//
//
//    constructor(mixed: And) : this(mixed.space, add = Action(c = mixed.complex, cc = mixed.simple))
//
//
//    constructor(space:Space) : this(space = space)
//
//
//    constructor(c: Iterable<Exp>, cc: Lit) : this(Constraints(c), Condition(cc))
//    constructor(c: DynComplex, cc: Cube) : this(Constraints(c), Condition(cc))
//
//
    @JvmOverloads
    constructor(that: Csp) :
            this(
                    space = that.space,
                    simple = that.copySimple(),
                    complex = that.copyComplex(),
                    dontCares = that.copyDontCares()
            )

    //constructor(mixed: And) : this(mixed.space, add = Add(c = mixed.complex, cc = mixed.simple))


    fun copySimple(): DynCube? {
        return simple?.copy()
    }

    fun copyComplex(): DynComplex? {
        return if (complex == null) {
            return null;
        } else {
            complex!!.copy();
        }
    }

    fun copyDontCares(): VarSet? {
        return if (dontCares == null) {
            return null;
        } else {
            dontCares!!.copy();
        }
    }

    fun containsConstraint(e: Exp): Boolean {
        return if (e.isSimple) {
            if (simple != null) {
                simple!!.containsLit(e.asLit())
            } else {
                false
            }

        } else if (e.isComplex) {
            if (complex != null) {
                complex!!.contains(e)
            } else {
                false
            }
        } else {
            throw IllegalStateException()
        }
    }


    @JvmOverloads
    @Throws(AlreadyFailedException::class)
    fun addConstraint(expText: String?, condition: Condition = Condition.identity) {
        if (isFailed) {
            throw AlreadyFailedException(_failure.toString() + "")
        }

        val exp: Exp? = parser.parseExpOrNull(expText)
        if (exp != null) {
            addConstraint(exp, condition)
        }

    }

    fun varsNonNull(): Boolean {
        return simple != null && complex != null && dontCares != null
    }

    fun varsNulledOut(): Boolean {
        return simple == null && complex == null && dontCares == null && q == null
    }

    val isSolved: Boolean get() = !isFailed && complex.isNullOrEmpty

    val isFailed: Boolean get() = _failure != null


//    fun assertCareVars(constraintJustAdded: String? = null): Boolean = if (c.complex != null) {
//        c.complex.assertVars(constraintJustAdded);
//    } else {
//        true;
//    }


    @Throws(AlreadyFailedException::class, ConflictingAssignmentException::class)
    fun addConstraint(constraint: Exp, condition: Condition): Boolean {
        assert(space === constraint.sp())
        if (isFailed) {
            throw AlreadyFailedException(_failure.toString())
        }


        val simplified: Exp = condition.condition(constraint, log = false, depth = depth)
        return addConstraint(simplified)
    }

    @Throws(AlreadyFailedException::class, ConflictingAssignmentException::class)
    fun addConstraint(constraint: Exp): Boolean {
        assert(space === constraint.sp())
        if (isFailed) {
            throw AlreadyFailedException(_failure.toString() + " " + constraint.toString())
        }

        return if (constraint.isTrue) {
            //ignore
            false

        } else if (constraint.isFalse) {
            failCspFalseConstraintAdded()
            true;
        } else if (constraint.isLit) {
            assign(constraint.asLit())
        }
//        else if (cc.isNot) {
//            println(cc)
//            if (true) throw IllegalStateException()
//            val flip = cc.pushNotsIn();
//            assert(flip.isPos)
//            addConstraint(flip);


//        }

        else if (constraint.isDcOr) {
            addDontCare(constraint.asDcOr())
        } else if (constraint.isAnd) {
            val posComplex = constraint.asPosComplex()
            var ch = false
            for (arg: Exp in posComplex.args) {
                val ch1 = addConstraint(arg)
                if (ch1) ch = true
                if (isFailed) break
            }
            ch
        } else {
            addComplexConstraint(constraint)
        }
    }

    private fun addComplexConstraint(cc: Exp): Boolean {
        assert(!cc.isAnd)

        if (this.hasSimple) {
            val ss: Exp = ExpFactory.maybeSimplifyComplex(simple!!, cc, disjoint = Bit.OPEN)
            if (ss != cc) {
                return addConstraint(ss);
            }
        }

        if (cc.isFlattenableNand) {
            val and = cc.asNand().flattenNand()
            return addConstraint(and)
        }

        return mkComplex.add(cc)


    }


    private val mkComplex: DynComplex
        get() {
            if (complex == null) {
                complex = space.mkComplex()
            }
            return complex!!
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

    fun satCountPL(): Long {
        return satCountPL(vars.plusVarSet(dontCares))
    }


    fun satCountPL(parentVars: VarSet): Long {

        propagate()

        return if (isFailed) {
            0
        } else if (isSolved) {
            computeDcVars(1, parentVars.minus(simpleVars), vars)
        } else {
            val ff = mkFormula()
            val minus = parentVars.minus(simpleVars);
            ff.satCountPL(minus)


        }


    }


    val anyVarOverlap: Boolean
        get() {
            if (simple.isNullOrEmpty) return false
            if (complex.isNullOrEmpty) return false

            val simpleVars = simple!!.vars
            val complexVars = complex!!.vars

            return complexVars.anyVarOverlap(simpleVars)


        }


    val isSimpleComplexDisjoint: Boolean get() = !anyVarOverlap


    fun getSatCount(): Long {
        return toDnnf().smooth.satCount
    }

    fun mkFormula(): Exp {
        if (formula == null) {
            formula = createFormula()
        }
        return formula!!
    }

    private fun createFormula(): Exp {
        propagate()
        assert(isStable)
        if (isFailed) {
            return space.mkFalse()
        } else if (isSolved) {
            return space.mkTrue()
        }
        assert(!complex.isNullOrEmpty)
        return complex!!.mkFormula()

    }


//    fun simpleIntersectionWith(cc: Exp?): Cube? = simple?.intersection(cc)
//    fun simpleIntersectionWith(cc: DynComplex?): Cube? = simple?.intersection(cc)

//    fun simpleComplexIntersection(): Cube? = simpleIntersectionWith(complex)

//    private fun maybeSimplifyComplex(cc: Exp): Exp {
//        assert(!cc.isAnd)
//        assert(cc.isComplex)
//
//        val ci: Cube? = simpleIntersectionWith(cc)
//        if (ci.isNullOrEmpty) return cc
//
//        val sz = ci!!.size
//        return when {
//            sz == 1 -> {
//                val firstLit: Lit = ci.firstLit
//                cc.condition(firstLit)
//            }
//            sz > 1 -> cc.condition(ci)
//            else -> throw IllegalStateException()
//        }
//
//
//    }


    fun computeUnionFind(): UnionFind {
        val ff = mkFormula()
        val f = ff.asFormula()
        return f.computeUnionFind();
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

    val isStableDeep: Boolean get() = !isDirtyDeep

    val isDirtyDeep: Boolean
        get() {
            if (isDirty) return true

            if (!hasSimple || !hasComplex) return false

            val vars1 = simple!!.vars
            val vars2 = complex!!.vars

            return vars1.anyVarOverlap(vars2)
        }

    val isAllSimple: Boolean get() = hasSimple && !hasComplex;
    val isAllComplex: Boolean get() = !hasSimple && hasComplex;

    fun propagate(): Boolean {
        var anyChange = false
        while (isDirty) {
            val lit = q!!.removeAt(0)
            assert(simple!!.containsLit(lit))

            val ch = if (complex.isNullOrEmpty) {
                //solved
                complex = null;
                q = null
                false
            } else {
                checkNotNull(complex);
                val tmp: DynComplex = complex!!
                this.complex = null
                val add = Add(tmp, lit)
                add.addConstraints(this);
            }

            if (ch) anyChange = true;
        }

        fixDataStructures()
        return anyChange;
    }

    fun propagateIntersection(): Boolean {
        if (complex.isNullOrEmpty || simple.isNullOrEmpty) return false;


        val simpleVars = simple!!.vars
        val complexVars = complex!!.vars

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
            val ch = assign(lit)
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

    fun toDnnfInternalK(): Exp {

        propagate()




        return if (isFailed) {
            space.mkFalse()
        } else if (!hasSimple && !hasComplex) {
            space.mkTrue()
        } else if (!hasSimple && hasComplex) {
            mkComplex.toDnnf()
        } else if (hasSimple && !hasComplex) {
            simple!!.toDnnf()
        } else if (hasSimple && hasComplex) {

            assert(simple != null)
            assert(complex != null)

            val dSimple = simple!!.toDnnf() //T|F|Lit|Cube

            when {
                dSimple.isFalse -> space.mkFalse()
                dSimple.isTrue -> complex!!.toDnnf()
                else -> {
                    assertDisjointSimpleComplex()
                    val dComplex = complex!!.toDnnf()
                    when {
                        dComplex.isTrue -> dSimple
                        dComplex.isFalse -> space.mkFalse()
                        else -> {
                            //both simple and complex are non-constant and disjoint
                            assert(dSimple.isOpen)
                            assert(dComplex.isOpen)
                            assert(dSimple.isVarDisjoint(dComplex))
                            assert(dSimple.isDnnf)
                            assert(dComplex.isDnnf)
                            //                    assert dSimple.checkDnnf();
                            //                    assert dComplex.checkDnnf();

                            space.mkDAnd(dSimple, dComplex)
                        }
                    }
                }


            }
        } else {
            throw IllegalStateException()
        }
    }

    fun toPLExp(): Exp {
        propagate()

        propagateIntersection();

        return if (isFailed) {
            space.mkFalse()
        } else if (!hasSimple && !hasComplex) {
            space.mkTrue()
        } else if (!hasSimple && hasComplex) {
            mkComplex.mkFormula()
        } else if (hasSimple && !hasComplex) {
            simple!!.mkCubeExp()
        } else if (hasSimple && hasComplex) {

            assert(simple != null)
            assert(complex != null)


            val ss = simple!!.mkCubeExp()
            val cc = complex!!.mk()


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

    fun toDnnfInternal(): Exp {
        val d: Exp = toDnnfInternalK()
        return if (dontCares != null && dontCares!!.isNotEmpty()) {
            addDcOrs(d, dontCares!!)
        } else {
            d
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


    fun fixDataStructures() {
        if (complex != null && complex!!.isEmpty) complex = null;
        if (simple != null && simple!!.isEmpty) simple = null;
        if (dontCares != null && dontCares!!.isEmpty()) dontCares = null;
    }


    val depth: Int get() = 0  //todo


    fun assertDisjointSimpleComplex() {
        assert(isSimpleComplexDisjoint);
    }

    fun assertDisjointDc() {
        assert(isDcDisjoint)
    }

    fun anySimpleComplexOverlap(): Boolean {
        return !isSimpleComplexDisjoint;
    }


    fun getXorsDeep(): List<Exp> {
        if (complex.isNullOrEmpty) return emptyList()
        val b = mutableListOf<Exp>()
        for (exp in complex!!.argIt) {
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

    fun getConstraintsContainingXor(): List<Exp> {
        if (complex.isNullOrEmpty) return ImmutableList.of()
        val b = mutableListOf<Exp>()
        for (exp in complex!!) {
            if (exp.isXorOrContainsXor) {
                b.add(exp)
            }
        }
        return b
    }

    val isDcDisjoint: Boolean
        get() {
            return if (dontCares == null) true
            else dontCares!!.isVarDisjoint(simple) || dontCares!!.isVarDisjoint(complex)
        }

    val failure: Any? get() = _failure

    /**
     * copies only the complex constraints to a new csp
     * and creates a new trimmed the space that references only unassigned _vars
     */
    fun reduce(): Csp {
        propagateIntersection()
        val space = Space(complexVars)
        val strSeq: Sequence<String> = complexConstraintsSer

        val parser = space.parser
        val expSeq = strSeq.map { parser.parseExpOrNull(it) }.filterNotNull()


        val add = Add(expSeq, space = space)
        return Csp(space, add = add)
    }

    fun checkState() {
        if (isFailed) {
            assert(simple == null && complex == null && dontCares == null)
        }
    }


    fun addConstraint(imp: Imp) {
        System.err.println("**** addConstraint-Imp")
        val arg1 = imp.arg1
        val arg2 = imp.arg2
        addBinaryOr(arg1.flip(), arg2)
    }

    fun addConstraint(rmp: Rmp) {
        System.err.println("**** addConstraint-Rmp")
        val arg1 = rmp.arg1
        val arg2 = rmp.arg2
        addBinaryOr(arg1, arg2.flip())
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
        addBinaryOr(arg1.flip(), arg2)
    }

    fun addRmp(arg1: Exp, arg2: Exp) {
        System.err.println("**** addRmp")
        addBinaryOr(arg1, arg2.flip())
    }

    fun addIff(arg1: Exp, arg2: Exp) {
        System.err.println("**** addIff")
        if (arg1 === arg2) return


        if (arg1.isFalse && arg2.isFalse) return
        if (arg1.isTrue && arg2.isTrue) return

        if (arg1.isFalse && arg2.isTrue) {
            failCspFalseConstraintAdded()
            return
        }

        if (arg1.isTrue && arg2.isFalse) {
            failCspFalseConstraintAdded()
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
            addConstraint(arg2.flip())
            return
        }

        if (arg1.isOpen && arg2.isFalse) {
            addConstraint(arg1.flip())
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

        if (arg1 === arg2.flip()) {
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


    fun nullVars() {
        dontCares = null
        complex = null
        simple = null
        q = null
    }


    fun failCspConstraintSimplifiedToFalse(before: Exp, lit: Exp) {
        fail(CspFailureConstraintSimplifiedToFalse(before, lit))
    }


    fun failCspFalseConstraintAdded() {
        //        if (true) throw new IllegalStateException();
        fail(CspFailureFalseConstraintAdded())
    }


    fun fail(ff: Any?) {
        if (_failure != null) {
            throw AlreadyFailedException()
        }
        assert(_failure == null)
        _failure = ff
        nullVars()
    }


    fun assign(sLit: String): Boolean {
        val lit = space.mkLit(sLit)
        return assign(lit)
    }

    fun assign(`var`: Var, value: Boolean): Boolean {
        return assign(`var`.getVarId(), value)
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

    @Throws(AlreadyFailedException::class, ConflictingAssignmentException::class)
    fun assign(lit: Lit): Boolean {
        checkNotNull(lit)
        varsNulledOut()
        if (isFailed) {
            throw AlreadyFailedException()
        }
        val ch: Boolean
        try {
            ch = mkSimple.assign(lit)
        } catch (e: ConflictingAssignmentException) {
            fail(e)
            return false
        }



        if (ch) {
            enqueue(lit)
        }

        return ch
    }

    private fun enqueue(vr: Var, sign: Boolean) {
        enqueue(vr.lit(sign))
    }

    private fun enqueue(lit: Lit) {
        if (q == null) q = mutableListOf()
        q!!.add(lit)
    }


    @Throws(ConflictingAssignmentException::class)
    fun assign(signedVarId: Int): Boolean {
        val lit = space.mkLit(signedVarId)
        return assign(lit)
    }

    val hasComplex: Boolean get() = !complex.isNullOrEmpty
    val hasDontCares: Boolean get() = !dontCares.isNullOrEmpty

    val hasSimple: Boolean get() = !simple.isNullOrEmpty

    val cube: Cube = if (hasSimple) simple!! else space.emptyCube


    fun addDontCare(varCode: String): Boolean {
        return mkDontCares.add(space.getVar(varCode))
    }

    fun addDontCare(vr: Var): Boolean {
        return mkDontCares.add(vr)
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

    fun refine(vararg sLits: String): Csp {
        val copy = copy()
        for (sLit in sLits) {
            val r = space.mkLit(sLit)
            copy.assign(r)
            copy.propagate()
        }
        return copy
    }

    fun refine(lit: Lit): Csp {
        val copy = copy()
        copy.assign(lit)
        copy.propagate()
        return copy
    }

    fun refine(lits: Iterable<Lit>): Csp {
        val copy = copy()
        for (lit in lits) {
            copy.assign(lit)
            copy.propagate()
        }
        return copy
    }


    fun refine1(sLits: String): Csp {
        val parser = space.parser
        val exp = parser.parseExp("and($sLits)")
        return when {
            exp.isCube -> refine(exp.litItFromExpArray())
            exp.isLit -> refine(exp.asLit())
            else -> throw IllegalArgumentException()
        }


    }

    fun refine1(lit: Lit): Csp {
        propagate()
        val copy = copy()
        copy.assign(lit)
        copy.propagate()
        return copy
    }


    val complexDyn: DynComplex? get() = complex

    val complexIt: Iterable<Exp>
        get() {
            return if (hasComplex) {
                complex!!.argIt
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

    val complexSeq: Sequence<Exp> get() = if (complex == null) emptySequence() else complex.asSeq()

    val complexConstraintsSer: Sequence<String> get() = complexSeq.map { it.serialize() }

    val complexVars: VarSet get() = complex?.vars ?: space.mkEmptyVarSet()
    val simpleVars: VarSet get() = simple?.vars ?: space.mkEmptyVarSet()

    val careVars: VarSet get() = complexVars;

    fun simplifySeriesModelAnds() {
        if (!hasComplex) return;
        val tmp = complex!!;
        complex = space.mkComplex()
        for (e in tmp.argIt) {
            val s = e.simplifySeriesModelAnd();
            addConstraint(s);
        }
    }

    override fun toString(): String {
        return "SpaceCsp";
    }

    fun print() {
        println("depth: " + depth)
        val sDontCares = if (dontCares.isNullOrEmpty) "" else dontCares.toString()
        val sSimple = if (simple.isNullOrEmpty) "" else simple.toString()
        println("<csp>");
        println("  <dontCares>$sDontCares</dontCares>");
        println("  <simple>$sSimple</simple>");
        val cc = complexConstraintCount
        if (cc == 0) {
            println("  <complex></complex>");
        } else if (cc == 1) {
            println("  <complex>" + complexIt.first() + "</complex>");
        } else {
            println("  <complex>");
            for (exp: Exp in complexIt) {
                println("    $exp");
            }
            println("  </complex>");
        }
        println("  <vars>$vars</vars>");
        println("  <sat>" + isSat() + "</sat>");
        println("<csp>");
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


    fun isSat(): Boolean {
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
        val bb = mkFormula().getBB()
        return DynCube.union(space, simple, bb)
    }

//    fun getBBLite(): DynCube {
//        propagate()
//        val bb: DynCube = mkFormula().asFormula().computeBBLite()
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

    fun proposeBothWaysLite(vr: Var): Lit? {


        val pLit = vr.mkPosLit()
        val nLit = vr.mkNegLit()

        val p = Add(c = complex!!, cc = pLit)
        val n = Add(c = complex!!, cc = nLit)

        val tt: Csp = Csp(space, add = p)

        if (tt.isFailed) {
            //must be f
            //            System.err.println("  found bb lit[" + vr.mkNegLit() + "]");
            return vr.nLit()
        } else {

            val ff = Csp(space = space, add = n)
            return if (ff.isFailed) {
                //must be t
                //                System.err.println("  found bb lit[" + vr.mkPosLit() + "]");
                vr.pLit()
            } else {
                null //open
            }
        }
    }

    fun refineFormulaOnly(sLit: String): Exp {
        if (!hasComplex) {
            return space.mkTrue()
        }
        val a = Add(c = copyComplex()!!, cc = space.mkLit(sLit))
        val csp = Csp(space, add = a)
        csp.propagate()
        csp.propagateIntersection()
        return csp.toPLExp()
    }


    fun _assignSafe(lit: Lit) {
        mkSimple.assignSafe(lit)
    }

    fun _assignSafe(cube: Cube) {
        mkSimple.assignSafe(cube)
    }

    fun getVVPlusConstraints(): List<Exp> {
        if (complex.isNullOrEmpty) return emptyList()
        val b = mutableListOf<Exp>()
        for (exp in complexIt) {
            if (exp.isVVPlus()) {
                b.add(exp);
            }
        }
        return b
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


    fun simplifyBasedOnVvs() {

        if (complex.isNullOrEmpty) {
            return
        }

        propagate()

        val vvs = vvConstraints

        val copy = complex!!.copy();

        val ff = DynComplex(space)

        for (e in copy) {
            if (e.isXorOrContainsXor || e.isIffOrContainsIff) {
                ff.add(e)
            } else if (e.isVVPlus) {
                val subsumedVVs = CspOldJava.findSubsumedVVs(e, vvs)
                if (subsumedVVs.isEmpty()) {
                    ff.add(e)
                } else {
                    val s = e.conditionVVs(subsumedVVs)
                    if (s !== e) {
                        addConstraint(s)
                        logSimplified(subsumedVVs, e, s)
                        if (isFailed) throw IllegalStateException("Failed after adding constraint[$e]")
                    } else {
                        ff.add(e)
                    }
                }
            } else if (e.isVv) {
                ff.add(e)
            } else {
                throw IllegalStateException()
            }

        }

        if (ff != copy) {
            System.err.println("repeat")
            simplifyBasedOnVvs()
        }

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
        if (true && before !== after) {
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
        //        assert space.posComplexTable != null;
        val n = toDnnfInternal()
        assert(n.isDnnf)
        return n
    }

    fun getCubeVars(): VarSet = if (simple.isNullOrEmpty) space.mkEmptyVarSet() else simple!!.vars


    val vars: VarSet get() = VarSet.union(space, simple?.vars, complex?.vars, careVars)


    val size: Int get() = simpleConstraintCount + complexConstraintCount

    val simpleConstraintCount: Int get() = simple?.size ?: 0
    val complexConstraintCount: Int get() = complex?.size ?: 0

    fun simplifyAlwaysTrueVars() {
        maybeAddAlwaysTrueVars()
        propagate()
    }

    fun maybeAddAlwaysTrueVars() {
        for (atVarCode in Space.alwaysTrueVars1) {
            if (space.containsVarCode(atVarCode)) {
                addConstraint(expText = atVarCode, condition = Condition.identity)
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
            complex!!.argIt.sortedBy(selector)
        } else {
            emptyList()
        }
    }


    fun getExpWithLargestAnd(): Exp? {
        return if (!hasComplex) {
            null as Exp?
        } else {
            var best: Exp? = null
            var bestAnd: Exp? = null
            for (exp in complex!!) {
                if (bestAnd == null) {
                    best = exp
                    bestAnd = exp.getAndWithHighestLitArgCount()
                } else {
                    val and = exp.getAndWithHighestLitArgCount()
                    if (and != null && and.getAndLitArgCount() > bestAnd.andLitArgCount) {
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


    fun tryForDeeperInference() {
        if (isFailed) return
        if (isSolved) return

        val exp: Exp = mkFormula()
        if (!exp.isFormula) {
            return
        }
        val f: Formula = exp.asFormula()

        val max = 5

        val complexCountBefore = complexConstraintCount


        val fVars = f.fVars
        val fVarList = fVars.sortedFVarList

        val L = Math.min(max, fVarList.size)

        for (i in 0 until L) {
            val fVar: FVar = fVarList.get(i)
            val vr = fVar.vr
            val lit = proposeBothWaysLite(vr)
            if (lit != null) {
                val assigned = assign(lit)
                if (isFailed) {
                    return
                }
            }
        }

        propagate()

        val complexCountAfter = complexConstraintCount
        val redux = complexCountBefore - complexCountAfter

        if (isFailed) {
            //            System.err.println("Failed");
        } else if (isSolved) {
            //            System.err.println("Solved");
        } else if (redux > 0) {
            //            System.err.println("Redux: " + redux);
        }
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


        this.complex = space.mkComplex()

//        System.err.println("Transforming " + tmp.size() + " sentences!");
        var current = 0;
        for (e: Exp in ff.argIt()) {
            System.err.println("  Transforming [" + current + "] of " + ff.size());
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

    fun serializeTinyCnfSimple(a: Ser) {
        throw UnsupportedOperationException();
    }

    fun serializeTinyCnfComplex(a: Ser) {
        if (complex.isNullOrEmpty) return;
        for (e: Exp in complexIt) {
            e.serializeTinyCnf(a);
            a.newLine();
        }
    }


/*
 Formula tmp = this.getFormula().asFormula();

    complex = new DynFormula(space);
    for (Exp b : tmp) {
        if (isFailed()) {
            return;
        }
        Exp a = t.transform(b);
        addConstraint(a);
    }
 */

    fun toNnf() {
        assert(!hasSimple)
        propagate();
        assert(!hasSimple)

        if (!hasComplex) return;

        val tmp = complex!!;

        complex = space.mkComplex()

        for (e in tmp) {
            val bnf = e.toBnf()
            val nnf = bnf.transform(Transformer.BNF_TO_NNF);
            addConstraint(nnf);
        }

        assert(!hasSimple)
    }


    fun toNnf2() {
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
        for (constraint in complex!!.argIt) {
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

    fun getXorConstraints(): List<Exp> = Csp.getXorConstraints(complexIt)

    fun getXor(prefix: String): Xor? {
        val xors: List<Exp> = getXorConstraints()
        for (xor in xors) {
            if (xor.argCount == 0) {
                throw IllegalStateException()
            }
            val arg = xor.getArg(0)
            if (arg.isVarWithPrefix(prefix)) {
                return xor.asXor()
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
        for (yearExp in yearXor!!.argIt()) {
            val yearVarCode = yearExp.varCode

            val yearCsp = refine1(yearVarCode)
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


    fun toCnfSortedSet(): TreeSet<TreeSet<String>> {
        assert(isCnf)

        val clauseSortedSetComparator = ClauseSortedSetComparator()
        val ss = TreeSet<TreeSet<String>>(clauseSortedSetComparator);

        if (hasComplex && complex != null) {
            for (e: Exp in complexIt) {
                assert(e.isClause)
                val s: TreeSet<String> = e.clauseToSortedSet();
                ss.add(s);
            }


        }

        return ss

    }

    companion object {


        fun computeDcVars(baseSatCount: Long, parentVars: VarSet, myVars: VarSet): Long {
            val dcVars = parentVars.minus(myVars)
            val pow = Math.pow(2.0, dcVars.size.toDouble()).toLong()
            return baseSatCount * pow
        }


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
        fun getXorConstraints(args: Iterable<Exp>): List<Exp> {
            val aa = mutableListOf<Exp>()
            for (exp in args) {
                if (exp.isXor) {
                    aa.add(exp)
                }
            }
            return aa
        }

        @JvmStatic
        @JvmOverloads
        fun compileDnnf(clob: String, tiny: Boolean = false): Exp {
            return parse(clob, tiny).toDnnf()
        }


        @JvmStatic
        @JvmOverloads
        fun toDnnf(clob: String, tiny: Boolean = false): Exp {
            return compileDnnf(clob, tiny)
        }


        @JvmStatic
        @JvmOverloads
        fun compileDnnf(sample: CspSample, tiny: Boolean = false): Exp {
            return compileDnnf(sample.loadText(), tiny)
        }

        @JvmStatic
        @JvmOverloads
        fun toDnnfCsp(clob: String, tiny: Boolean = false): Dnnf {
            val d = compileDnnf(clob, tiny)
            return Dnnf(d);
        }


        //create

        @JvmStatic
        @JvmOverloads
        fun parse(clob: String, tiny: Boolean = false): Csp = Parser.parseCsp(clob, tiny);

        @JvmStatic
        @JvmOverloads
        fun parse(cspSample: CspSample, tiny: Boolean = false): Csp {
            return parse(cspSample.loadText(), tiny);
        }

    }//end companion

    fun printVarInfo() {
        val varMap = space.getVarSpace();
        varMap.printVarInfo();
    }


    fun conditionOutAtVars() {
        simplifyAlwaysTrueVars();
    }

    fun removeConstraint(constraint: Exp): Boolean {
        //        System.err.println("Removing constraint: " + constraint);
        return if (constraint.isSimple) {
            removeSimple(constraint)
        } else if (constraint.isComplex) {
            removeComplex(constraint)
        } else {
            throw IllegalArgumentException()
        }
    }

    fun removeSimple(lit: Exp): Boolean {
        if (simple.isNullOrEmpty) return false
        assert(lit.isSimple)
        return simple!!.removeLit(lit.asLit())
    }

    fun removeComplex(exp: Exp): Boolean {
        if (complex.isNullOrEmpty) return false
        assert(exp.isComplex)
        val contains = complex!!.contains(exp)
        val removed = complex!!.remove(exp)
        assert(contains == removed)

        return removed
    }

    fun removeComplex(expText: String): Boolean {
        val exp = space.parseExp(expText)
        return removeComplex(exp)
    }

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
val VarSet?.isNullOrEmpty: Boolean get() = this == null || this.empty

val Exp.sp: Space get() = this.sp()
val Cube.sp: Space get() = this.space

private val log = Logger.getLogger(Csp::class.simpleName)

//fun String.toCubes(space: Space): Set<Cube> = space.expFactory.parseCubesToDynCubeSet(this)
//fun String.toCubes(space: Space): Set<Cube> = space.parser.expFactory.parseCubesToDynCubeSet(this)

fun String.toCubes(sp: Space): Set<Cube> = sp.parser.parseCubes(this)


fun String?.toCube(sp: Space): Cube {
    return if (this == null || this.trim().isEmpty()) sp.mkEmptyCube()
//    else space.expFactory.parseDynCube(this)
    else sp.parser.parseDynCube(this)
}

fun String.toCubes(hasSpace: HasSpace): Set<Cube> = toCubes(hasSpace.space)

fun String?.toCube(hasSpace: HasSpace): Cube = toCube(hasSpace.space)



