package com.tms.csp.util

import com.google.common.collect.Iterators
import com.tms.csp.argBuilder.IArgBuilder
import com.tms.csp.ast.*
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.ints.TreeSequence
import com.tms.csp.util.it.ExpFilterIterator
import com.tms.csp.util.varSets.VarSet


class DynComplex constructor(val space: Space) : IArgBuilder, PLConstants, Iterable<Exp> {

    //For And
    //    null:   constantFalse
    //    empty:  constantTrue
    var args: TreeSequence<Exp>? = null
    var _vars: VarSet? = null

    override var isFcc: Boolean? = null

    constructor(sp: Space, args: Iterable<Exp>) : this(sp) {
        addExpIt(args)
    }

    constructor(sp: Space, args: Array<Exp>) : this(sp) {
        addExpArray(args)
    }

    constructor(that: DynComplex) : this(that.space) {
        this.args = that.args?.copy()
        _vars = that._vars?.copy()
        isFcc = that.isFcc
    }

    val isEmpty: Boolean
        get() = args == null || args!!.isEmpty

    val isConstantTrue: Boolean
        get() = isEmpty


    val firstArg: Exp
        get() = args!!.first().value()

    fun first(): Exp = firstArg

    override val size: Int get() = if (args == null) 0 else args!!.size()

    override val op: Op
        get() = Op.Formula

    override val op1: Op1
        get() = op.op1

    override val argIt: Iterable<Exp>
        get() = Iterable { this.argIter() }

    override fun iterator(): Iterator<Exp> = argIter()


    fun copy(): DynComplex {
        return DynComplex(this)
    }


    fun addExpArray(args: Array<Exp>) {
        for (arg in args) {
            add(arg)
        }
    }

    fun addExpIt(args: Iterable<Exp>) {
        for (arg in args) {
            add(arg)
        }
    }

    fun assertVars(): Boolean {
        return assertVars("")
    }

    fun assertVarsMsg(vars1: VarSet, vars2: VarSet, constraintJustAdded: String): String {
        return "\n  _vars[$vars1]\n  computed[$vars2]\n  constraint[$constraintJustAdded]"
    }

    private fun assertVars(constraintJustAdded: String): Boolean {
        val vars1 = _vars
        val vars2 = _computeVars()

        if (vars1 == null) {
            throw IllegalStateException()
        }

        val chk = vars1 == vars2

        assert(chk) { assertVarsMsg(vars1, vars2, constraintJustAdded) }
        return true
    }

    private val mkArgs: TreeSequence<Exp>
        get() {
            args = args ?: TreeSequence()
            return args!!
        }

    fun add(e: Exp): Boolean {
        requireNotNull(e)
        if (e.isAnd) throw IllegalArgumentException("Ands should be added at the csp level")

        val old = mkArgs.put(e.getExpId(), e)
        val ch = old == null
        return if (!ch) {
            assert(old!!.expId == e.expId)
            //            System.err.println("Put Error:");
            //            System.err.println("  new: " + e.getExpId() + ": " + e);
            //            System.err.println("  old: " + old.getExpId() + ": " + old);
            //            boolean sameSpace1 = e.getSpace() == old.getSpace();
            //            boolean sameSpace2 = getSpace() == old.getSpace();
            //            System.err.println("  sameSpace1[" + sameSpace1 + "]");
            //            System.err.println("  sameSpace2[" + sameSpace2 + "]");
            //            getSpace().check();

            //            System.err.println("Dup complex constraint: " + old);
            false
        } else {
            addVars(e)
            true
        }


    }

    private fun addVars(e: Exp) {
        this._vars = VarSet.union(space, _vars, e.vars);
    }


//    /**
//     * @return an iterator over this.args, formula the increasing order of ids.
//     */
//    override fun iterator(): Iterator<Exp> = expIter()

    fun expIter(): Iterator<Exp> = args.expIter()

    fun argIter(): Iterator<Exp> = args.expIter()

    fun computeHash(): Int {
        var sum = 0
        for (arg in argIt) {
            sum += arg.hashCode()
        }
        return sum
    }

    private val a: TreeSequence<Exp> get() = args!!

    fun remove(arg: Exp): Boolean {
        return if (args == null) {
            false
        } else {
            val expId = arg.getExpId()
            val removed: Exp? = args!!.remove(expId)
            val ch = removed != null
            if (ch) {
                _vars = null
            }
            return ch
        }

    }

    fun removeAll(complex: Iterable<Exp>): Boolean {
        var ch = false
        for (exp in complex) {
            if (remove(exp)) {
                ch = true
            }
        }
        return ch
    }


    fun eq(that: DynComplex): Boolean {
        return args == that.args
    }

    operator fun contains(e: Exp): Boolean {
        return args!!.containsIndex(e.expId)
    }

    operator fun get(expId: Int): Exp? {
        if (args == null) {
            throw UnsupportedOperationException()
        }
        return args!!.get(expId)
    }

    private fun _computeVars(): VarSet {
        return if (isEmpty) {
            space.mkEmptyVarSet()
        } else {
            space.varSetBuilder().apply { argIt.forEach { addVarSet(it.vars) } }.build()
        }
    }

    val vars: VarSet
        get() {
            if (_vars == null) {
                _vars = _computeVars();
            }
            return _vars!!
        }


    fun remove(o: Any): Boolean {
        return remove(o as Exp)
    }

    fun clear() {
        _vars = null
        args = null
    }


    fun toDnnf(): Exp {
        if (isEmpty) return space.mkTrue()
        val exp = mkExp()
        val dnnf = exp.toDnnf()

        assert(dnnf.isDnnf)
        //        assert dnnf.checkDnnf();
        return dnnf
    }


    fun vvIt(): Iterable<Exp> {
        return object : Iterable<Exp> {
            override fun iterator(): Iterator<Exp> {
                return vvIterator()
            }
        }
    }

    fun vvIterator(): Iterator<Exp> {
        return if (isEmpty) Iterators.emptyIterator() else object : ExpFilterIterator(argIter()) {
            override fun accept(e: Exp): Boolean {
                return e.isVv
            }
        }

    }

    fun vvpIt(): Iterable<Exp> {
        return object : Iterable<Exp> {
            override fun iterator(): Iterator<Exp> {
                return vvpIterator()
            }
        }
    }

    fun vvpIterator(): Iterator<Exp> {
        return object : ExpFilterIterator(argIter()) {
            override fun accept(e: Exp): Boolean {
                return e.isVVPlus
            }
        }
    }

    override fun mk(): Exp = when {
        isEmpty -> space.mkConstantTrue()
        size == 1 -> firstArg
        else -> space.mkPosComplex(this)
    }

    fun mkExp(): Exp = mk()

    fun mkFormula(): Exp = mk()

    override fun toString(): String {
        val a = Ser()
        Exp.serializeArgList(a, argIt)
        return a.toString()
    }


    fun containsVar(vr: Var): Boolean {
        return _vars?.containsVar(vr) ?: false
    }

    fun anyVarOverlap(lit: Lit): Boolean {
        return _vars!!.anyVarOverlap(lit)
    }

    fun anyVarOverlap(cube: Cube): Boolean {
        return _vars!!.anyVarOverlap(cube)
    }

    fun isVarDisjoint(simple: DynCube): Boolean {
        return vars.isVarDisjoint(simple)
    }

    fun varOverlap(simple: DynCube): VarSet = vars.overlap(simple.vars)

    fun _asSeq(): Sequence<Exp> = argIt.asSequence()

    override fun createExpArray(): Array<Exp> {
        check(!isEmpty)
        check(size != 1)
        val a = args!!
        val aa: Array<Exp?> = arrayOfNulls(size)
        for ((i, arg) in argIt.withIndex()) {
            aa[i] = arg
        }
        return aa.requireNoNulls()
    }

    fun toArray(): Array<Exp> = createExpArray()

    fun toIntArray(): IntArray {
        check(!isEmpty)
        check(size != 1)
        val aa: IntArray = IntArray(size)
        for (i in 0 until size) aa[i] = args!![i].expId
        return aa
    }


    fun toList(): List<Exp> = argIt.toList()


}

fun DynComplex?.asSeq(): Sequence<Exp> {
    return this?._asSeq() ?: emptySequence()
}