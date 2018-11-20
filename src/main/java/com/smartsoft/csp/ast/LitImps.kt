package com.smartsoft.csp.ast

import com.smartsoft.csp.ast.formula.KFormula
import com.smartsoft.csp.fm.dnnf.products.Cube
import com.smartsoft.csp.ssutil.Strings.indent
import java.util.*


fun Exp.litImpSimple(li: LitImps) {
    if (isLitImp) {
        when (this) {
            is Or -> litImpSimple(li)
            is Iff -> litImpSimple(li)
            is Imp -> toOr.litImpSimple(li)
            is Rmp -> toOr.litImpSimple(li)
            is Nand -> toOr.litImpSimple(li)
        }
    }
}

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

//
//private fun Or.litImpSimple(li: LitImps) {
//    if (isPair) {
//        val a1 = if (arg1.isNotClause) {
////            println("notClause: $arg1")
////            println("notClauseToCube: ${arg1.notClauseToCube}")
//            arg1.notClauseToCube
//        } else {
//            arg1
//        }
//        val a2 = if (arg2.isNotClause) {
////            println("notClause: $arg2")
////            println("notClauseToCube: ${arg2.notClauseToCube}")
//            arg2.notClauseToCube
//        } else {
//            arg2
//        }
//        when {
//            a1 is Lit -> when {
//                a2 is Lit -> {
//                    li.imp(a1.flipLit, a2)
//                    li.imp(a2.flipLit, a1)
//                }
//                a2 is Cube -> {
////                    println(33333333)
//                    li.imp(a1.flipLit, a2)
//                }
////                a2.isClause -> {println(111111);a2.args.forEach { li.imp(it.flip.asLit, a1) }}
//            }
//            a2 is Lit -> when {
//                a1 is Cube -> {
////                    println(44444444)
//                    li.imp(a2.flipLit, a1)
//                }
////                a1.isClause ->  {println(111111);a1.args.forEach { li.imp(it.flip.asLit, a2) }}
//            }
//        }
//    }
//}

//
//fun Iff.litImpSimple(li: LitImps) {
//    val a1 = if (arg1.isNotClause) arg1.notClauseToCube else if (arg1.isNotCube) arg1.notCubeToClause else arg1
//    val a2 = if (arg2.isNotClause) arg2.notClauseToCube else if (arg2.isNotCube) arg2.notCubeToClause else arg2
//    when {
//        a1 is Lit -> when {
//            a2 is Lit -> {
//                li.imp(a1, a2)
//                li.imp(a2.flipLit, a1.flipLit)
//            }
//            a2 is Cube -> li.imp(a1, a2)
//            a2.isClause -> a2.args.forEach { li.imp(it.flip.asLit, a1.flipLit) }
//        }
//        a2 is Lit -> when {
//            a1 is Cube -> li.imp(a2, a1)
//            a1.isClause -> a1.args.forEach { li.imp(it.flip.asLit, a2.flipLit) }
//        }
//    }
//}


class Assignments {

    private var map: MutableMap<Var, Boolean>? = null

    fun assign(vr: Var, newValue: Boolean) {
        if (map == null) map = mutableMapOf()
        val oldValue = map!!.put(vr, newValue)
        when (oldValue) {
            null -> {
                //new entry
            }
            newValue -> {
                //dup entry
            }
            else -> throw ConflictingAssignmentException()
        }
    }

    fun assignAll(cube: Cube) {
        for (lit in cube) {
            assign(lit.vr, lit.sign())
        }
    }

    val size: Int get() = map?.size ?: 0

    override fun toString(): String {
        if (map == null) return ""
        return map!!.entries.joinToString(" ") { "${if (it.value) "" else "!"}${it.key}" }
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

    private var pImps: Assignments = Assignments()
    private var nImps: Assignments = Assignments()

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


    @Throws(ImpliedLitException::class)
    fun addPImp(lit: Lit) {
        try {
            pImps.assign(lit.vr, lit.sign())
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.nLit())
        }
    }

    @Throws(ImpliedLitException::class)
    fun addNImp(lit: Lit) {
        try {
            nImps.assign(lit.vr, lit.sign())
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.pLit())
        }
    }

    override fun toString(): String {
        return "$vr pImps: $pImps nImps:$nImps"
    }

    //not xors and not vvs
    fun addOtherComplex(cc: Exp) {
        assert(!cc.isVv)
        assert(!cc.isXor)
        when {
            cc.varCount == 3 -> complex3Var.add(cc)
            cc.varCount == 4 -> complex4Var.add(cc)
            else -> complexNVar.add(cc)
        }
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
}


class LitImps {

    private val map: MutableMap<Var, VarImps> = mutableMapOf()

    private val xors: MutableList<Xor> = mutableListOf<Xor>()

    private var best: VarImps? = null
    private var bestXor: Xor? = null

    @Throws(ImpliedLitException::class)
    fun addComplex(cc: Exp): LitImps {
        if (cc is Or && cc.isVv) {
            addVv(cc)
        } else if (cc is Or && cc.isPair) {
            val a1: Exp = cc._args[0]
            val a2: Exp = cc._args[1]
            if (a1 is Lit && a2 is Cube) {
                addConstraintOrVvs(a1, a2)
            } else if (a1 is Cube && a2 is Lit) {
                addConstraintOrVvs(a2, a1)
                for (lit1 in a1) {
                    addImp(lit1.flipLit, a2)
                    addImp(lit1, a2.flipLit)
                }
            } else if (a1 is Lit && a2 is Not && a2.pos.isClause) {
                for (lit2 in a2.pos._args) {
                    addImp(a1.flipLit, lit2.asLit.flipLit)
                    addImp(a1, lit2.asLit)
                }

            } else if (a1 is Not && a1.pos.isClause && a2 is Lit) {
                addConstraintOrVvs(a2, a1)
            }
        } else if (cc is Xor) {
            addXor(cc.asXor)
        } else {
            //not xors and not vvs
            addOtherComplex(cc)
        }

        return this
    }


    //lit imp
    private fun addConstraintOrVvs(lit1: Lit, cube: Cube) {
        for (lit2: Lit in cube) {
            addConstraintOrVv(lit1, lit2)
        }
    }

    private fun addConstraintOrVvs(lit1: Lit, notClause: Not) {
        if (notClause.pos !is Or) throw IllegalArgumentException()
        for (lit2: Exp in notClause.pos._args) {
            if (lit2 !is Lit) throw IllegalArgumentException()
            addConstraintOrVv(lit1, lit2.flipLit)
        }
    }

    private fun addConstraintOrVv(lit1: Lit, lit2: Lit) {
        addImp(lit1.flipLit, lit2)
        addImp(lit1, lit2.flipLit)
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
    fun addImp(lit1: Lit, lit2: Lit): LitImps {
        val vi = mkVarImps(lit1.vr)
        if (lit1.isPos) {
            vi.addPImp(lit2)
        } else {
            vi.addNImp(lit2)
        }

        maybeUpdateBestVar(vi)

        return this
    }

    //not xors and not vvs
    private fun addOtherComplex(complex: Exp) {
        val vars = complex.vars
        vars.forEach {
            val vi = mkVarImps(it)
            vi.addOtherComplex(complex)
            maybeUpdateBestVar(vi)
        }
    }

    private fun maybeUpdateBestVar(vi: VarImps) {
        if (best == null || vi.score > best!!.score) {
            best = vi
        }
    }

    private fun addXor(xor: Xor) {
        for (xorChild in xor.args) {
            xorChild.vr.xorParent = xor
        }
        xors.add(xor)
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
    fun addVv(vv: Or) {

        val a1 = vv._args[0]
        val a2 = vv._args[1]

        if (a1 !is Lit) throw IllegalArgumentException()
        if (a2 !is Lit) throw IllegalArgumentException()

        addImp(a1.flipLit, a2)
        addImp(a2.flipLit, a1)
    }

    fun bestVarImps(): VarImps {
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
        return bestVarImps().vr
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
        fun fromFormula(formula: KFormula): LitImps = LitImps().addAll(formula._args)

        @Throws(ImpliedLitException::class)
        fun computeBest(formula: KFormula): VarImps? = fromFormula(formula).bestVarImps()

        @Throws(ImpliedLitException::class)
        fun computeBestVar(formula: KFormula): Var? = computeBest(formula)?.vr
    }

    val varImpsList: List<VarImps> get() = map.values.sortedByDescending { it.score }


}