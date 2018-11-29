package com.smartsoft.csp.litImp

import com.smartsoft.csp.ast.*
import com.smartsoft.csp.dnnf.products.Cube
import java.util.*


class Kb(
        private val space: Space,
        private val simple: DynCube = DynCube(space),
        private val litImpMap: MutableMap<Var, VarImps> = mutableMapOf(),
        private val xors: MutableList<Xor> = mutableListOf()) {

    private var best: VarImps? = null
    private var bestXor: Xor? = null

    //initial
    constructor(space: Space, complex: Array<Exp>) : this(space) {
        addAll(complex)
    }

    val expFactory: ExpFactory get() = space.expFactory
    val parser: Parser get() = space.parser

    fun assignAll(cube: Cube): Boolean {
        return simple.assignLits(cube)
    }

    fun assignAll(a: Assignments): Boolean {
        return a.assignToDynCube(simple)
    }

    fun assign(lit: Lit): Boolean {
        return simple.assign(lit)
    }

    fun toDnnf(): Exp {
        return expFactory.mkTrue()
    }

    fun getImps(lit: Lit): Assignments {
        return litImpMap[lit.vr]!!.imps(lit.sign())
    }

    fun checkState(lit: Lit) {
        val imps: Assignments = getImps(lit)

    }

    fun refineLitImpMap(lit: Lit): MutableMap<Var, VarImps> {
        val varImps: VarImps = litImpMap.remove(lit.vr)!!
        val imps: Assignments = varImps.imps(lit.sign())
        assign(lit)
        assignAll(imps)
        return mutableMapOf()
    }

    fun refineXors(lit: Lit): MutableList<Xor> {
        return mutableListOf()
    }

    fun refine(lit: Lit): Kb {
        val litImpMap = refineLitImpMap(lit)
        val xors = refineXors(lit)
        return Kb(space = space, litImpMap = litImpMap, xors = xors)
    }

    fun splitOnBestVarImps(): KbSplit {
        return KbSplit(parent = this, decision = best!!)
    }

    @Throws(ImpliedLitException::class)
    fun addComplex(cc: Exp, condition: ConditionOn) {
        val after = cc.condition(condition)
        addComplex(after)
    }

    @Throws(ImpliedLitException::class)
    fun addComplex(cc: Exp): Boolean {
        return if (cc is Xor) {
            addXor(cc.asXor)
        } else if (cc.isOrVv) {
            addVvOr(cc.asOr)
        } else if (cc is Or && cc.isPair && cc !is Xor) {
            val a1: Exp = cc._args[0]
            val a2: Exp = cc._args[1]
            if (a1 is Lit && a2 is Cube) {
                addConstraintLitOrCube(a1, a2)
            } else if (a1 is Cube && a2 is Lit) {
                addConstraintLitOrCube(a2, a1)
            } else if (a1 is Lit && a2.isNotClause) {
                addConstraintLitOrNotClause(a1, a2 as Not)
            } else if (a1.isNotClause && a2 is Lit) {
                addConstraintLitOrNotClause(a2, a1 as Not)
            } else {
                addOtherComplex(cc)
            }
        } else {
            //not xors and not vvs
            addOtherComplex(cc)
        }
    }

    //lit imp
    private fun addConstraintLitOrCube(lit: Lit, cube: Cube): Boolean {

        var anyChange = false

        //!lit imps cube
        val nLit = lit.flp

        for (cubeLit: Lit in cube) {
            val ch = addImp(nLit, cubeLit)
            if (ch) anyChange = true
        }

        //!cube imps lit
        for (cubeLit: Lit in cube) {
            val ch = addImp(cubeLit.flp, lit)
            if (ch) anyChange = true
        }

        return anyChange
    }

    private fun addConstraintLitOrNotClause(lit: Lit, notClause: Not): Boolean {
//        return addConstraintLitOrNotClause1(lit, notClause)
        return addConstraintLitOrNotClause2(lit, notClause)
    }

    private fun print() {
        println(litImpMap)
    }


    override fun equals(other: Any?): Boolean {
        return eq(other!! as Kb)
    }

    private fun eq(o: Kb): Boolean {
        return Objects.equals(litImpMap, o.litImpMap)
    }

    private fun addConstraintLitOrNotClause1(lit: Lit, notClause: Not): Boolean {

        var anyChange = false

        //!lit imps cube
        val nLit = lit.flp
        for (clauseLit in notClause.pos.args) {
            check(clauseLit is Lit)
            val ch = addImp(nLit, clauseLit.flp)
            if (ch) anyChange = true
        }

        //!cube imps lit
        for (clauseLit in notClause.pos.args) {
            check(clauseLit is Lit)
            val ch = addImp(clauseLit, lit)
            if (ch) anyChange = true
        }

        return anyChange

    }

    private fun addConstraintLitOrNotClause2(lit: Lit, notClause: Not): Boolean {
        val cube = notClause.notClauseToCube
        return addConstraintLitOrCube(lit, cube)
    }

    private fun addConstraintOrVv(lit1: Lit, lit2: Lit) {
        addImp(lit1.flp, lit2)
        addImp(lit1, lit2.flp)
    }


//    fun computeBestXor(): Xor? {
//        var _bestXor: XorCount? = null
//        for (xorCount in xors1.values) {
//            xorCount.xor.vars.forEach {
//                val score: Int = map[it]?.impCount ?: 0
//                xorCount.increment(score)
//            }
//            if (_bestXor == null || xorCount.count > _bestXor.count) {
//                _bestXor = xorCount
//            }
//        }
//        if (_bestXor != null && _bestXor.count > 0) {
//            return _bestXor.xor
//        } else {
//            return null
//        }
//    }

//    @Throws(ImpliedLitException::class)
//    fun addAll(parent: LitImps, condition: ConditionOn): LitImps {
//        complex.forEach {
//            addComplex(it)
//        }
//        return this
//    }


    @Throws(ImpliedLitException::class)
    fun addAll(complex: Array<Exp>, condition: ConditionOn? = null): Kb {
        complex.forEach {
            if (condition != null) {
                addComplex(it, condition)
            } else {
                addComplex(it)
            }

        }
        return this
    }

    private fun mkVarImps(vr: Var): VarImps {
        var varImps = litImpMap[vr]
        if (varImps == null) {
            varImps = VarImps(vr)
            litImpMap[vr] = varImps
        }
        return varImps
    }


    private fun mkXorCounts(vr: Var): VarImps {
        var varImps = litImpMap[vr]
        if (varImps == null) {
            varImps = VarImps(vr)
            litImpMap[vr] = varImps
        }
        return varImps
    }

    @Throws(ImpliedLitException::class)
    fun addImp(lit1: Lit, lit2: Lit): Boolean {
        val vi = mkVarImps(lit1.vr)
        val ch = if (lit1.isPos) {
            vi.addPImp(lit2)
        } else {
            vi.addNImp(lit2)
        }
        maybeUpdateBestVar(vi)
        return ch
    }


    //not xors and not vvs
    private fun addOtherComplex(complex: Exp): Boolean {
        var anyChange = false
        complex.vars.forEach {
            val vi = mkVarImps(it)
            val ch = vi.addOtherComplex(complex)
            if (ch) anyChange = true
            maybeUpdateBestVar(vi)
        }
        return anyChange
    }

    private fun maybeUpdateBestVar(vi: VarImps) {
        if (best == null || vi.score > best!!.score) {
            best = vi
        }
    }

    private fun addXor(xor: Xor): Boolean {
        for (xorChild in xor.args) {
            xorChild.vr.xorParent = xor
        }
        return xors.add(xor)
    }

    //after LitImps is fully populated
    private fun scoreXor(xor: Xor): Int {
        return xor.args.sumBy { vi(it.vr)?.score ?: 0 }
    }

    fun getBestXor(): Xor? {
        if (xors.isEmpty()) return null
        if (bestXor == null) {
            this.bestXor = computeBestXor()
        }
        return bestXor
    }

    private fun computeBestXor(): Xor {
        assert(xors.isNotEmpty())
        var best: Xor? = null
        var bestScore: Int = -1
        for (xor in xors) {
            val score = scoreXor(xor)
            if (score > bestScore) {
                best = xor
                bestScore = score
            }
        }
        return best!!
    }

    fun vi(vr: Var): VarImps? {
        return litImpMap[vr]
    }

    @Throws(ImpliedLitException::class)
    fun addVvOr(vv: Or): Boolean {
        val a1 = vv._args[0]
        val a2 = vv._args[1]


        check(a1 is Lit)
        check(a2 is Lit)

        val ch1 = addImp(a1.flp, a2)
        val ch2 = addImp(a2.flp, a1)

        return ch1 || ch2
    }

    fun getBestVarImps(): VarImps {
        return best!!
    }

//    private fun bestVarImps2(): VarImps {
//        var best: VarImps? = null
//        for (vi in map.values) {
//            if (best == null || vi.score > best.score) {
//                best = vi
//            }
//        }
//        return best!!
//    }

    fun isEmpty(): Boolean {
        return litImpMap.isEmpty()
    }

    fun getBestVar(): Var {
        return getBestVarImps().vr
    }

//    fun bestXor(): Xor? {
//        XorCounts.
//        if (bestXor == null) {
//            bestXor = computeBestXor()
//        }
//        return bestXor
//
//    }

    fun checkXors() {
        for (vi in litImpMap.values) {
            val vr = vi.vr
            if (vr.isXorChild) {
                val xorParent = vr.xorParent
                assert(xors.contains(xorParent)) {
                    println("xors:      $xors")
                    println("xorParent: $xorParent")
                }
            }
        }
    }

    companion object {

        @Throws(ImpliedLitException::class)
        fun fromFormula(formula: Formula): Kb = Kb(formula.space, formula._args)

        @Throws(ImpliedLitException::class)
        fun computeBest(formula: Formula): VarImps? = fromFormula(formula).getBestVarImps()

        @Throws(ImpliedLitException::class)
        fun computeBestVar(formula: Formula): Var? = computeBest(formula)?.vr
    }

    val varImpsList: List<VarImps> get() = litImpMap.values.sortedByDescending { it.score }


}