package com.tms.csp.ast

import com.tms.csp.ast.formula.Formula
import com.tms.csp.util.DynComplex

enum class Type {
    Formula, ExpSeq, StrSeq, ExpIt, DynComplex, Clob, NoAction
}

enum class SeqType {
    Exp, Str
}

typealias ConstraintFilter = (Exp) -> Boolean

fun guessType(c: Any): Type {
    return when (c) {
        is Type -> {
            assert(c == Type.NoAction)
            Type.NoAction
        }
        is Formula -> Type.Formula
        is DynComplex -> Type.DynComplex
        is Sequence<*> -> Type.ExpSeq
        is Iterable<*> -> Type.ExpIt
        is String -> Type.Clob
        else -> throw IllegalStateException(c.toString())
    }
}

class Constraints(val c: Any, val type: Type = guessType(c)) {


    companion object {

        fun computeType(on: Any?, seqType: SeqType?): Type {
            return when (on) {
                null -> Type.NoAction
                is DynComplex -> {
                    assert(seqType == null);
                    Type.DynComplex
                }
                is String -> {
                    assert(seqType == null);
                    Type.Clob
                }
                is Sequence<*> -> {
                    assert(seqType != null);
                    when (seqType) {
                        SeqType.Exp -> Type.ExpSeq
                        SeqType.Str -> Type.StrSeq
                        else -> throw IllegalStateException()
                    }
                }
                else -> {
                    throw IllegalStateException()
                }
            }

        }

        @JvmStatic
        fun mkDynComplex(c: DynComplex) = Constraints(c, Type.DynComplex)


        @JvmStatic
        fun mkExpSeq(c: Sequence<Exp>) = Constraints(c, Type.ExpSeq)


        @JvmStatic
        fun mkExpSeq(c: Iterable<Exp>) = Constraints(c.asSequence(), Type.ExpSeq)

        @JvmStatic
        fun mkExpIt(c: Iterable<Exp>) = Constraints(c, Type.ExpIt)


        @JvmStatic
        fun mkExpIt(formula: Formula) = Constraints(formula.argIt(), Type.ExpIt)


        @JvmStatic
        fun mkExpIt(and: And) = Constraints(and.argIt(), Type.ExpIt)


        @JvmStatic
        fun mkStrSeq(seq: Sequence<String?>) = Constraints(seq, Type.StrSeq)

        @JvmStatic
        fun mkClob(clob: String) = Constraints(clob, Type.Clob)

        @JvmStatic
        val noAction
            get() = Constraints(c = Type.NoAction, type = Type.NoAction)
    }
//
//    constructor(lit: Lit) : this(lit as Any)
//    constructor(cube: Cube) : this(cube as Any)

    val isFormula: Boolean get() = c is Formula;
    val isDynComplex: Boolean get() = c is DynComplex;
    val isExpSeq: Boolean get() = c is Sequence<*> && type == Type.ExpSeq;
    val isStrSeq: Boolean get() = c is Sequence<*> && type == Type.StrSeq
    val isClob: Boolean get() = c is String && type == Type.Clob
    val isExpIt: Boolean get() = c is Iterable<*> && type == Type.ExpIt
    val isNoAction: Boolean get() = c is Type && type == Type.NoAction

    val asDynComplex: DynComplex
        get() {
            assert(isDynComplex)
            return c as DynComplex
        }

    val asFormula: Formula
        get() {
            assert(isFormula)
            return c as Formula
        }

    val asExpSeq: Sequence<Exp>
        get() {
            assert(isExpSeq)
            return c as Sequence<Exp>
        }

    val asStrSeq: Sequence<String?>
        get() {
            assert(isStrSeq)
            return c as Sequence<String?>
        }

    val asExpIt: Iterable<Exp>
        get() {
            assert(isExpIt)
            return c as Iterable<Exp>
        }


    val asClob: String
        get() {
            assert(isClob)
            return c as String
        }


//    val type: Type get() = Complex.computeType(c, seqType);

    override fun toString(): String = type.toString();


}


