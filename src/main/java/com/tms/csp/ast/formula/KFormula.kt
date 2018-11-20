package com.tms.csp.ast.formula

import com.google.common.collect.ArrayListMultimap
import com.tms.csp.Structure
import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.*
import com.tms.csp.ast.PLConstants.*
import com.tms.csp.util.varSets.VarSet


sealed class FccState {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

class Open : FccState()
class Fcc : FccState()
data class Fccs(val args: List<Exp>) : FccState()

/**
 * represents a set of complex constraints
 * <p>
 * A formula is an and with all complex args
 * <p>
 * if may be an fcc, or not
 *
 */
class KFormula(space: Space, expId: Int, args: Array<Exp>, var fcc: FccState = Open()) : And(space, expId, args), FConstraintSet {


    //cache computed values

    /*
     fcc = null:  fcc and complexFccs have not been computed yet
     fcc != null: fcc and complexFccs have been computed
     fcc = true:  means this *is* an fcc, complexFccs set to null
     fcc = false: means this is not an fcc, complexFccs should be non-null, size >= 2
     */

//    var _fccs: List<Exp>? = null


    private var fVars: FVars? = null

    private var dnnf: Exp? = null

    private var _bb: DynCube? = null

    init {
//        println("init: $fcc")
    }

    fun getBestXorSplit(): Xor? {
//        println("KFormula.getBestXorSplit")
        return getBestXorSplit1()
//        return getBestXorSplit2()
    }

    //faster
    fun getBestXorSplit1(): Xor? {
        return if (_space.hasPrefixes()) {
            XorCounts.getMax(this)
        } else {
            val xors = getXorConstraints()
            if (xors.isEmpty()) null else xors.get(0).asXor
        }
    }

    //slower
    fun getBestXorSplit2(): Xor? {
        //requires up-front processing
        var best: Xor? = null
        for (a in _args) {
            if (a is Xor && (best == null || a.score > best.score)) {
                best = a
            }
        }
        return best
    }

//    override fun getComplexFccs(): Exp? {
//        if (fcc == null) {
//            _fccs = computeComplexFccs()
//        }
//        return _fccs
//    }


    fun getTopXorSplit(): Xor? {
        var x: Xor?

        x = getYearXor()
        if (x != null) return x

        x = getSeriesXor()
        return x ?: getModelXor()

    }

    fun getFVars(): FVars {
        if (fVars == null) {
            fVars = FVars(argIt)
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
        return Csp.computeXorConstraints(argIt)
    }

    /**
     * Active:
     *      lit -> lit
     *      lit -> cube
     *      lit -> !or(all lits)
     *
     *      !or(a b c) is equiv to and(!a !b !c)
     */
    fun findAllActiveConstraints() {

    }


    override val satCountPL: Long
        get() {

            fun topXorSplitSatCount(): Long? {

                val xor: Xor? = getTopXorSplit()
                return if (xor != null) {
                    val split = XorSplit(this, xor.asXor)
                    split.plSatCount()
                } else {
                    null
                }

            }

            fun bestXorSplitSatCount(): Long? {
                val xor1: Xor? = getBestXorSplit()
                return if (xor1 != null) {
                    val split = XorSplit(this, xor1.asXor)
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
//            val dcVars = parentVars.minus(this._complexVars)
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
            val vr = decide()
            val split = FormulaSplit(this, vr)
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
//            println("topXorSplit[${xor.prefix}]..")
            xorSplitToDnnf(xor)
        } else {
            null
        }
    }


    private fun toDnnfBestXorSplit(): Exp? {
        val xor = getBestXorSplit()
        return if (xor != null) {
//            println("bestXorSplit[${xor.prefix}]..")
            xorSplitToDnnf(xor)
        } else {
            null
        }
    }

    private fun toDnnfBestLitImp(): Exp? {
        val vr: Var?
        try {
            vr = decideLitImp()
        } catch (e: ImpliedLitException) {
//            println("ImpliedLitException: ${e.lit}")
            val csp = Csp(this.args, e.lit)
            return csp.toDnnf()
        }
        return if (vr != null) {
            return FormulaSplit(this, vr).toDnnf()
        } else {
            null
        }
    }

    private fun xorSplitToDnnf(xor: Exp): Exp {
        val split = XorSplit(this, xor.asXor)
        return split.toDnnf()
    }

    private fun decisionSplit(decisionVar: Var): Exp {
        val split = FormulaSplit(this, decisionVar)
        return split.toDnnf()
    }


    fun getYearXor(): Xor? {
        return getXor(YR_PREFIX)
    }

    fun getSeriesXor(): Xor? {
        return getXor(SER_PREFIX)
    }

    fun getModelXor(): Xor? {
        return getXor(MDL_PREFIX)
    }

    fun getXColXor(): Xor? {
        return getXor(XCOL_PREFIX)
    }

    fun getDealerXor(): Xor? {

        for (e in argIt) {
            if (e.isXor) {
                if (e.hasDealers()) {
                    return e.asXor
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

        if (fcc is Open) {
//            println("computeComplexFccs..")
            fcc = computeFccs()
        }

        return if (fcc is Fcc) {
//            println("fccToDnnf..")
            fccToDnnf()
        } else if (fcc is Fccs) {
//            println("fccsToDnnf..")
            fccsToDnnf(fcc as Fccs)
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


    private fun decide(): Var {
        val fVars = getFVars()
        try {
            val d = fVars.decide()
            return d.vr
        } catch (e: NoVarsException) {
            throw NoVarsException(this.toString())
        }


    }

    fun computeLitImps(): ActualLitImps {
        return ActualLitImps(_args)
    }


    override fun print() {
        System.err.println(simpleName)
        println("<formula>")
        println("  <constraints>")
        _args.forEach { println("    $it") }
        println("  </constraints>")
        println("  <litImps>")
        computeLitImps().varImps.forEach { println("    $it") }
        println("  </litImps>")
        println("</formula>")
    }

    private fun decideLitImp(): Var? {
        val xx = ActualLitImps(_args)
        val best = xx.best()
        if (best == null) {
            return null
        } else {
            val bestVar = space.getVar(best)
            return bestVar
        }
    }


    fun getXor(xorPrefix: String): Xor? {
        for (exp in argIt) {
            if (exp.isXor && exp.asXor.prefix.equals(xorPrefix, ignoreCase = true)) {
                return exp.asXor
            }
        }
        return null
    }


    fun fccToDnnf(): Exp {
        val bestXorSplit = toDnnfBestXorSplit()
        if (bestXorSplit != null) {
            return bestXorSplit
        }

        val bestLitImp = toDnnfBestLitImp()
        if (bestLitImp != null) {
            return bestLitImp
        }


        val vr = FVars2.decide(_args)!!
//        val vr = decide()
//        println("decide[$vr]..")
        return decisionSplit(vr)
    }

    fun fccsToDnnf(fcc: Fccs): Exp {
        val b = ArgBuilder(space, Op.DAnd)
        for (f in fcc.args) {
            val d = f.toDnnf()
            if (d.isFalse) return d
            if (d.isTrue) continue
            b.addExp(d)
        }
        return b.mk()
    }


    override fun isDirectlyRelated(c1: Int, c2: Int): Boolean {
        val complex1 = _args[c1]
        val complex2 = _args[c2]

        assert(complex1.isComplex)
        assert(complex2.isComplex)

        val vs1 = complex1.vars
        val vs2 = complex2.vars

        return vs1.anyVarOverlap(vs2)
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
        //        System.err.println("testing xor prefix[" + xorPrefix + "] for dead _complexVars: ");

        val xorVars: VarSet
        var xor = getXor(xorPrefix)
        if (xor == null) {
            xorVars = getVarsForPrefix(xorPrefix)
            //            xor = new Xor(xorVars, Integer.MAX_VALUE);
            val exp = _space.mkXor(xorVars)
            if (exp.isXor) {
                xor = exp.asXor
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
        //        System.err.println("testing prefix[" + prefix + "] for dead _complexVars");


        val vars = vars.filter(prefix)

        for (vr in vars) {
            //            System.err.println("    testing vr[" + vr + "]");
            val bbLit = proposeBothWays(vr)
            if (bbLit != null) {
                bb.assign(bbLit)
                //                System.err.println("bbLit[" + bbLit + "]");
            }
        }

    }


    //    fun getConstraintCount(): Int {
//        return argCount
//    }
    override val constraintCount: Int get() = argCount

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

//    /**
//     * return null if this *is* an fcc
//     */
//    fun computeComplexFccs2(): List<List<Exp>>? {
//        assert(isAllComplex)
//        assert(isFormula)
//        assert(!isDnnf)
//
//        assert(this.fcc is Open)
//
//        val uf = computeUnionFind()
//
//        val fccCount = uf.fccCount
//
//        if (fccCount == 1) {
//            this.fcc = true
//            return null
//        }
//
//        val mm = ArrayListMultimap.create<Int, Exp>()
//        for (i in 0 until constraintCount) {
//            val fccKey = uf.getFccFor(i)
//            val constraint = getArg(i)
//            mm.put(fccKey, constraint)
//        }
//
//        val disjoint = ArrayList<List<Exp>>()
//        for (key in mm.keySet()) {
//            disjoint.add(mm.get(key))
//        }
//
//        return disjoint
//
//    }


    fun computeFccs(): FccState {
        assert(isAllComplex) { this }
        assert(isFormula)
        assert(!isDnnf)

        assert(this.fcc is Open)

        val uf = computeUnionFind()

        val fccCount = uf.fccCount
//        println("Compute FCCs: fccCount = ${fccCount}")
        if (fccCount == 1) {
            return Fcc()
        } else {
            val mm = ArrayListMultimap.create<Int, Exp>()
            for (i in 0 until constraintCount) {
                val fcc = uf.getFccFor(i)
                val constraint = getArg(i)
                mm.put(fcc, constraint)
            }
            val list = mutableListOf<Exp>()
            for (key in mm.keySet()) {
                val fccConstraints = mm.get(key)
                val bFcc = ArgBuilder(_space, Op.Fcc, fccConstraints)
                bFcc.structure = Structure.Fcc
                val fccExp = bFcc.mk()
                list.add(fccExp)
            }
            return Fccs(list)
        }
    }

    override val bb: DynCube
        get() {
            if (_bb == null) {
                _bb = computeBB()
            }
            return _bb!!
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


    override val op: Op get() = Op.Formula


    fun copyArray(): Array<Exp> = _args.copyOf()
}

/*
        this.fcc = false
            this._fccs = list
 */



