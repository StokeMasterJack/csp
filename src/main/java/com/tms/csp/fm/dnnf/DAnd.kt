package com.tms.csp.fm.dnnf

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.*
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.varSets.VarSet
import java.math.BigInteger
import java.util.*

open class DAnd(space: Space, id: Int, args: Array<Exp>) : And(space, id, args) {

    private var _smooth: Exp? = null
    var _isSmooth: Boolean? = null
    private var _value: Int? = null  //for counting graph

    override val op: Op
        get() = Op.DAnd


    //    @Override
    //    public void print(int depth) {
    //        prindent(depth, "<and>");
    //        for (Exp arg : args) {
    //            arg.print(depth + 1);
    //        }
    //        prindent(depth, "</and>");
    //    }


    override val isDnnf: Boolean get() = true

    override val cubeCount: Int get() = cubes.size

    init {

        for (arg in args) {
            if (!arg.isDnnf) {
                throw IllegalStateException(arg.simpleName + " " + arg.toString())
            }
        }
    }

    override fun project(outVars: VarSet): Exp {
        checkNotNull(outVars)

        val vars = this.vars
        if (outVars.containsAllVars(vars)) {
            return this
        }

        val b = ArgBuilder(_space, Op.DAnd)

        for (i in 0 until _args.size) {
            val arg = _args[i]
            val s = arg.project(outVars)

            if (s.isFalse) {
                return mkFalse()
            } else if (s.isTrue) {
                //skip
            } else {
                assert(s.isOpen)
                b.addExp(s)
            }

        }


        return b.mk()

    }

    fun asDAnd(): DAnd {
        return this
    }

    override fun checkDnnf(): Boolean {
        val disjoint = checkDisjointConjuncts()
        if (!disjoint) return false
        for (arg in args) {
            arg.checkDnnf()
        }
        return true
    }


    @Throws(Exception::class)
    fun test1() {

    }

    override fun toDnnf(): Exp {
        return this
    }


    override fun checkChildCounts(cc: ChildCounts) {

        try {

            assert(cc.cube + cc.or + cc.and == cc.argCount)

            assert(cc.cube == 0 || cc.cube == 1)
            assert(cc.constantFalse == 0)
            assert(cc.constantTrue == 0)
            assert(cc.lit == 0)

            val o1 = cc.nested && cc.argCount == 2 && cc.cube == 1 && cc.and == 1
            val o2 = !cc.nested && cc.cube == 1 && cc.or >= 1 && cc.cube + cc.or == cc.argCount
            val o3 = !cc.nested && cc.cube == 0 && cc.or >= 1 && cc.or == cc.argCount && cc.argCount > 1


            assert(o1 || o2 || o3)
        } catch (e: Error) {
            System.err.println(toXml())
            throw e
        }

    }


    override fun copyToOtherSpace(destSpace: Space): Exp {
        return if (space === destSpace) {
            this
        } else Exp.copyArgsExpToOtherSpace(destSpace, op(), argIt)
    }

    override val isSmooth: Boolean
        get() {
            if (_isSmooth == null) {
                _isSmooth = computeIsSmooth()
            }
            return _isSmooth!!
        }

    private fun computeIsSmooth(): Boolean {
        if (_smooth != null) {
            return _smooth === this
        }
        for (arg in args) {
            if (!arg.isSmooth) {
                return false
            }
        }
        return true
    }

    /*
    override fun computeCubesRough(): Set<Cube> {

        val a = ArrayList<Set<Cube>>(_args.size)
        for (n in args) {
            if (n.isLit) {
                val litCubeSet = ImmutableSet.of(n.asLit.asCube)
                a.add(litCubeSet)
            } else {
                val cubes = n.cubesRough
                a.add(cubes)
            }

        }

        val cartesianProduct = Sets.cartesianProduct<Cube>(a)
        val flatCubes = flattenCubes(cartesianProduct)

        assert(cartesianProduct.size == flatCubes.size)

        return flatCubes
    }
     */

    override fun computeCubesRough(): Set<Cube> {

        val a = ArrayList<Set<Cube>>(_args.size)
        for (n in args) {
            if (n.isLit) {
                val litCubeSet = ImmutableSet.of(n.asLit.asCube)
                a.add(litCubeSet)
            } else {
                val cubes = n.cubesRough
                a.add(cubes)
            }

        }

        val cartesianProduct = Sets.cartesianProduct<Cube>(a)
        val flatCubes = flattenCubes(cartesianProduct)

        assert(cartesianProduct.size == flatCubes.size)

        return flatCubes
    }

    /*

   public Set<Cube> computeCubesSmooth() {

        ArrayList<Set<Cube>> a = new ArrayList<Set<Cube>>(args.length);

        for (Exp n : args) {
            Set<Cube> cubes = n.getCubesSmooth();
            a.add(cubes);
        }

        Set<List<Cube>> cartesianProduct = Sets.cartesianProduct(a);
        Set<Cube> flatCubes = flattenCubes(cartesianProduct);

        assert cartesianProduct.size() == flatCubes.size();

        return flatCubes;
    }


     */

    override fun computeCubesSmooth(): Set<Cube> {

        val a = ArrayList<Set<Cube>>(_args.size)

        for (n in args) {
            val cubes = n.cubesSmooth
            a.add(cubes)
        }

        val cartesianProduct = Sets.cartesianProduct<Cube>(a)
        val flatCubes = flattenCubes(cartesianProduct)

        assert(cartesianProduct.size == flatCubes.size)

        return flatCubes
    }

    fun computeCubeCount(): Int {
        var c = 1
        for (arg in args) {
            val cc = arg.cubeCount
            c = c * cc
        }
        return c
    }

    override fun computeSatCount(): BigInteger {
        var c = BigInteger.ONE
        for (arg in args) {
            val satCount = arg.satCount
            //            c = c * satCount;
            c = satCount.multiply(c)
        }
        return c
    }

    fun flattenCubes(cartesianProduct: Set<List<Cube>>): Set<Cube> {
        val flats = HashSet<Cube>()

        for (cubes in cartesianProduct) {
            val flat = flattenCube(cubes)
            flats.add(flat)
        }

        return flats
    }

    fun flattenCube(cubes: List<Cube>): Cube {
        return flattenCube4(cubes)
        //        return flattenCube5(cubes);
        //        return flattenCube3(cubes);
    }


    fun flattenCube4(cubes: List<Cube>): Cube {
        val space = space
        val a = DynCube(space)
        a.assignCubes(cubes)
        return a
    }


    override fun computeValue(cube: Cube): Int {
        var value = 1
        for (arg in args) {
            value *= arg.computeValue(cube)
        }
        return value
    }

    override fun computeValue(): Int {
        assert(_space.pics != null)
        if (this._value == null) {
            var v = 1
            for (arg in args) {
                v *= arg.computeValue()
            }
            this._value = v
        }
        return this._value!!
    }

    override fun computeSat(lit: Lit): Boolean {
        for (child in args) {
            val sat = child.computeSat(lit)
            if (!sat) return false
        }
        return true
    }

    override fun computeSat(cube: Cube): Boolean {
        for (child in args) {
            val sat = child.computeSat(cube)
            if (!sat) return false
        }
        return true
    }

    override fun computeSat(trueVars: VarSet): Boolean {
        for (child in args) {
            val sat = child.computeSat(trueVars)
            if (!sat) return false
        }
        return true
    }

    override fun computeSatCount1(lit: Lit): Long {
        var c = 1L
        for (arg in args) {
            val satCount = arg.computeSatCount1(lit)
            c = c * satCount
        }
        return c
    }


    override fun computeIsSat(): Boolean {
        for (arg in args) {
            val sat = arg.isSat
            if (!sat) {
                return false
            }
        }
        return true
    }

    override fun flatten(): Exp {
        val space = space

        if (isFlat) {
            return this
        }

        val b = ImmutableSet.builder<Exp>()
        for (arg in args) {
            val argFlat = arg.flatten()
            if (argFlat.isDAnd) {
                for (aa in argFlat.args) {
                    val aaFlat = aa.flatten()
                    b.add(aaFlat)
                }
            } else {
                b.add(arg)
            }
        }

        val retVal = b.build()

        for (arg in retVal) {
            assert(!arg.isNested(this))
        }

        return space.mkDAnd(retVal)
    }

    override fun serializeTinyDnnf(a: Ser) {
        a.append(A_SP)
        serializeArgsTinyDnnf(a)
    }

    /*
    public DAnd smooth(VarSet dontCares) {
        if (dontCares == null || dontCares.isEmpty()) {
            return this;
        }

        ArgBuilder bAnd = new ArgBuilder(_space, Op.DAnd);


        //add current args
        for (Exp arg : args) {
            bAnd.addExp(arg);
        }

        //add special dontCare DOrs
        for (Var dontCare : dontCares) {
            DcOr dcOr = dontCare.mkDcOr();
            bAnd.addExp(dcOr);
        }


        return bAnd.mk().asDAnd();
    }
     */
    override fun smooth(dontCares: VarSet): DAnd {
        if (dontCares.isEmpty()) {
            return this
        }

        val bAnd = ArgBuilder(_space, Op.DAnd)


        //add current args
        for (arg in args) {
            bAnd.addExp(arg)
        }

        //add special dontCare DOrs
        for (dontCare in dontCares) {
            val dcOr = dontCare.mkDcOr()
            bAnd.addExp(dcOr)
        }


        return bAnd.mk().asDAnd
    }

    override val smooth: Exp
        get() {
            if (_smooth == null) {
                _smooth = if (isSmooth) {
                    this
                } else {
                    computeSmooth()
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

        val b = ArgBuilder(_space, op())
        for (child in args) {
            val smoothChild = child.smooth
            b.addExp(smoothChild)
        }
        val smooth = b.mk()
        smooth._setIsSmooth()

        return smooth
    }

    override fun _setIsSmooth() {
        _smooth = this
        _isSmooth = true
    }

    companion object {

        private val A_SP = "A "
    }
}
