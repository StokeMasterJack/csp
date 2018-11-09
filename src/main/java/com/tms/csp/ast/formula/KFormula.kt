package com.tms.csp.ast.formula

import com.google.common.collect.ArrayListMultimap
import com.tms.csp.Structure
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.*
import com.tms.csp.util.varSets.VarSet
import java.util.*


//interface FF {
//
//
//    val topXorSplit: Xor?
//    val bestXorSplit: Xor?
//    val decide: Var
//}

class KFormula(space: Space, expId: Int, args: Array<Exp>, var fcc: Boolean?) : And(space, expId, args), FConstraintSet {

    var _complexFccs: Exp? = null


    private var fVars: FVars? = null

    private var dnnf: Exp? = null
    private var bb: DynCube? = null

    fun getBestXorSplit(): Xor? {
        if (_space.hasPrefixes()) {
            return XorCounts.getMax(this)
        } else {
            val xors = getXorConstraints()
            return if (xors.isEmpty()) null else xors.get(0).asXor()
        }
    }

    override fun getComplexFccs(): Exp? {
        if (fcc == null) {
            _complexFccs = computeComplexFccs()
        }
        return _complexFccs
    }


    fun getTopXorSplit(): Xor? {
        var x: Xor?

        x = getYearXor()
        if (x != null) return x

        x = getSeriesXor()
        return x ?: getModelXor()

    }

    fun getFVars(): FVars {
        if (fVars == null) {
            fVars = FVars(this)
            if (fVars!!.sortedList.isEmpty()) {
                throw IllegalStateException()
            }
        } else {
            if (fVars!!.sortedList.isEmpty()) {
                throw IllegalStateException()
            }
        }
        return fVars!!
    }


    fun getXorConstraints(): List<Exp> {
        return Csp.getXorConstraints(argIt())
    }


    override fun satCountPL(): Long {

        fun topXorSplitSatCount(): Long? {

            val xor: Xor? = getTopXorSplit()
            return if (xor != null) {
                val split = XorSplit(this, xor.asXor())
                split.plSatCount()
            } else {
                null
            }

        }

        fun bestXorSplitSatCount(): Long? {
            val xor1: Xor? = getBestXorSplit()
            return if (xor1 != null) {
                val split = XorSplit(this, xor1.asXor())
                split.plSatCount()
            } else {
                null
            }
        }

        fun decisionSplitSatCount(): Long {
            val decisionVar: Var = decide()
            val split = FormulaSplit(this, decisionVar)
            return split.plSatCount()
        }

        fun satCount(): Long {
            val sc1 = topXorSplitSatCount()
            if (sc1 != null) {
                return sc1
            }


            val sc2 = bestXorSplitSatCount()
            if (sc2 != null) {
                return sc2
            }

            val sc3 = decisionSplitSatCount();
            return sc3;
        }


        return satCount()

//
//        val satCountWithDc = if (parentVars.isNullOrEmpty()) {
//            baseSatCount
//        } else {
//            val dcVars = parentVars.minus(this._vars)
//            val pow = Math.pow(2.0, dcVars.size.toDouble()).toLong()
//            baseSatCount * pow
//        }
//
//        return satCountWithDc


    }


    override fun computeIsSat(): Boolean {
        val xor = getBestXorSplit()
        if (xor != null) {
            val split = XorSplit(this, xor)
            return split.isSat
        } else {
            val `var` = decide()
            val split = FormulaSplit(this, `var`)
            return split.isSat
        }
    }

    override fun print(heading: String) {
        for (arg in args) {
            System.err.println("  $arg")
        }
    }

    private fun toDnnfTopXorSplit(): Exp? {
        val xor = getTopXorSplit()
        return if (xor != null) {
            xorSplitToDnnf(xor)
        } else {
            null
        }
    }


    private fun toDnnfBestXorSplit(): Exp? {
        val xor = getBestXorSplit()
        return if (xor != null) {
            xorSplitToDnnf(xor)
        } else {
            null
        }
    }

    private fun xorSplitToDnnf(xor: Exp): Exp {
        val split = XorSplit(this, xor.asXor())
        return split.toDnnf()
    }

    private fun decisionSplit(decisionVar: Var): Exp {
        val split = FormulaSplit(this, decisionVar)
        return split.toDnnf()
    }


    fun getYearXor(): Xor? {
        return getXor(PLConstants.YR_PREFIX)
    }

    fun getSeriesXor(): Xor? {
        return getXor(PLConstants.SER_PREFIX)
    }

    fun getModelXor(): Xor? {
        return getXor(PLConstants.MDL_PREFIX)
    }

    fun getXColXor(): Xor? {
        return getXor(PLConstants.XCOL_PREFIX)
    }

    fun getDealerXor(): Xor? {

        for (e in argIt()) {
            if (e.isXor()) {
                if (e.hasDealers()) {
                    return e.asXor()
                }
            }
        }

        return null
    }

    override fun toDnnf(): Exp {
        if (dnnf == null) {
            dnnf = toDnnfInternal()
        }
        assert(dnnf != null)
        assert(dnnf!!.isDnnf) { dnnf!! }
        return dnnf!!
    }

    private fun toDnnfInternal(): Exp {
//        return toDnnfInternalNoFccNoXorSplits();
//        return toDnnfInternalNoFcc();
        return toDnnfInternalWithFccAndXorSplits();
    }

    private fun toDnnfInternalWithFccAndXorSplits(): Exp {

        val topXorSplit = toDnnfTopXorSplit()
        if (topXorSplit != null) {
            return topXorSplit
        }

        if (fcc == null) {
            assert(_complexFccs == null)

            val fccs = complexFccs //returns null if *this* is an fcc
            if (fccs == null) {
                fcc = true
                _complexFccs = null
                return fccSplitDnnf()
            } else {
                fcc = false
                _complexFccs = fccs
                return fccs.toDnnf() ?: error(fccs.javaClass.toString() + " returned null from toDnnf()")
            }

        } else if (fcc != null && fcc!!) {

            val bestXorSplit = toDnnfBestXorSplit()
            if (bestXorSplit != null) {
                return bestXorSplit
            }

            val vr = decide()
            val decisionSplit = decisionSplit(vr)
            return decisionSplit

        } else if (fcc != null && !fcc!!) {
            System.err.println("complexFccs: $complexFccs")
            assert(complexFccs != null)  //boom
            return complexFccs!!.toDnnf()!!
        } else {
            throw IllegalStateException()
        }


    }

    private fun toDnnfInternalNoFcc(): Exp {

        val topXorSplit = toDnnfTopXorSplit()
        if (topXorSplit != null) {
            return topXorSplit
        }

        val bestXorSplit = toDnnfBestXorSplit()
        if (bestXorSplit != null) {
            return bestXorSplit
        }

        val vr = decide()
        return decisionSplit(vr)


    }


    private fun toDnnfInternalNoFccNoXorSplits(): Exp {
        val vr = decide()
        return decisionSplit(vr)
    }


    fun decide(): Var {
        val fVars = getFVars()
        try {
            val d = fVars.decide()
            return d.vr
        } catch (e: NoVarsException) {
            throw NoVarsException(this.toString())
        }


    }


    fun getXor(xorPrefix: String): Xor? {
        for (exp in argIt()) {
            if (exp.isXor && exp.asXor().prefix.equals(xorPrefix, ignoreCase = true)) {
                return exp.asXor()
            }
        }
        return null
    }


    fun fccSplitDnnf3(): Exp {

        val xor = getBestXorSplit()
        if (xor != null) {
            return xorSplitToDnnf(xor)
        } else {
            val `var` = decide()
            return decisionSplit(`var`)
        }
    }

    fun fccSplitDnnf1(): Exp {
        val xor = getBestXorSplit()
        if (xor != null) {
            System.err.println("XorSplitting c $xor")
            val split = XorSplit(this, xor)
            return split.toDnnf()
        } else {
            val `var` = decide()
            val split = FormulaSplit(this, `var`)
            return split.toDnnf()
        }
    }

    override fun isDirectlyRelated(index1: Int, index2: Int): Boolean {
        val complex1 = args[index1]
        val complex2 = args[index2]

        assert(complex1.isComplex)
        assert(complex2.isComplex)

        val vs1 = complex1.vars
        val vs2 = complex2.vars

        return vs1.anyVarOverlap(vs2)
    }


    fun fccSplitDnnf(): Exp {
        return fccSplitDnnf3()
    }


    override fun computeBB(): DynCube {
        val vrs = vars

        val aa = DynCube(space)

        for (prefix in vrs.getXorPrefixes()) {
            computeBbForXorPrefix(prefix, aa)
        }


        for (prefix in vrs.getNonXorPrefixes()) {
            computeBbForNonXorPrefix(prefix, aa)
        }
        return aa
    }


    private fun computeBbForXorPrefix(xorPrefix: String, bb: DynCube) {
        //        System.err.println("testing xor prefix[" + xorPrefix + "] for dead _vars: ");

        val xorVars: VarSet
        var xor = getXor(xorPrefix)
        if (xor == null) {
            xorVars = getVarsForPrefix(xorPrefix)
            //            xor = new Xor(xorVars, Integer.MAX_VALUE);
            val exp = _space.mkXor(xorVars)
            if (exp.isXor) {
                xor = exp.asXor()
            } else {
                computeBbForNonXorPrefix(xorPrefix, bb)
                return
                //                throw new IllegalStateException("Prefix[" + xorPrefix + "] not an xor: [" + exp + "]");
            }

        } else {
            xorVars = xor.vars
        }

        val split = XorSplit(this, xor!!)

        val xorFalseVars = space.newMutableVarSet()

        var lastOpenVar: Var? = null
        for (trueVar in xorVars) {

            //            System.err.println("    testing vr[" + vr + "]");


            val r = split.mkCsp(trueVar)

            val sat = r.isSat()
            if (!sat) {
                xorFalseVars.add(trueVar)
                //                System.err.println("bbLit[" + vr.nLit() + "]");
            } else {
                lastOpenVar = trueVar
            }
        }

        for (xorFalseVar in xorFalseVars) {
            bb.assign(xorFalseVar, false)
        }

        val openCount = xorVars.size - xorFalseVars.size
        if (openCount == 1) {
            bb.assign(lastOpenVar, true)
        }


    }


    private fun computeBbForNonXorPrefix(prefix: String, bb: DynCube) {
        //        System.err.println("testing prefix[" + prefix + "] for dead _vars");


        val vars = vars.filter(prefix)

        for (`var` in vars) {
            //            System.err.println("    testing vr[" + vr + "]");
            val bbLit = proposeBothWays(`var`)
            if (bbLit != null) {
                bb.assign(bbLit)
                //                System.err.println("bbLit[" + bbLit + "]");
            }
        }

    }

    override fun isFormula(): Boolean {
        return true
    }


    override fun getConstraintCount(): Int {
        return argCount
    }

//
//    override fun iterator(): Iterator<Exp> {
//        return argIter()
//    }

//    override fun iterator(): Iterator<Exp> {
//        return argIter()
//    }

    override fun varIt(): Iterable<Var> {
        return vars.varIt()
    }


    /**
     * return null if this *is* an fcc
     */
    override fun computeComplexFccs(): Exp? {
        assert(isAllComplex) { this }
        assert(isFormula)
        assert(!isDnnf)

        assert(this.fcc == null)

        val uf = computeUnionFind()
        uf.processAllUniquePairs()

        val fccCount = uf.fccCount

        if (fccCount == 1) {
            this.fcc = true
            return null
        }

        val mm = ArrayListMultimap.create<Int, Exp>()

        for (i in 0 until constraintCount) {
            val fcc = uf.getFccFor(i)
            val constraint = getArg(i)
            mm.put(fcc, constraint)
        }

        val keySet = mm.keySet()

        val bFccs = ArgBuilder(_space, Op.DAnd)
        for (key in keySet) {
            if (bFccs.isShortCircuit) {
                break
            }
            val fccConstraints = mm.get(key)
            val bFcc = ArgBuilder(_space, Op.Formula, fccConstraints)

            bFcc.structure = Structure.Fcc

            val fccExp = bFcc.mk()

            if (fccExp.isFalse) {
                return _space.mkFalse()
            }

            val fccExpDnnf = fccExp.toDnnf()

            if (fccExpDnnf.isFalse) {
                return _space.mkFalse()
            }

            bFccs.addExp(fccExpDnnf)
        }


        return bFccs.mk()


    }

    override fun getBB(): DynCube {
        if (bb == null) {
            bb = computeBB()
        }
        return bb!!
    }

    private fun proposeBothWays(vr: Var): Lit? {
        val split = FormulaSplit(this, vr)
        val tt = split.mkCsp(true)
        if (!tt.isSat()) {
            //must be fCon
            //            System.err.println("  found bb lit[" + vr.mkNegLit() + "]");
            return vr.nLit()
        } else {

            val ff = split.mkCsp(false)
            return if (!ff.isSat()) {
                //must be tCon
                //                System.err.println("  found bb lit[" + vr.mkPosLit() + "]");
                vr.pLit()
            } else {
                null //open
            }
        }
    }


    fun isXorPrefix(prefix: String): Boolean {
        if (Prefix.isXor(prefix)) {
            return true
        }
        val vr = getFirstVarForPrefix(prefix)
        return vr!!.isXorChild()
    }

    private fun getVarsForPrefix(prefix: String): VarSet {
        return vars.filter(prefix)
    }

    fun computeBbForPrefix(prefix: String, bb: DynCube) {
        if (isXorPrefix(prefix)) {
            computeBbForXorPrefix(prefix, bb)
        } else {
            computeBbForNonXorPrefix(prefix, bb)
        }
    }

    fun computeBbForPrefix(prefix: String): DynCube {
        val bb = DynCube(space)
        computeBbForPrefix(prefix, bb)
        return bb
    }


    fun computeBbForPrefix(prefix: Prefix): DynCube {
        return computeBbForPrefix(prefix.getName())
    }


    fun computeBbYears(): DynCube {
        return computeBbForPrefix(Prefix.YR)
    }

    fun computeBbModels(): DynCube {
        return computeBbForPrefix(Prefix.MDL)
    }

    fun computeBbDrives(): DynCube {
        return computeBbForPrefix(Prefix.DRV)
    }

    fun getFirstVarForPrefix(prefix: String): Var? {
        return vars.getFirstVarForPrefix(prefix)
    }


    /**
     * return null if this *is* an fcc
     */
    fun computeComplexFccs2(): List<List<Exp>>? {
        assert(isAllComplex)
        assert(isFormula)
        assert(!isDnnf)

        assert(this.fcc == null)

        val uf = computeUnionFind()
        uf.processAllUniquePairs()

        val fccCount = uf.fccCount

        if (fccCount == 1) {
            this.fcc = true
            return null
        }

        val mm = ArrayListMultimap.create<Int, Exp>()
        for (i in 0 until constraintCount) {
            val fccKey = uf.getFccFor(i)
            val constraint = getArg(i)
            mm.put(fccKey, constraint)
        }

        val disjoint = ArrayList<List<Exp>>()
        for (key in mm.keySet()) {
            disjoint.add(mm.get(key))
        }

        return disjoint

    }

    override fun getOp(): Op {
        return Op.Formula
    }


    override fun asFormula(): KFormula {
        return this
    }
}