package com.tms.csp.ast

import com.tms.csp.fm.dnnf.products.Cube
import java.util.*

interface LitImps {

    fun imp(lit1: Lit, lit2: Lit)
    fun imp(lit: Lit, cube: Cube)

}

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


private fun Or.litImpSimple(li: LitImps) {
    if (isPair) {
        val a1 = if (arg1.isNotClause) {
//            println("notClause: $arg1")
//            println("notClauseToCube: ${arg1.notClauseToCube}")
            arg1.notClauseToCube
        } else {
            arg1
        }
        val a2 = if (arg2.isNotClause) {
//            println("notClause: $arg2")
//            println("notClauseToCube: ${arg2.notClauseToCube}")
            arg2.notClauseToCube
        } else {
            arg2
        }
        when {
            a1 is Lit -> when {
                a2 is Lit -> {
                    li.imp(a1.flipLit, a2)
                    li.imp(a2.flipLit, a1)
                }
                a2 is Cube -> {
//                    println(33333333)
                    li.imp(a1.flipLit, a2)
                }
//                a2.isClause -> {println(111111);a2.args.forEach { li.imp(it.flip.asLit, a1) }}
            }
            a2 is Lit -> when {
                a1 is Cube -> {
//                    println(44444444)
                    li.imp(a2.flipLit, a1)
                }
//                a1.isClause ->  {println(111111);a1.args.forEach { li.imp(it.flip.asLit, a2) }}
            }
        }
    }
}


fun Iff.litImpSimple(li: LitImps) {
    val a1 = if (arg1.isNotClause) arg1.notClauseToCube else if (arg1.isNotCube) arg1.notCubeToClause else arg1
    val a2 = if (arg2.isNotClause) arg2.notClauseToCube else if (arg2.isNotCube) arg2.notCubeToClause else arg2
    when {
        a1 is Lit -> when {
            a2 is Lit -> {
                li.imp(a1, a2)
                li.imp(a2.flipLit, a1.flipLit)
            }
            a2 is Cube -> li.imp(a1, a2)
            a2.isClause -> a2.args.forEach { li.imp(it.flip.asLit, a1.flipLit) }
        }
        a2 is Lit -> when {
            a1 is Cube -> li.imp(a2, a1)
            a1.isClause -> a1.args.forEach { li.imp(it.flip.asLit, a2.flipLit) }
        }
    }
}


/**
 * Just logging for now
 */
class LitImpHandler : LitImps {

    override fun imp(lit1: Lit, lit2: Lit) {
        if (Space.config.log.litImps) {
            println("lit-implies-lit: $lit1 implies $lit2")
        }
    }

    override fun imp(lit: Lit, cube: Cube) {
        if (Space.config.log.litImps) {
            println("lit-implies-cube: $lit implies $cube")
        }
    }


}

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

    private var pImps: Assignments? = null
    private var nImps: Assignments? = null

    val pImpCount: Int get() = pImps?.size ?: 0
    val nImpCount: Int get() = nImps?.size ?: 0

    val impCount: Int get() = pImpCount + nImpCount

    fun addPImp(lit: Lit) {
        if (pImps == null) pImps = Assignments()
        try {
            pImps!!.assign(lit.vr, lit.sign())
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.nLit())
        }
    }

    fun addNImp(lit: Lit) {
        if (nImps == null) nImps = Assignments()
        try {
            nImps!!.assign(lit.vr, lit.sign())
        } catch (e: ConflictingAssignmentException) {
            //vr.nLit() causes formula to fail
            //thus, vr.pLit is implied
            throw ImpliedLitException(vr.pLit())
//            throw RuntimeException("${vr.pLit()} is implied")
        }
    }

    fun addPImps(cube: Cube) {
        if (pImps == null) pImps = Assignments()
        try {
            pImps!!.assignAll(cube)
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.nLit())
        }
    }

    fun addNImps(cube: Cube) {
        if (nImps == null) nImps = Assignments()
        try {
            nImps!!.assignAll(cube)
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.pLit())
        }
    }

    override fun toString(): String {
        return "$vr pImps: $pImps nImps:$nImps"
    }

//    fun toVvs():List<Exp>{
//        if(pImps!= null){
//
//
//        }
//    }
}

class ActualLitImps(complexConstraints: Array<Exp>) : LitImps {

    private val map: MutableMap<Var, VarImps> = mutableMapOf()

    private val space: Space = complexConstraints[0].space

    init {
        complexConstraints.forEach {
            it.litImpSimple(this)
        }
    }

    override fun imp(lit1: Lit, lit2: Lit) {
        var varImps = map[lit1.vr]
        if (varImps == null) {
            varImps = VarImps(lit1.vr)
            map[lit1.vr] = varImps
        }
        if (lit1.isPos) {
            varImps.addPImp(lit2)
        } else {
            varImps.addNImp(lit2)
        }
    }

    override fun imp(lit: Lit, cube: Cube) {
        var varImps = map[lit.vr]
        if (varImps == null) {
            varImps = VarImps(lit.vr)
            map[lit.vr] = varImps
        }
        if (lit.isPos) {
            varImps.addPImps(cube)
        } else {
            varImps.addNImps(cube)
        }
    }

    fun best(): Var? {
        var best: MutableMap.MutableEntry<Var, VarImps>? = null
        for (entry in map.entries) {
            if (best == null || entry.value.impCount > best.value.impCount) {
                best = entry
            }
        }
        return best?.key
    }

    fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    val varImps: List<VarImps> get() = map.values.sortedByDescending { it.impCount }

//    val toVVs:List<Exp> get(){
//
//        for (entry in map.entries) {
//            val vr: Var = entry.key
//            val imps: VarImps = entry.value
//
//        }
//
//        space.expFactory.mkLitPair()
//    }

}