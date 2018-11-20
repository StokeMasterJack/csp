package com.smartsoft.csp.ast

import com.google.common.collect.ImmutableSet
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.util.varSets.VarSet
import java.math.BigInteger

/**
 * This is for the smooth transformation
 */
class DcOr(private val _vr: Var, expId: Int) : PosComplexSingleVar(_vr.space, expId) {

    private var _isNew = true;

    private var value: Int? = null  //for counting graph

    private val _args: List<Exp> = listOf(_vr.nLit(), _vr.pLit())

//    private val cubes2 = ImmutableSet.of(
//            _vr.nLit().asCube,
//            _vr.pLit().asCube
//    )

    override val cubes by lazy {
        ImmutableSet.of(
                _vr.nLit().asCube,
                _vr.pLit().asCube
        )
    }

    override fun notNew() {
        _isNew = false;
    }

    override val isNew: Boolean get() = _isNew

//    private val args = listOf<Exp>(_vr.nLit(), _vr.pLit())


    override val isPos: Boolean get() = true


    override val vr: Var get() = _vr



    override val pos: Exp get() = this

    override val argCount: Int get() = 2

    override val hasFlip: Boolean get() = false

    override val firstVar: Var get() = _vr

    override val isDnnf: Boolean get() = true

    override fun checkDnnf(): Boolean = true

    override val posOp = PLConstants.PosOp.OR

    override val isComplex: Boolean get() = true


    override fun computeCubesSmooth(): Set<Cube> = cubes

    override fun computeSatCount(): BigInteger {
        return BigInteger.TWO
    }


    override val vars: VarSet get() = _vr.mkSingletonVarSet();

//    override val cubesRough: Set<Cube> get() = cubes
//
//    override val cubesSmooth: Set<Cube> get() = cubes

    override val cubesRough: Set<Cube> get() = cubes

    override val cubesSmooth: Set<Cube> get() = cubes

    override fun getArg(index: Int): Exp = _args[index]

    override fun condition(ctx: Cube): Exp {
        return if (ctx.containsVar(_vr)) {
            mkTrue()
        } else {
            this
        }
    }

    override fun condition(lit: Lit): Exp {
        return if (lit.varId == _vr.vrId) {
            mkTrue()
        } else {
            this
        }
    }


    override fun toString(): String {
        val a = Ser()
        serialize(a)
        return a.toString()
    }

    override fun serialize(a: Ser) {
        val token = getPosComplexOpToken(a)
        a.append(token)
        a.append(PLConstants.LPAREN)
        arg1.serialize(a)
        a.argSep()
        arg2.serialize(a)
        a.append(PLConstants.RPAREN)
    }

    override val isSat: Boolean get() = true

    override fun copyToOtherSpace(destSpace: Space): Exp {
        if (_vr.space === destSpace) return this
        val destVar = destSpace.getVar(varCode)
        assert(destVar.space === destSpace)
        return destVar.mkDcOr()
    }

    override val varCode: String get() = _vr.varCode

//    override fun getVr(): Var = _vr


    override val argIter: Iterator<Exp> get() = _args.iterator()

    override fun serializeTinyDnnf(a: Ser) {
        val arg1NodeId = arg1.expId
        val arg2NodeId = arg2.expId
        a.append('O')
        a.append(' ')
        a.append(arg1NodeId)
        a.append(' ')
        a.append(arg2NodeId)
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

    override val isSmooth: Boolean get() = true

    override val smooth: Exp get() = this
}

