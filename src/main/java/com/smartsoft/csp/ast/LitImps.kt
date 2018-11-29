package com.smartsoft.csp.ast

import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.ssutil.Strings.indent
import com.smartsoft.csp.util.Bit
import java.util.*


private val ops: EnumSet<Op> = EnumSet.of(Op.Or, Op.Nand, Op.Imp, Op.Rmp, Op.Iff)

private enum class Arg {
    Lit, Cube, NotClause, None;
}

private fun argType(a: Exp): Arg {
    return if (a is Lit) Arg.Lit
    else if (a is CubeExp) Arg.Cube
    else if (a is Not && a.pos is Or && a.pos.isAllLits) Arg.NotClause
    else Arg.None
}


val Exp.isLitImp: Boolean
    get() = if (this is PosComplexMultiVar && this._args.size == 2 && this.op in ops) {


        val a1: Arg = argType(this._args[0])
        if (a1 == Arg.None) {
            false
        } else {
            val a2: Arg = argType(this._args[1])
            if (a1 == Arg.Lit) {
                a2 == Arg.Lit || a2 == Arg.Cube || a2 == Arg.NotClause
            } else {
                assert(a1 == Arg.Cube || a1 == Arg.NotClause)
                a2 == Arg.Lit
            }
        }


    } else {
        false
    }


class Assignments(decisionLit: Lit) {

    private var _space: Space? = null

    private var map: MutableMap<Var, Boolean>? = null

    val space: Space get() = _space!!

    init {
        assign(decisionLit);
    }

    fun eq(o: Assignments): Boolean {
        return Objects.equals(map, o.map)
    }

    override fun equals(other: Any?): Boolean {
        return eq(other!! as Assignments)
    }

    @Throws(ConflictingAssignmentException::class)
    fun assign(lit: Lit): Boolean {
        return assign(lit.vr, lit.sign())
    }

    @Throws(ConflictingAssignmentException::class)
    fun assign(vr: Var, newValue: Boolean): Boolean {
        if (_space == null) _space = vr.space
        if (map == null) map = mutableMapOf()
        val oldValue = map!!.put(vr, newValue)
        return when (oldValue) {
            null -> true //new entry
            newValue -> false //dup
            !newValue -> throw ConflictingAssignmentException()
            else -> throw IllegalStateException()
        }
    }

    val isEmpty: Boolean get() = map == null || map!!.isEmpty()

    fun assignAll(cube: Cube): Boolean {
        var anyChange = false
        for (lit in cube) {
            val ch = assign(lit.vr, lit.sign())
            if (ch) anyChange = true
        }
        return anyChange
    }

    fun assignToDynCube(cc: DynCube) {
        if (map == null) return
        for (entry in map!!.entries) {
            val vr: Var = entry.key
            val sign: Boolean = entry.value
            cc.assign(vr, sign)
        }
    }

    operator fun get(vr: Var): Bit {
        return if (map == null) Bit.OPEN
        else {
            val b: Boolean? = map!![vr]
            when (b) {
                null -> Bit.OPEN
                true -> Bit.TRUE
                false -> Bit.FALSE
            }
        }
    }

    fun contains(lit: Lit): Boolean {
        val bit = this[lit.vr]
        return lit.eqSign(bit);
    }

    val size: Int get() = map?.size ?: 0

    override fun toString(): String {
        if (map == null) return ""
        return map!!.entries.joinToString(" ") { "${if (it.value) "" else "!"}${it.key}" }
    }

    fun toCube(decisionLit: Lit): DynCube {
        if (isEmpty) {
            throw IllegalStateException()
        }
        val d = DynCube(space)
        assignToDynCube(d)
        assert(contains(decisionLit))
        return d
    }


//    fun toVvs(lit:Lit):List<Exp>{
//        if(map==null) return emptyList()
//        else{
//            map.values.forEach()
//        }
//    }
}

class ImpliedLitException(val lit: Lit) : RuntimeException() {

}

class VarImps(val vr: Var) {

    private var pImps: Assignments = Assignments(vr.pLit())
    private var nImps: Assignments = Assignments(vr.nLit())

    val pImpCount: Int get() = pImps.size
    val nImpCount: Int get() = nImps.size

    val impCount: Int get() = pImpCount + nImpCount

    val complex3Var: MutableList<Exp> = mutableListOf()  //not xors and not vvs
    val complex4Var: MutableList<Exp> = mutableListOf()  //not xors and not vvs
    val complexNVar: MutableList<Exp> = mutableListOf()  //not xors and not vvs

    val score: Int
        get() = Math.min(pImpCount, nImpCount) * 1000 +
                impCount * 1000 +
                complex3Var.size * 500 +
                complex4Var.size * 300 +
                complexNVar.size * 100 +
                vr.vrId

    fun eqImps(o: VarImps): Boolean {
        val e1 = pImps.eq(o.pImps)
        val e2 = nImps.eq(o.nImps)
        return e1 && e2
    }

    override fun equals(other: Any?): Boolean {
        return eqImps(other!! as VarImps)
    }

    override fun hashCode(): Int {
        var result = vr.hashCode()
        result = 31 * result + pImps.hashCode()
        result = 31 * result + nImps.hashCode()
        return result
    }


    @Throws(ImpliedLitException::class)
    fun addPImp(lit: Lit): Boolean {
        return try {
            pImps.assign(lit)
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.nLit())
        }


    }

    @Throws(ImpliedLitException::class)
    fun addNImp(lit: Lit): Boolean {
        return try {
            nImps.assign(lit)
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.pLit())
        }


    }

    override fun toString(): String {
        return "$vr pImps: $pImps nImps:$nImps"
    }

    //not xors and not vvs
    fun addOtherComplex(cc: Exp): Boolean {
        assert(!cc.isVv)
        assert(!cc.isXor)
        when {
            cc.varCount == 3 -> complex3Var.add(cc)
            cc.varCount == 4 -> complex4Var.add(cc)
            else -> complexNVar.add(cc)
        }

        return true
    }

//    fun toVvs():List<Exp>{
//        if(pImps!= null){
//
//
//        }
//    }

    fun print(depth: Int) {
        println("${indent(depth)}vr: $vr")
        println("${indent(depth)}  pImps:${pImpCount}:${pImps}")
        println("${indent(depth)}  nImps:${nImpCount}:${nImps}")
        println("${indent(depth)}  complex3var:${complex3Var.size}")
        println("${indent(depth)}  complex4var:${complex4Var.size}")
        println("${indent(depth)}  complexNvar:${complexNVar.size}")
        println("${indent(depth)}  score:${score}")
        println()
    }

    fun imps(sign: Boolean): Assignments? {
        if (sign) return pImps
        return nImps
    }

    fun hasImps(sign: Boolean): Boolean = if (sign) pImpCount > 0 else nImpCount > 0


    fun impsCube(sign: Boolean): ConditionOn {
        return if (sign) {
            pImps.toCube(vr.pLit())
        } else {
            nImps.toCube(vr.nLit())
        }
    }

}


class LitImps() {

    private val map: MutableMap<Var, VarImps> = mutableMapOf()


    private val xors: MutableList<Xor> = mutableListOf<Xor>()

    private var best: VarImps? = null
    private var bestXor: Xor? = null

    constructor(complex: Array<Exp>) : this() {
        addAll(complex)
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
        println(map)
    }


    override fun equals(other: Any?): Boolean {
        return eq(other!! as LitImps)
    }

    private fun eq(o: LitImps): Boolean {
        return Objects.equals(map, o.map)
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

    @Throws(ImpliedLitException::class)
    fun addAll(complex: Array<Exp>): LitImps {
        complex.forEach {
            addComplex(it)
        }
        return this
    }

    private fun mkVarImps(vr: Var): VarImps {
        var varImps = map[vr]
        if (varImps == null) {
            varImps = VarImps(vr)
            map[vr] = varImps
        }
        return varImps
    }


    private fun mkXorCounts(vr: Var): VarImps {
        var varImps = map[vr]
        if (varImps == null) {
            varImps = VarImps(vr)
            map[vr] = varImps
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
        return map[vr]
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
        return map.isEmpty()
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
        for (vi in map.values) {
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
        fun fromFormula(formula: Formula): LitImps = LitImps(formula._args)

        @Throws(ImpliedLitException::class)
        fun computeBest(formula: Formula): VarImps? = fromFormula(formula).getBestVarImps()

        @Throws(ImpliedLitException::class)
        fun computeBestVar(formula: Formula): Var? = computeBest(formula)?.vr
    }

    val varImpsList: List<VarImps> get() = map.values.sortedByDescending { it.score }


}