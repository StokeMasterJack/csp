package com.tms.csp.ast

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterators
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.fm.dnnf.products.LitCube
import com.tms.csp.ssutil.Strings.lpad
import com.tms.csp.util.Bit
import com.tms.csp.util.varSets.VarSet
import com.tms.csp.varCodes.VarCode
import java.math.BigInteger

class Lit(override val vr: Var, override val isPos: Boolean, expId: Int) : Exp(vr.space, expId), ConditionOn {

    private val _cube: Cube
    private val _cubes: ImmutableSet<Cube>

    private var value: Int? = null  //for counting graph

    private var litAndFalse: LitAndFalse? = null

    override var isNew: Boolean = false
        private set

    override val space: Space get() = vr.space


    //    public Var getVr() {
    //        return vr;
    //    }

    override val code: String
        get() = vr.varCode

    override val varId: Int
        get() = vr.vrId

    override val pos: Exp
        get() = if (isPos)
            this
        else
            flip

    override val neg: Exp
        get() = if (!isPos)
            this
        else
            flip

    override val isLit: Boolean get() = true

    override val isPosLit: Boolean get() = isLit && isPos


    override val op: Op get() = Op.Lit


    val flipLit: Lit
        get() = flip.asLit

    override val isSat: Boolean
        get() = true

    override val satCount: BigInteger
        get() = BigInteger.ONE

    override val cubeCount: Int
        get() = 1

    override val isLeaf: Boolean
        get() = true

    override val varCode: String
        @Throws(UnsupportedOperationException::class)
        get() = vr.varCode

    override val varCode2: VarCode
        @Throws(UnsupportedOperationException::class)
        get() = vr.varCode2

    val lit: Int
        get() = if (sign()) vr.vrId else -vr.vrId


    /*
      open val cubesSmooth: Set<Cube>
        get() = computeCubesSmooth()
     */

    override val cubesSmooth: Set<Cube>
        get() = _cubes

    val pref: Prefix
        get() = vr.prefix2

    override val vars: VarSet
        get() = vr.mkSingletonVarSet()

    override val isDnnf: Boolean
        get() = true

    override val isSmooth: Boolean
        get() = true

    override val smooth: Exp
        get() = this

    val hashKey: String
        get() {
            val sb = StringBuilder()
            sb.append('L')
            sb.append(vr.hashKey)
            if (isPos) {
                sb.append('T')
            } else {
                sb.append('F')
            }
            return sb.toString()
        }

    override val argCount: Int
        get() = 0

    fun hasLitAndFalse(): Boolean {
        return litAndFalse != null
    }

    fun mkLitAndFalse(): LitAndFalse {
        if (litAndFalse == null) {
            val sp = space
            val nodeCount = sp.nodeCount
            litAndFalse = LitAndFalse(this, nodeCount)
            space.addNode(litAndFalse)
        }
        return litAndFalse!!
    }


    init {
        this._cube = LitCube(this)
        this._cubes = ImmutableSet.of(_cube)
        isNew = true
    }

    override fun notNew() {
        isNew = false
    }

    override fun anyVarOverlap(exp: Exp): Boolean {
        if (exp == null || exp.isConstant) return false
        if (exp.isLit) {
            return vr === exp.vr
        }
        if (exp.isComplex) {
            val vars = exp.vars
            return vars.containsVar(vr)
        }

        throw IllegalStateException()
    }

    override val flip: Exp get() = vr.mkLit(!isPos)


    override fun condition(ctx: Cube): Exp {
        if (!ctx.containsVar(vr)) return this

        if (ctx.isTrue(vr) && isPos) return mkTrue()
        if (ctx.isTrue(vr) && isNeg) return mkFalse()
        if (ctx.isFalse(vr) && isPos) return mkFalse()
        if (ctx.isFalse(vr) && isNeg) return mkTrue()

        throw IllegalStateException()
    }

    override fun computeSatCount(): BigInteger {
        return BigInteger.ONE
    }

    override val firstVar: Var get() = vr

    override fun containsVarId(varId: Int): Boolean = vr.vrId == varId

    override fun serialize(a: Ser) {
        if (!isPos) {
            a.bang()
        }
        a.append(varCode)
    }

    override fun condition(lit: Lit): Exp {

        if (vr !== lit.vr) {
            return this
        }
        return if (isPos == lit.isPos) {
            mkConstantTrue()
        } else {
            mkConstantFalse()
        }

    }


    override fun flatten(): Exp {
        return this
    }

    override fun serializeTinyDnnf(a: Ser) {
        a.append(L_SP)
        a.append(lit)
    }


    override fun toXml(a: Ser, depth: Int) {
        a.indent(depth)
        if (isNeg) {
            a.bang()
        }
        val varCode = varCode
        a.append(varCode)
        a.newLine()
    }


    override fun project(outVars: VarSet): Exp {
        return if (outVars.containsVar(vr)) {
            this
        } else {
            mkConstantTrue()
        }
    }

    override fun getValue(vr: Var): Bit {
        return if (vr === this.vr) {
            if (isPos)
                Bit.TRUE
            else
                Bit.FALSE
        } else {
            Bit.OPEN
        }
    }

    override fun toDnnf(): Exp {
        return this
    }

    override val hasFlip: Boolean get() {
        return if (isPos) {
            vr.hasNegLit()
        } else {
            vr.hasPosLit()
        }
    }


    override fun computeValue(cube: Cube): Int {
        val _flip = flip.asLit
        return if (cube.containsLit(_flip)) {
            0
        } else {
            1
        }
    }

    override fun computeValue(): Int {
        assert(_space.pics != null)
        if (this.value == null) {
            val _flip = flip.asLit
            if (_space.pics.containsLit(_flip)) {
                this.value = 0
            } else {
                this.value = 1
            }
        }
        return this.value!!
    }

    override fun checkDnnf(): Boolean {
        return true
    }


    override fun computeSat(lit: Lit): Boolean {
        return if (vr !== lit.vr) true else isPos == lit.isPos
    }

    override fun computeSat(cube: Cube): Boolean {
        val _flip = flip.asLit
        return if (cube.containsLit(_flip)) {
            false
        } else {
            true
        }
    }

    override fun computeSat(trueVars: VarSet): Boolean {
        return if (isNeg && trueVars.containsVar(vr)) {
            false
        } else {
            true
        }
    }

    override fun computeSatCount1(lit: Lit): Long {
        val sat = computeSat(lit)
        return if (sat) {
            1
        } else {
            0
        }
    }


    fun serializeTrueVars(a: Ser) {
        if (isPos) {
            serialize(a)
        }
    }

    fun serializeTrueVars(): String {
        val a = Ser()
        serializeTrueVars(a)
        return a.toString()
    }

    override fun litIterator(): Iterator<Lit> {
        return Iterators.singletonIterator(this)
    }

    override fun varIterator(): Iterator<Var> {
        return Iterators.singletonIterator(vr)
    }

    override val asCube: Cube get() = _cube


    fun argIter(): Iterator<Exp> {
        return Iterators.singletonIterator(asExp)
    }


    override fun copyToOtherSpace(destSpace: Space): Exp {
        if (space === destSpace) {
            return this
        }
        val varCode = varCode
        val destVar = destSpace.getVar(varCode)
        assert(destVar.space === destSpace)
        return destVar.mkLit(sign())
    }

    fun sameVarCode(that: Lit): Boolean {
        return varCode == that.varCode
    }

    override fun litMatch(): Exp {
        return this
    }

    override fun smooth(dontCares: VarSet): Exp {

        val space = space

        if (dontCares.isEmpty()) {
            return this
        }
        val bAnd = ArgBuilder(space, Op.DAnd)


        //add special dontCare DOrs
        for (dontCare in dontCares) {
            val dcOr = dontCare.mkDcOr()
            bAnd.addExp(dcOr)
        }

        if (bAnd.isEmpty) {
            throw IllegalStateException()
        }

        //add current args
        bAnd.addExp(this)

        val exp = bAnd.mk(space)
        return exp.asDAnd
    }

    fun toString(cols: Int): String {
        return lpad(toString(), ' ', cols)
    }

    override fun conditionThat(that: Exp): Exp {
        return that.condition(this)
    }

    override val satCountPL: Long get() = 1

    companion object {

        private val L_SP = "L "
    }


}
