package com.tms.csp.argBuilder

import com.google.common.collect.Lists.newArrayList
import com.tms.csp.ExpIt
import com.tms.csp.Structure
import com.tms.csp.TreeSeqTo
import com.tms.csp.ast.*
import com.tms.csp.ast.formula.FccState
import com.tms.csp.ast.formula.Open
import com.tms.csp.fm.dnnf.TrueOrArg
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.ints.IndexedEntry
import com.tms.csp.util.ints.TreeSequence
import com.tms.csp.util.varSets.VarSet

enum class Cause { VarConflict, TrueArg, FalseArg, TrueArgs, Empty }


class ShortCircuit(op: Op1, cause: Cause)


/**
 * builder for and args and or args
 *
 * And
 *      or:
 *          short circuits triggers:
 *              true arg    =>  simplifies to true
 *              flip args   =>  simplifies to true
 *              empty       =>  simplifies to false
 *          skip trigger:
 *              false arg => arg is removed from the or
 *          at end
 *              singleton   =>  simplifies to lit (optional)
 *
 *      and:
 *         short circuits triggers:
 *              false arg => simplifies to false
 *              flip args => simplifies to false
 *              empty     => simplifies to true
 *          skip trigger:
 *              true arg => arg is removed from the and
 *          at end
 *              singleton   =>  simplifies to lit (optional)
 *      xor:
 *        short circuits triggers:
 *           trueCount > 1          =>  simplifies to false
 *           flip var               =>  simplifies to false
 *           empty                  =>  simplifies to false
 *        skip rigger:
 *           false arg =>  arg is removed from the xor
 *        at end
 *           tCount = 1   =>    simplifies to and(!arg1 !arg2 !arg3)
 *           singleton && tCount = 0   simplifies to lit
 *
 *
 * ensures:
 *      never will contain dups
 *      never will contain flip args
 *      never will contain constants
 *
 * structure and contentModel are for cases when the programmer
 * knows more info up front. so the we can avoid certain calculations
 */


class ArgBuilder
@JvmOverloads
constructor(val sp: Space, override var op: Op = Op.And, var flatten: Boolean = true, override var fcc:FccState = Open()) : IArgBuilder {

    init {
        require(op.isAndLike || op.isOrLike || op.isXor)
    }

    var structure = Structure.Unknown

    private val simple: TreeSequence<Exp> = TreeSequence()
    private val complex: TreeSequence<Exp> = TreeSequence()
    private val all: TreeSequence<Exp> = TreeSequence()

    private var shortCircuit = false
    private var trueCount: Int = 0

    override val op1: Op1 get() = op.op1

//    var fcc: Boolean? = null


    @JvmOverloads
    constructor(sp: Space, op: Op, arg1: Exp, arg2: Exp) : this(sp, op) {
        addExp(arg1)
        addExp(arg2)
    }




    @JvmOverloads
    constructor(sp: Space, op: Op, args: Iterable<Exp>, condition: Condition = Condition.identity) : this(sp, op) {
        addExpIt(args, condition);
    }


    @JvmOverloads
    constructor(sp: Space, op: Op, args: VarSet, sign: Boolean = true, condition: Condition = Condition.identity) : this(sp, op) {
        addVarSet(args, sign, condition)
    }

    @JvmOverloads
    constructor(sp: Space, op: Op, args: Array<out Exp>, condition: Condition = Condition.identity) : this(sp, op) {
        addExpArray(args, condition)
    }

    fun op(): Op {
        return this.op
    }


    @JvmOverloads
    fun addComplex(sLits: String, condition: Condition = Condition.identity): ArgBuilder {
        val expText = "and($sLits)"
        val e1: Exp = sp.parseExp(expText)
        val e2 = condition.condition(e1)
        addExp(e2)
        return this
    }

    fun addExpArray(args: Array<out Exp>, condition: Lit): ArgBuilder {
        return addExpArray(args, Condition(condition))
    }

    fun addExpArray(args: Array<out Exp>, condition: Cube): ArgBuilder {
        return addExpArray(args, Condition(condition))
    }

    fun addExpArray(args: Array<out Exp>, condition: Condition = Condition.identity): ArgBuilder {
        if (isShortCircuit) {
            return this
        }
        for (arg1: Exp in args) {
            if (isShortCircuit) {
                return this
            }
            val arg2: Exp = condition.condition(arg1)
            addExp(arg2)
        }
        return this
    }

    @JvmOverloads
    fun addVarSet(args: VarSet, sign: Boolean, condition: Condition = Condition.identity): ArgBuilder {
        if (isShortCircuit) {
            return this
        }
        for (vr: Var in args) {
            if (isShortCircuit) {
                return this
            }
            val lit = vr.lit(sign)
            val conditioned: Exp = condition.condition(lit)
            addExp(conditioned);
        }
        return this
    }


    /*
   public void addAll(VarSet args) {
        for (Var arg : args) {
            if (isShortCircuit()) {
                return;
            }
            add(arg.mkPosLit());
        }
    }
     */


    @JvmOverloads
    fun addExp(sExp: String): ArgBuilder {
        val exp: Exp = sp.parseExp(sExp)
        return addExp(exp)
    }

    @JvmOverloads
    fun addExp(arg: Exp): ArgBuilder {
        if (isShortCircuit) {
            return this
        }
        addInternal(arg)
        if (isShortCircuit) {
            return this
        }
        return this
    }


    @JvmOverloads
    fun addLit(lit: Lit): ArgBuilder {
        if (isShortCircuit) return this
        addInternal(lit)
        if (isShortCircuit) return this
        return this
    }


    @JvmOverloads
    fun addExp(vr: Var): ArgBuilder {
        if (isShortCircuit) return this
        val lit = vr.mkPosLit()
        addLit(lit)
        if (isShortCircuit) return this
        return this
    }

    @JvmOverloads
    fun addVars(vars: Iterable<Var>, condition: Condition = Condition.identity): ArgBuilder {
        if (isShortCircuit) {
            return this
        }
        for (v: Var in vars) {
            if (isShortCircuit) {
                return this
            }
            val arg1 = v.mkPosLit();
            val arg2: Exp = condition.condition(arg1);
            addExp(arg2)
        }
        return this

    }


    @JvmOverloads
    fun addExpSeq(args: Sequence<Exp>, condition: Condition = Condition.identity): ArgBuilder {
        if (isShortCircuit) {
            return this
        }
        for (arg1: Exp in args) {
            if (isShortCircuit) {
                return this
            }
            val arg2: Exp = condition.condition(arg1);
            addExp(arg2)
        }
        return this

    }

    fun addStrSeq(args: Sequence<String?>, condition: Condition = Condition.identity): ArgBuilder {
        if (isShortCircuit) {
            return this
        }
        for (arg1: String? in args) {
            if (isShortCircuit) {
                return this
            }

            val exp1: Exp? = sp.parser.parseExpOrNull(arg1)
            if (exp1 != null) {
                val exp2: Exp = condition.condition(exp1);
                addExp(exp2)
            }
        }
        return this
    }

    @JvmOverloads
    fun addExpIt(args: Iterable<Exp>, condition: Condition = Condition.identity): ArgBuilder {
        if (isShortCircuit) {
            return this
        }
        for (arg1: Exp in args) {
            if (isShortCircuit) {
                return this
            }
            val arg2: Exp = condition.condition(arg1);
            addExp(arg2)
        }
        return this
    }


    fun addCube(cube: Cube, condition: Condition = Condition.identity): ArgBuilder {
        val expIt = cube.argIt()
        addExpIt(expIt, condition);
        return this
    }



    val simpleIt: Iterable<Exp> get() = TreeSeqTo.litIt(simple)

    val complexIt: Iterable<Exp> get() = TreeSeqTo.litIt(complex)

    val litIt: Iterable<Lit>
        //        get() = ToLitIt.fromTreeSeq(simple)
        get() = TreeSeqTo.litIt(simple)


    override val argIt: ExpIt
        get() = TreeSeqTo.expIt(all)


    fun argIterator(): Iterator<Exp> {
        return TreeSeqTo.expIter(all)
    }


    fun isCompatible(parent: Op1, child: Exp): Boolean {
        if (parent.isAnd && child.isAnd) {
            return true
        } else if (parent.isOr && child.isOr) {
            return true
        }
        return false
    }

    private fun addInternal(arg: Exp) {
        if (isShortCircuit) {
            return
        }

        if (arg.isConstant) {
            if (isShortCircuitCondition(arg)) {
                shortCircuit()
            } else {
                //skip
                assert(isSkipConstant(arg))
            }
            if (arg.isTrue) {
                trueCount++  //for xors
            }
        } else if (arg.isLit) {
            addLitChild(arg.asLit)
        } else if (arg.isComplex) {
            addComplexChild(arg)
        } else {
            throw IllegalStateException()
        }

    }

    private fun addComplexChild(e: Exp) {
        assert(e.isComplex) { e }
        if (isCompatible(op1, e) && flatten) {
            //flatten
            addExpIt(e.argIt)
        } else {
            complex.put(e.expId, e)
            all.put(e.expId, e)
        }
    }

    private fun addLitChild(e: Exp) {
        assert(e.isLit)
        val newValue = e.asLit
        val varId = newValue.varId
        val oldValue = simple.put(varId, newValue)
        when (oldValue) {
            null -> {
                all.put(newValue.expId, newValue)
            }
            newValue -> {
                //println("  Dup assignment: $newValue")
            }
            else -> {
                assert(newValue.hasFlip && oldValue == newValue.flip)
                shortCircuit()
            }
        }

    }


    private fun shortCircuit() {
        this.shortCircuit = true;
    }

    fun checkNotShortCircuit() {
        check(!isShortCircuit)
    }

    val firstEntry: IndexedEntry<Exp>
        get() {
            return if (!simple.isEmpty) simple.first() else complex.first()
        }

    fun lastEntry(): IndexedEntry<Exp> {
        checkNotShortCircuit()
        return if (!complex.isEmpty) complex.last() else simple.last()
    }

    val first: Exp get() = firstEntry.value()

    val last: Exp get() = lastEntry().value()

    val isSingleton: Boolean get() = (argCount == 1)

    val isShortCircuit: Boolean get() = this.shortCircuit

    val isEmpty: Boolean get() = size == 0

    val isUnary: Boolean get() = size == 1

    val isBinary: Boolean get() = size == 2

    val isNary: Boolean get() = size > 2

    private fun isShortCircuitCondition(arg: Exp): Boolean {
        assert(arg.isConstant)
        when {
            op1.isAnd -> return arg.isFalse
            op1.isOr -> return arg.isTrue
            op1.isXor -> return arg.isTrue && trueCount > 0
            else -> throw IllegalStateException()
        }
    }

    fun isSkipConstant(arg: Exp): Boolean {
        assert(arg.isConstant)

        return when {
            op1.isAnd -> arg.isTrue
            op1.isOr -> arg.isFalse
            op1.isXor -> arg.isFalse
            else -> throw IllegalStateException()
        }

    }

//    public boolean isAnd() {
//        return op1 == Op.And;
//    }
//
//    public boolean isOr() {
//        return op1 == Op.Or;
//    }

    fun isXor(): Boolean {
        return op1 === Op1.Xor
    }

    val isBinaryOr: Boolean get() = isBinary && op1.isOr

    val isNaryOr: Boolean get() = isNary && op1.isOr

    val isBinaryAnd: Boolean get() = isBinary && op1.isAnd

    val isNaryAnd: Boolean get() = isNary && op1.isAnd


    @Throws(TrueOrArg::class)
    fun collectOrArgs(args: Iterable<Exp>) {
        for (arg in args) {
            if (arg.isConstantTrue) {
                throw TrueOrArg()
            } else if (arg.isConstantFalse) {
                //skip
            } else if (arg.isOr) {
                collectOrArgs(arg.argIt)
            } else {
                addExp(arg)
            }
        }
    }

    override fun toString(): String {
        val list = newArrayList(argIt)
        val a = Ser()
        Exp.serializeArgList(a, list)
        return op1.toString() + a.toString()
    }


    val argCount: Int get() = size

    val complexCount: Int get() = complex.size()
    val simpleCount: Int get() = simple.size()
    override val size: Int get() = all.size()

    @Deprecated(message = "Use mk()")
    fun mk(space: Space): Exp {
        assert(space == sp)
        return mk();
    }

    override fun mk(): Exp {
        val retVal = when (op1) {
            Op1.And -> mkAnd()
            Op1.Or -> mkOr()
            Op1.Xor -> mkXor()
        }

        return retVal;
    }


    val isAllLits: Boolean
        get() {
            return complex.isEmpty && !simple.isEmpty
        }
    val isAllComplex: Boolean
        get() {
            return !complex.isEmpty && simple.isEmpty
        }


    private fun mkXor(): Exp {
        return when {
            isShortCircuit -> sp.mkFalse()
            isEmpty -> sp.mkFalse()
            trueCount == 0 -> if (isSingleton) {
                first
            } else {
                assert(isAllLits)
                sp.mkPosComplex(this)
            }
            trueCount == 1 -> if (isSingleton) {
                first.flip
            } else {
                val bb = ArgBuilder(sp, Op.Cube)
                for (lit in litIt) {
                    bb.addExp(lit.flip)
                }
                bb.mkAnd()
            }
            else -> throw IllegalStateException()
        }

    }

    private fun mkAnd(): Exp {
        assert(op1.isAnd)

        return when {
            isShortCircuit -> sp.mkFalse()
            isEmpty -> sp.mkTrue()
            isSingleton -> first
            else -> {
                if (isAllLits) {
                    op = Op.Cube
                } else if (isAllComplex) {
                    if (op != Op.DAnd && op != Op.Fcc) {
                        op = Op.Formula
                    }
                } else {
                    //mixed
                    Op.And
                }
                sp.mkPosComplex(this)
            }
        }
    }

    private fun mkOr(): Exp {
        assert(op1.isOr)
        return when {
            isShortCircuit -> sp.mkTrue()
            isEmpty -> sp.mkFalse()
            isSingleton -> first
            else -> {
//                assert(argCount > 1);
                if (argCount <= 1) {
                    println(this.first)
                    println("first = ${first}")
                    println("isSingleton = ${isSingleton}")
                    println("simpleCount = ${simpleCount}")
                    println("complexCount = ${complexCount}")
                    println("all.size = ${all.size()}")
                    println("argCount = ${argCount}")
                    println("size = ${size}")
                }
                this.op = if (structure.isDnnf || op.isDOr) Op.DOr else Op.Or
                return sp.mkPosComplex(this)
            }
        }

    }

    override fun createExpArray(): Array<Exp> {
        return super.createExpArray();
    }


}






