package com.smartsoft.csp.ast

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.Iterators
import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.ast.PLConstants.LPAREN
import com.smartsoft.csp.ast.PLConstants.RPAREN
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.util.Bit
import com.smartsoft.csp.util.DynComplex
import com.smartsoft.csp.varSet.VarSet
import java.util.*

abstract class PosComplexMultiVar(space: Space, expId: Int, internal val _args: Array<Exp>) : PosComplex(space, expId) {

    protected var _neg: Exp? = null

    //cache
    protected var _vars: VarSet? = null

    //used by unique tabe
    private var _new = true

    override fun notNew() {
        _new = false
    }

    override val isNew: Boolean get() = _new

    override val isPosArgsExp: Boolean
        get() = true

    override val litArgCount: Int
        get() {
            var c = 0
            for (arg in args) {
                if (arg.isLit) {
                    c++
                }
            }
            return c
        }


    override val argCount: Int
        get() = _args.size

    //    public Iterator<Exp> iterator() {
    //        return Iterators.forArray(args);
    //    }

    override val firstVar: Var get() = arg1.firstVar


    val isAllClauses: Boolean
        get() {
            for (arg in args) {
                if (!arg.isClause) {
                    return false
                }
            }
            return true
        }


    val isUnitDEAD: Boolean
        get() = false

    val unitDEAD: Exp?
        get() = null

    override val isPos: Boolean get() = true

    override val vars: VarSet
        get() {
            if (this._vars == null) {
                _vars = computeVars()
            }
            return this._vars!!
        }

    override val code: String
        get() = simpleName.toLowerCase()

    override val isSat: Boolean get() = lazyIsSat.value

    private val lazyIsSat: Lazy<Boolean> = lazy { computeIsSat() }

    val simpleArgs: Iterable<Exp>
        get() {
            val aa = ArrayList<Exp>()
            for (a in argIt) {
                if (a.isSimple) {
                    aa.add(a)
                }
            }
            return aa
        }


    val simple: DynCube
        get() {
            val lits = Exp.extractSimple(argIt)
            return DynCube(_space, lits)
        }


    val complex: DynComplex
        get() {
            val complexArgs = complexArgs
            return DynComplex(_space, complexArgs)
        }

    val complexArgs: Iterable<Exp>
        get() = Exp.extractComplex(argIt)

    override val pos: Exp
        get() = this

    val hashKey: String
        get() {
            assert(isDAnd || isDOr)
            return buildHashKey()
        }


    init {
        assert(Exp.assertFixedArgs(_args))
    }


    //    protected PosComplex(Space space, int expId, ArgBuilder args) {
    //        this(space, expId, args.toArray());
    //    }


    /**
     * Creates new Complex expr of same type and sign
     */
    fun newComplex(args: Iterable<Exp>): Exp {
        val op = posOp
        val space = space
        return space.mkPosComplex(op, args)
    }


    override fun getArg(index: Int): Exp {
        return _args[index]
    }

    override fun argsRest(): List<Exp> {
        return subList(1, size())
    }

    override fun serialize(a: Ser) {
        val token = getPosComplexOpToken(a)
        a.append(token)
        a.append(LPAREN)
        //        Exp.serializeArgsUnsorted(a, args);
        Exp.serializeArgsSorted(a, _args)
        a.append(RPAREN)
    }

    override fun containsVarId(varId: Int): Boolean {
        return vars.containsVarId(varId)
    }

    override fun anyVarOverlap(exp: Exp): Boolean {
        if (exp.isConstant) return false
        if (exp.isLit) {
            return vars.containsVar(exp.vr)
        }
        if (exp.isComplex) {
            val otherVars = exp.vars
            return otherVars.anyVarOverlap(vars)
        }

        throw IllegalStateException()
    }

//    open fun argIter(): Iterator<Exp> {
//        return Iterators.forArray(*_args)
//    }

    override val argIter: Iterator<Exp> get() = Iterators.forArray(*_args)


    fun gcNeg() {
        if (_neg != null) {
            _neg = null
        }
    }

    //    public boolean isFrontier() {
    //        if (assignment == null) return false;
    //        if (conflict != null) return false;
    //        if (eval == null) return false;
    //        if (eval.value != assignment.value) return false;
    //        return true;
    //    }
    //
    //    public boolean isLive() {
    //        if (isFrontier()) {
    //            return true;
    //        } else {
    //            for (Complex parent : parents) {
    //                if (parent.isLive()) {
    //                    return true;
    //                }
    //            }
    //            return false;
    //        }
    //
    //    }


    //
    //    public Exp simplifyAfterVVPick(Exp vv) {
    //        if (this.containsAll(vv)) {
    //            boolean ch = false;
    //            ArrayList<Exp> a = new ArrayList<Exp>();
    //            for (Exp e : args) {
    //                Exp s = e.simplifyAfterVVPick(vv);
    //                if (s != e) {
    //                    ch = true;
    //                }
    //                a.add(s);
    //            }
    //            if (ch) {
    //                PosOp op1 = getPosOp();
    //                if (op1.isAnd()) return space.mkAnd(a);
    //                if (op1.isOr()) return space.mkOr(a);
    //                if (op1.isXor()) return space.mkXor(a);
    //                throw new IllegalStateException();
    //            } else {
    //                return this;
    //            }
    //
    //        } else {
    //            return this;
    //        }
    //    }


    protected open fun computeVars(): VarSet {
        val b = _space.newMutableVarSet()
        for (arg in _args) {
            if (arg.isLit) {
                val vr = arg.vr
                b.addVar(vr)
            } else if (arg.isComplex) {
                val vars1 = arg.vars
                b.addVarSet(vars1)
            }
        }
        return b.build()
    }

    /**
     * Returns true if the given iterator and this.iterator
     * return the same elements, formula the same order.
     *
     * @return true if values and this.iterator return the same elements,
     * formula the same order.
     */
    fun sameArgs(values: Iterator<Exp>): Boolean {
        for (f in _args) {
            if (!(values.hasNext() && f === values.next()))
                return false
        }
        return !values.hasNext()
    }

    fun sameSize(thatSize: Int): Boolean {
        return size() == thatSize
    }


    fun sameOp(op: Op): Boolean {
        return op === op
    }

    override fun containsLit(lit: Exp): Boolean? {
        if (containsArg(lit)) return true
        return if (containsArg(lit.flip)) false else null
    }

    override fun containsArg(arg: Exp): Boolean {
        for (a in args) {
            if (a === arg) return true
        }
        return false
    }

    enum class LL {
        TT, TF, FT, FF;

        fun tt(): Boolean {
            return this == TT
        }

        fun ff(): Boolean {
            return this == FF
        }

        fun tf(): Boolean {
            return this == TF
        }


        fun ft(): Boolean {
            return this == FT
        }

        companion object {

            fun create(L1: Boolean, L2: Boolean): LL {
                if (L1 && L2) return TT
                if (!L1 && !L2) return FF
                return if (L1) TF else FT
            }
        }
    }

    fun containsVVArgs(vv: Exp): LL? {
        assert(vv.isVv)
        val lit1 = vv.arg1
        val lit2 = vv.arg2
        return containsVVArgs(lit1, lit2)
    }

    fun containsLocalLitWithVar(var1: Int, var2: Int): Boolean {
        return containsLocalLitWithVar(var1) && containsLocalLitWithVar(var2)
    }

    fun containsLocalLits(vv: Exp): Boolean {

        val vars = vv.vars
        val it = vars.intIterator()

        val var1 = it.next()
        if (!containsLocalLitWithVar(var1)) return false

        val var2 = it.next()
        return if (!containsLocalLitWithVar(var2)) false else true

    }

    fun containsLocalLitWithVar(varId: Int): Boolean {
        for (a in _args) {
            if (a.isLit && a.varId == varId) {
                return true
            }
        }
        return false
    }

    /**
     * NN
     * YY
     * FF
     *
     * @return
     */
    fun containsVVArgs(lit1: Exp, lit2: Exp): LL? {
        val L1 = containsLit(lit1) ?: return null
        val L2 = containsLit(lit2) ?: return null
        return LL.create(L1, L2)
    }

    open fun computeIsSat(): Boolean = th

    fun serializeArgsTinyDnnf(a: Ser) {
        val it = argIter
        while (it.hasNext()) {
            val arg = it.next()
            val nodeId = arg.expId
            a.append(nodeId)
            if (it.hasNext()) {
                a.argSep()
            }
        }
    }


    fun subList(fromIndex: Int, toIndex: Int): List<Exp> {
        val a = ArrayList<Exp>(toIndex - fromIndex + 1)
        for (i in fromIndex until toIndex) {
            a.add(_args[i])
        }
        return a
    }

    override val flip: Exp get() = neg

    override val neg: Exp
        get() {
            if (this._neg == null) {
                this._neg = Not(this, _space.nodeCount)
                _space.addNode(this._neg)
            } else {
                this._neg!!.notNew()
            }
            return this._neg!!
        }

    override fun getValue(vr: Var): Bit {

        if (!containsVar(vr)) {
            return Bit.OPEN  //d
        }

        val t = vr.mkPosLit()
        val f = vr.mkNegLit()
        val tSat = computeSat(t)
        val fSat = computeSat(f)
        return if (tSat && !fSat) {
            Bit.TRUE
        } else if (!tSat && fSat) {
            Bit.FALSE
        } else if (!tSat && !fSat) {
            throw IllegalStateException()
        } else {
            Bit.OPEN
        }
    }

    override val hasFlip: Boolean get() = _neg != null

    val pairFirstLitArg: Lit?
        get() = when {
            _args[0] is Lit -> _args[0] as Lit
            _args[1] is Lit -> _args[1] as Lit
            else -> throw IllegalStateException()
        }

    val pairFirstCubeArg: Cube
        get() = when {
            _args[0] is Cube -> _args[0] as Cube
            _args[1] is Cube -> _args[1] as Cube
            else -> throw IllegalStateException()
        }

    val pairFirstNonLitArg: Exp
        get() = when {
            _args[0] !is Lit -> _args[0]
            _args[1] !is Lit -> _args[1]
            else -> throw IllegalStateException()
        }

    val pairIsLitLit: Boolean get() = _args[0] is Lit && _args[1] is Lit

    val pairIsLitCube: Boolean get() = _args[0] is Lit && _args[1] is Cube
    val pairIsCubeLit: Boolean get() = _args[0] is Cube && _args[1] is Lit

    val pairIsLitNotClause: Boolean get() = _args[0] is Lit && _args[1].isNotClause
    val pairIsNotClauseLit: Boolean get() = _args[0].isNotClause && _args[1] is Lit

    open fun flipArgs(): ArgBuilder {
        throw UnsupportedOperationException()
    }

    open fun createHardFlip(): Exp {
        throw UnsupportedOperationException()
    }

    fun sameArgs(that: PosComplexMultiVar): Boolean {
        return Arrays.equals(_args, that._args)
    }

    fun buildHashKey(): String {
        val sb = StringBuilder()
        if (isDAnd) {
            sb.append('A')
        } else if (isDOr) {
            sb.append('O')
        } else {
            throw IllegalStateException()
        }
        for (arg in _args) {
            val sId = Integer.toString(arg.expId, Character.MAX_RADIX)
            sb.append(sId)
        }
        return sb.toString()
    }

    override fun anyVarOverlap(vs: VarSet): Boolean {
        return vars.anyVarOverlap(vs)
    }

    override fun isVarDisjoint(vs: VarSet): Boolean {
        return vars.isVarDisjoint(vs)
    }

    companion object {

        val COMPARATOR_BY_ARITY: Comparator<PosComplexMultiVar> = Comparator { e1, e2 ->
            checkNotNull(e1)
            checkNotNull(e2)
            val argCount1 = e1.argCount
            val argCount2 = e2.argCount
            argCount1.compareTo(argCount2)
        }
    }
}
