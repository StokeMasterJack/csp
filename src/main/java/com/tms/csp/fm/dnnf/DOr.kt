package com.tms.csp.fm.dnnf


import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.ImmutableSet
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.*
import com.tms.csp.fm.dnnf.models.Solution
import com.tms.csp.fm.dnnf.models.Solutions
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.varSets.VarSet
import java.math.BigInteger

class DOr(space: Space, id: Int, args: Array<Exp>) : Or(space, id, args) {

    private var _smooth: Exp? = null

    private var _isSmooth: Boolean? = null
    private var _value: Int? = null  //for counting graph


    override val isOr: Boolean get() = true


    override val isDOr: Boolean get() = true

    override val op: Op get() = Op.DOr

    override val cubeCount: Int get() = computeCubeCount()

    override val isDnnf: Boolean
        get() = true

    init {
        for (arg in args) {
            assert(arg.isDnnf) { arg }
        }
    }

    override val smooth: Exp
        get() {
            if (_smooth == null) {
                if (isSmooth) {
                    _smooth = this
                } else {
                    _smooth = computeSmooth()
                }
            }
            return _smooth!!
        }

    private fun computeSmooth(): Exp {
        assert(_smooth == null)
        if (_isSmooth != null && _isSmooth!!) {
            _smooth = this
            return this
        }

        val b = ArgBuilder(space, op())

        val parentVars = vars

        for (arg in args) {

            val child = arg.smooth

            val childVars = child.vars
            val dontCares = parentVars.minus(childVars)

            val smoothChild: Exp = if (dontCares.isEmpty()) {
                child
            } else {
                child.smooth(dontCares)
            }

            b.addExp(smoothChild)
        }


        val smooth = b.mk()
        smooth._setIsSmooth()

        return smooth

    }

    override fun project(outVars: VarSet): Exp {
        checkNotNull(outVars)

        val vars = this.vars
        if (outVars.containsAllVars(vars)) {
            return this
        }

        val b = ArgBuilder(_space, Op.DOr)


        for (i in 0 until _args.size) {
            val arg = _args[i]
            val s = arg.project(outVars)

            if (s.isTrue) {
                return mkTrue()
            } else if (s.isFalse) {
                //skip
            } else {
                assert(s.isOpen)
                b.addExp(s)

            }

        }

        return b.mk()
    }


    override fun serializeTinyDnnf(a: Ser) {
        a.append('O')
        a.append(' ')
        serializeArgsTinyDnnf(a)
    }

    override fun toDnnf(): Exp {
        return this
    }

    override fun asDOr(): DOr {
        return this
    }

    override fun checkDnnf(): Boolean {
        checkDeterministic()
        for (arg in args) {
            arg.checkDnnf()
        }
        return true
    }

    fun checkDeterministic(): Boolean {
        return true
    }

    override fun flatten(): Exp {
        if (isFlat) {
            return this
        }


        val b = ArgBuilder(_space, Op.DOr)
        for (arg in args) {
            val argf = arg.flatten()
            if (argf.isOr) {
                for (aa in argf.args) {
                    val aaf = aa.flatten()
                    b.addExp(aaf)
                }
            } else {
                b.addExp(arg)
            }
        }


        //        for (Exp arg : retVal) {
        //            assert !arg.isNested(this);
        //        }

        return b.mk()
    }


    override fun computeCubesRough(): Set<Cube> {
        val parentCareVars = vars

        val b = ImmutableSet.builder<Cube>()
        for (arg in args) {
            val argVars = arg.vars
            val dcVars = parentCareVars.minus(argVars)

            if (arg.isLit) {
                val solution = Solution(_space, arg.asLit.asCube, dcVars)
                b.addAll(solution)
            } else {
                val argCubes = arg.cubesRough
                val cubes = Solutions(_space, argCubes, dcVars)
                b.addAll(cubes)
            }

        }
        return b.build()
    }


    override fun computeCubesSmooth(): Set<Cube> {
        val b = ImmutableSet.builder<Cube>()
        for (arg in args) {
            if (arg.isLit) {
                b.add(arg.asLit.asCube)
            } else {
                val argCubes = arg.cubesSmooth
                b.addAll(argCubes)
            }
        }
        return b.build()
    }

    override fun computeSatCount(): BigInteger {
        var satCount = BigInteger.ZERO
        for (arg in args) {
            //            satCount += arg.getSatCount();
            satCount = satCount.add(arg.satCount)
        }
        return satCount
    }


    override fun computeCubesNoVarSet(): Set<Cube> {
        val parentCareVars = vars

        val b = ImmutableSet.builder<Cube>()
        for (arg in args) {
            val argVars = arg.vars
            val dcVars = parentCareVars.minus(argVars)

            if (arg.isLit) {
                val solution = Solution(_space, arg.asLit.asCube, dcVars)
                b.addAll(solution)
            } else {
                val argCubes = arg.cubesSmooth
                val cubes = Solutions(_space, argCubes, dcVars)
                b.addAll(cubes)
            }

        }
        //        return new DisjointCubeSet(b);
        return b.build()
    }


    fun computeCubeCount(): Int {
        val parentCareVars = vars

        val b = ImmutableSet.builder<Cube>()
        for (arg in args) {
            val argVars = arg.vars
            val dcVars = parentCareVars.minus(argVars)

            if (arg.isLit) {
                val solution = Solution(_space, arg.asLit.asCube, dcVars)
                b.addAll(solution)
            } else {
                val argCubes = arg.cubesSmooth
                val cubes = Solutions(_space, argCubes, dcVars)
                b.addAll(cubes)
            }

        }
        //        return new DisjointCubeSet(b);
        return b.build().size
    }


    /**
     * replace unmatched lits with: lit or (!lit and false)
     */
    override fun litMatch(): Exp {
        val space = space

        val newArgs = ImmutableSet.builder<Exp>()

        for (arg in args) {
            val newArg = arg.litMatch()
            newArgs.add(newArg)
        }

        val retVal = space.mkDOr(newArgs.build())


        return retVal

    }

    override fun _setIsSmooth() {
        //        assert smooth == null;
        //        assert isSmooth == null;
        _smooth = this
        _isSmooth = true
    }

    override val isSmooth: Boolean
        get() {
            if (_isSmooth == null) {
                _isSmooth = computeIsSmooth()
            }
            return _isSmooth!!
        }

    private fun computeIsSmooth(): Boolean {
        val parentVars = vars
        for (child in args) {
            val childVars = child.vars
            if (parentVars != childVars) {
                return false
            }
            if (!child.isSmooth) {
                return false
            }
        }
        return true
    }


    override fun computeValue(cube: Cube): Int {
        var totalValue = 0
        for (child in args) {
            val childValue = child.computeValue(cube)
            totalValue += childValue
        }
        return totalValue
    }

    override fun computeValue(): Int {
        assert(_space.pics != null)
        if (this._value == null) {
            var totalValue = 0
            for (child in args) {
                val childValue = child.computeValue()
                totalValue += childValue
            }
            this._value = totalValue
        }
        return this._value!!
    }

    override fun computeSat(lit: Lit): Boolean {
        for (arg in args) {
            val sat = arg.computeSat(lit)
            if (sat) return true
        }
        return false
    }

    override fun computeSat(cube: Cube): Boolean {
        for (arg in args) {
            val sat = arg.computeSat(cube)
            if (sat) return true
        }
        return false
    }

    override fun computeSat(trueVars: VarSet): Boolean {
        for (arg in args) {
            val sat = arg.computeSat(trueVars)
            if (sat) return true
        }
        return false
    }

    override fun computeSatCount1(lit: Lit): Long {
        var satCount: Long = 0

        val parentCareVars = vars

        for (child in args) {

            val satCountWithOutDcs = child.computeSatCount1(lit)

            val childCareVars = child.vars
            val dontCares = parentCareVars.minus(childCareVars)

            val dcCount = dontCares.size

            val dcSatCount = Exp.computeDcPermCount(dcCount).toLong()

            if (dcSatCount < 0) {
                throw IllegalStateException()
            }

            val satCountWithDcs = satCountWithOutDcs * dcSatCount

            satCount += satCountWithDcs


        }

        return satCount
    }

    override fun computeIsSat(): Boolean {
        for (arg in args) {
            val sat = arg.isSat
            if (sat) {
                return true
            }
        }
        return false
    }


    override fun copyToOtherSpace(destSpace: Space): Exp {
        return if (space === destSpace) {
            this
        } else Exp.copyArgsExpToOtherSpace(destSpace, op, argIt)
    }

    companion object {

        private val O_SP = "O "
    }

    //    @Override
    //    public Set<Lit> getLits() {
    //        ImmutableSet.Builder<Lit> b = ImmutableSet.builder();
    //        for (Exp arg : args) {
    //            b.addAll(arg.getLits());
    //        }
    //        return b.build();
    //    }

    //    @Override
    //    public boolean isLitMatched() {
    //        for (Exp arg : args) {
    //            if (!arg.isLitMatched()) {
    //                return false;
    //            }
    //        }
    //
    //        return true;
    //    }

}

