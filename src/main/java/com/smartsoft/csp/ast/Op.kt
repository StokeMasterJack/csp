package com.smartsoft.csp.ast


enum class Op0 {
    Const, Lit, Complex;

    val isConst: Boolean get() = this == Const
    val isLit: Boolean get() = this == Lit
    val isComplex: Boolean get() = this == Complex
}

enum class Op1(@Suppress("UNUSED_PARAMETER") parent: Op0) {
    And(Op0.Complex), Or(Op0.Complex), Xor(Op0.Complex);

    val isAnd: Boolean get() = this == And
    val isOr: Boolean get() = this == Or
    val isXor: Boolean get() = this == Xor;

}


enum class Op {

    Constant,
    Lit,

    //and-like
    And,
    Formula, //all complex
    Cube, //lit lit
    Kb, //mixed
    DAnd, //dnnf
    Fcc,

    Or,
    DOr,
    Xor,

    Iff, Imp, Rmp, Nand,
    Not;

    val isLit: Boolean
        get() = this == Lit

    val isAnd: Boolean
        get() = this == And

    val isDAnd: Boolean
        get() = this == DAnd

    val isFormula: Boolean
        get() = this == Formula

    val isFcc: Boolean
        get() = this == Fcc

    val isCube: Boolean
        get() = this == Cube

    val isKb: Boolean
        get() = this == Kb

    val isXor: Boolean
        get() = this == Xor

    val isAndLike: Boolean
        get() = isAnd || isFormula || isDAnd || isCube || isKb || isFcc

    val isOrLike: Boolean
        get() = isOr || isDOr

    val isOr: Boolean get() = this == Or
    val isImp: Boolean get() = this == Imp
    val isRmp: Boolean get() = this == Rmp

    val isNot: Boolean
        get() = this == Not

    val isNand: Boolean
        get() = this == Nand

    val isDOr: Boolean
        get() = this == DOr


    val isPosComplex: Boolean
        get() = isAndLike || isOrLike || isXor

    val op1: Op1
        get() = when {
            isAndLike -> Op1.And
            isOrLike -> Op1.Or
            isXor -> Op1.Xor
            else -> throw IllegalStateException()
        }
}
