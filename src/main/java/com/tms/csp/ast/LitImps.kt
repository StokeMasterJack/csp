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


private val Exp.isLitImp: Boolean
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
        val a1 = if (arg1.isNotClause) arg1.notClauseToCube else if (arg1.isNotCube) arg1.notCubeToClause else arg1
        val a2 = if (arg2.isNotClause) arg2.notClauseToCube else if (arg2.isNotCube) arg2.notCubeToClause else arg2
        when {
            a1 is Lit -> when {
                a2 is Lit -> {
                    li.imp(a1.flipLit, a2)
                    li.imp(a2.flipLit, a1)
                }
                a2 is Cube -> li.imp(a1.flipLit, a2)
                a2.isClause -> a2.args.forEach { li.imp(it.flip.asLit, a1) }
            }
            a2 is Lit -> when {
                a1 is Cube -> li.imp(a2.flipLit, a1)
                a1.isClause -> a1.args.forEach { li.imp(it.flip.asLit, a2) }
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