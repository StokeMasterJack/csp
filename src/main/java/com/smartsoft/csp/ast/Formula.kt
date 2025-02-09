package com.smartsoft.csp.ast

import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.ast.PLConstants.*
import com.smartsoft.csp.litImp.ImpliedLitException
import com.smartsoft.csp.litImp.Kb
import com.smartsoft.csp.litImp.VarImps
import com.smartsoft.csp.ssutil.prident
import com.smartsoft.csp.util.it.Structure
import com.smartsoft.csp.varSet.VarSet


sealed class FccState {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    val fccCount: Int
        get() = when (this) {
            is Fcc -> 1
            is Fccs -> this.args.size
            is Open -> -1
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
class Formula(space: Space, expId: Int, args: Array<Exp>, var fcc: FccState = Open()) : And(space, expId, args), FConstraintSet<Exp> {


    //cache computed values

    private var dnnf: Exp? = null

    private var _bb: DynCube? = null

    val litImps: Kb get() = lazyLitImps.value

    private val lazyLitImps: Lazy<Kb> = lazy { Kb.computeFromFormula(this) }

    fun printXorInfo() {
        println("Formula.xorInfo:")
        println("   xors: ${xors}")
        println("   xorsDeep: $xorsDeep")
        println("   xorsDeepMinusTop: ${xorsDeep.map { it as Xor }.filterNot { it.isTop }.map { it.prefix }}")
    }


    fun getTopXorSplit(): Xor? {
        var x: Xor?

        x = getYearXor()
        if (x != null) return x

        x = getSeriesXor()
        return x ?: getModelXor()

    }

    val xors: List<Exp> get() = Csp.computeXorConstraints(argIt)

    fun getXorConstraints(): List<Exp> {
        return Csp.computeXorConstraints(argIt)
    }

    val xorCount: Int = xors.size

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


            val li = litImps

            fun bestXorSplitSatCount(): Long? {
                val xor1: Xor? = li.bestXor
                return if (xor1 != null) {
                    val split = XorSplit(this, xor1.asXor)
                    split.plSatCount()
                } else {
                    null
                }
            }

            fun decisionSplitSatCount(): Long {
                val best: VarImps = li.getBestVarImps()
                val split = FormulaSplit(this, best)
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


        }


    override fun computeIsSat(): Boolean {
        val xor1: Xor? = getTopXorSplit();
        if (xor1 != null) {
            return XorSplit(this, xor1).isSat
        }

        val li = try {
            litImps
        } catch (e: ImpliedLitException) {
            return Csp(this, e.lit).isSat()
        }

        val xor2 = li.bestXor
        if (xor2 != null) {
            return XorSplit(this, xor2).isSat
        }

        val best = li.getBestVarImps()
        return FormulaSplit(this, best).isSat
    }

    override fun print(heading: String) {
        for (arg in args) {
            System.err.println("  $arg")
        }
    }

    private fun toDnnfTopXorSplit(): Exp? {
        val xor = getTopXorSplit()
        return if (xor != null) {
//            prident(depth, "topXorSplit[${xor.prefix}]..")
            xorSplitToDnnf(xor)
        } else {
            null
        }
    }


    private fun xorSplitToDnnf(xor: Exp): Exp {
        val split = XorSplit(this, xor.asXor)
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
//            println("cacheMiss")
        } else {
//            println("cacheHit")
        }
        assert(dnnf != null)
        assert(dnnf!!.isDnnf) { dnnf!! }
        return dnnf!!
    }

    private fun toDnnfInternal(): Exp {

//        println("depth = ${depth}")
        val topXorSplit = toDnnfTopXorSplit()

        if (topXorSplit != null) {
            return topXorSplit
        }

        if (fcc is Open) {
//            prident(depth, "computeComplexFccs..")
            fcc = computeFccs()
//            prident(depth, "  fccCount[${fcc.fccCount}]")
        }

        return when (fcc) {
            is Fcc -> fccToDnnf()
            is Fccs -> fccsToDnnf(fcc as Fccs)
            else -> throw IllegalStateException()
        }


    }


    override fun print() {
        System.err.println(simpleName)
        println("<formula>")
        println("  <constraints>")
        _args.forEach { println("    $it") }
        println("  </constraints>")
        println("  <litImps>")
        litImps.varImpsList.forEach { println("    $it") }
        println("  </litImps>")
        println("</formula>")
    }

    companion object {
        var globalDelta: Long = 0
    }

//    private fun decideLitImp(): Var? {
//        val xx = LitImps(_args)
//        val best = xx.best()
//        if (best == null) {
//            return null
//        } else {
//            val bestVar = space.getVar(best)
//            return bestVar
//        }
//    }


    fun getXor(xorPrefix: String): Xor? {
        for (exp in argIt) {
            if (exp.isXor && exp.asXor.prefix.equals(xorPrefix, ignoreCase = true)) {
                return exp.asXor
            }
        }
        return null
    }


    fun fccToDnnf(): Exp {

        val kb: Kb = try {
            litImps
        } catch (e: ImpliedLitException) {
//            prident(depth, "ImpliedLitException: ${e.lit}")
            val csp = Csp(this, e.lit)
            return csp.toDnnf()
        }


        val xorInfo = kb.bestXorInfo
//        prident(depth, "xorCount: $xorCount  xorDeepCount: $xorsDeepCount  BestXor: $xorInfo")

        if (xorInfo != null) {
            return XorSplit(this, xorInfo.xor).toDnnf()
        }

        val best: VarImps = kb.getBestVarImps()
//        best.printLitImpsLite(depth)

        return FormulaSplit(this, best).toDnnf()


    }

    fun fccsToDnnf(fcc: Fccs): Exp {
        val b = ArgBuilder(space, Op.DAnd)
        for (f in fcc.args) {
            f.addParent(this)
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

        val split = XorSplit(this, xor)

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

    override fun getConstraint(index: Int): Exp {
        return getArg(index)
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

//        println("Compute FCCs: fccCount = ${fccCount}")
        if (uf.fccCount == 1) {
            return Fcc()
        } else {
            val list = mutableListOf<Exp>()
            for (fccConstraints: List<Exp> in uf.fccs) {
                //sp: Space, op: Op, args: Iterable<Exp>, condition: Condition = Condition.identity, fcc: FccState = Open(), flatten: Boolean = true
                val bFcc = ArgBuilder(
                        sp = _space,
                        op = Op.Fcc,
                        args = fccConstraints,
                        fcc = Fcc()
                )
                bFcc.structure = Structure.Fcc
                val fccExp = bFcc.mk()
                fccExp.addParent(this)
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



