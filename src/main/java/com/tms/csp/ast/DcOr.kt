package com.tms.csp.ast

import com.google.common.collect.ImmutableSet
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.varSets.VarSet

/**
 * This is for the smooth transformation
 */
class DcOr(private val _vr: Var, expId: Int) : PosComplexSingleVar(_vr.space, expId) {

    private var _isNew = true;

    private var value: Int? = null  //for counting graph

//    private val cubes2 = ImmutableSet.of(
//            _vr.nLit().asCube(),
//            _vr.pLit().asCube()
//    )

    private val cubes by lazy {
        ImmutableSet.of(
                _vr.nLit().asCube(),
                _vr.pLit().asCube()
        )
    }

    override fun notNew() {
        _isNew = false;
    }

    override fun isNew(): Boolean = _isNew

    private val args = listOf<Exp>(_vr.nLit(), _vr.pLit())


    override fun isPos(): Boolean = true

    override fun getPos(): Exp = this

    override fun getArgCount(): Int = 2

    override fun hasFlip(): Boolean = false

    override fun getFirstVar(): Var = _vr

    override fun isDnnf(): Boolean = true

    override fun checkDnnf(): Boolean = true

    override fun getPosOp() = PLConstants.PosOp.OR

    override fun isComplex(): Boolean = true

    override fun isOr(): Boolean = true

    override fun computeCubesSmooth(): Set<Cube> = cubes

    override fun computeSatCount(): Long {
        return 2
    }


    override val vars: VarSet get() = _vr.mkSingletonVarSet();

//    override val cubesRough: Set<Cube> get() = cubes
//
//    override val cubesSmooth: Set<Cube> get() = cubes

    override fun getCubesRough(): Set<Cube> = cubes

    override fun getCubesSmooth(): Set<Cube> = cubes

    override fun getArg(i: Int): Exp = when (i) {
        0 -> _vr.nLit()
        1 -> _vr.pLit()
        else -> throw IllegalStateException()
    }

    override fun condition(c: Cube): Exp = if (c.containsVar(_vr)) mkTrue() else this

    override fun condition(lit: Lit): Exp = if (lit.varId == _vr.varId) mkTrue() else this


    override fun toString(): String {
        val a = Ser()
        serialize(a)
        return a.toString()
    }

    override fun serialize(aa: Ser) {
        val token = getPosComplexOpToken(aa)
        aa.append(token)
        aa.append(PLConstants.LPAREN)
        arg1.serialize(aa)
        aa.argSep()
        arg2.serialize(aa)
        aa.append(PLConstants.RPAREN)
    }

    override fun isDcOr(): Boolean = true

    override fun isSat(): Boolean = true

    override fun copyToOtherSpace(destSpace: Space): Exp {
        if (_vr.space === destSpace) return this
        val destVar = destSpace.getVar(varCode)
        assert(destVar.space === destSpace)
        return destVar.mkDcOr()
    }

    override fun getVarCode(): String = _vr.varCode

    override fun getVr(): Var = _vr

    override fun argIt(): Iterable<Exp> {
        return Iterable { argIter() }
    }

    override fun argIter(): Iterator<Exp> {
        return args.iterator()
    }

    override fun isDOr(): Boolean = true

    override fun serializeTinyDnnf(a: Ser) {
        val arg1NodeId = arg1.getExpId()
        val arg2NodeId = arg2.getExpId()
        a.append('O')
        a.append(' ')
        a.append(arg1NodeId)
        a.append(' ')
        a.append(arg2NodeId)
    }


    override fun getArgs(): List<Exp> {
        return args
    }


    override fun computeSat(lit: Lit) = true

    override fun computeSat(trueVars: VarSet): Boolean = true

    override fun computeSat(cube: Cube): Boolean = true

    override fun project(outVars: VarSet): Exp {
        return if (outVars.containsVar(_vr)) {
            this
        } else {
            mkTrue()
        }
    }


    override fun computeValue(): Int {
        val sp = sp()
        assert(sp.pics != null)
        if (this.value == null) {
            var totalValue = 0
            for (child in args) {
                val childValue = child.computeValue()
                totalValue += childValue
            }
            this.value = totalValue
        }
        return this.value!!
    }

    override fun asDcOr(): DcOr {
        return this
    }


    override fun _setIsSmooth() {
        super._setIsSmooth()
    }

    override fun isSmooth(): Boolean = true

    override fun getSmooth(): Exp {
        return this;
    }
}

