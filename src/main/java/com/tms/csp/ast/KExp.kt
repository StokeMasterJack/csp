package com.tms.csp.ast

import com.tms.csp.fm.dnnf.products.Cube

class KExp(val e: Exp) {


    fun And.condition(lit: Lit): Exp {
                return if (!anyVarOverlap(lit)) {
                        this
        } else {
                        val b = argBuilder(op()).addExpArray(args, lit)
            b.mk()
        }
    }

    fun And.condition(ctx: Cube): Exp {
                return if (!anyVarOverlap(ctx)) {
                        this
        } else {
                        val b = argBuilder(op()).addExpArray(args, ctx)
            b.mk()
        }
    }

    fun And.toDnnf(): Exp {
                return when {
            size == 0 -> {
                throw IllegalStateException("PosComplex disallows empty")
            }
            size == 1 -> {
                                val arg = getArg()
                arg.toDnnf()
            }
            isAllLits -> {
                                assert(size > 1)
                space.mkCube(argIt())
            }
            isAllComplex -> {
                assert(size > 1)
                                val f = space.mkFormula(argIt())
                f.toDnnf()
            }
            isAllConstants -> {
                throw UnsupportedOperationException("all constants: " + javaClass.getName())
            }
            else -> {
                                assert(!isOrContainsConstants())
                assert(!isOrContainsConstant())

                val csp = Add.mixed(this).mkCsp()
                csp.toDnnf()
                //            throw new UnsupportedOperationException(getClass().getName() + ": " + getContentModel() + ":" + toString());
            }
        }


    }


    companion object {
        @JvmStatic
        val selector: (Exp) -> Int = { it.expId }


        @JvmStatic
        fun Exp.conditionThat(that: Exp): Exp {
            return when (this) {
                is Lit -> that.condition(this)
                is Cube -> that.condition(this)
                else -> throw IllegalStateException()
            }

        }
    }

}


val Exp.size get() = argCount;

val Exp.argSeq
    get() = when (this) {
        is Constant -> emptySequence<Exp>()
        is Lit -> emptySequence<Exp>()
        is Not -> kotlin.sequences.sequenceOf(arg)
        is PosComplexMultiVar -> args.asSequence()
        is DcOr -> sequenceOf(vr.pLit(), vr.nLit())
        is LitAndFalse -> sequenceOf(lit, mkFalse())
        else -> throw IllegalStateException()
    }


fun Exp.collectVarCodes(s: MutableSet<String>) {
    when (this) {
        is Lit -> s.add(this.varCode)
        is Not -> this.arg.collectVarCodes(s)
        is PosComplex -> this.args.forEach { it.collectVarCodes(s) }
        is Constant -> Unit
        else -> throw  IllegalStateException()
    }
}


//fun And.condition(): ArgBuilder {
//    val b = argBuilder(op())
//            .addExpArray(args, condition = condition)
//    return b.mk();
//}

//fun DynCube.litIterator() Iterator<Lit> {
//    return LitIterator(varIterator(), this)
//}
//
//


