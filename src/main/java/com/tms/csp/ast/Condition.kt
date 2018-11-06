package com.tms.csp.ast

import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.transforms.Transformer
import com.tms.csp.util.DynComplex
import com.tms.csp.util.XorCube

enum class ConditionType {
    Lit, Cube, XorCube, Identity, Transformer
}

class Condition(val on: Any) {

    init {
        type
    }

    val isIdentity: Boolean get() = type == ConditionType.Identity
    val isXorCube: Boolean get() = type == ConditionType.XorCube
    val isCube: Boolean get() = type == ConditionType.Cube
    val isLit: Boolean get() = type == ConditionType.Lit
    val isTransformer: Boolean get() = type == ConditionType.Transformer

    val isExp: Boolean get() = isXorCube || isCube || isLit


    val asXorCube: XorCube get() = on as XorCube
    val asCube: Cube get() = on as Cube
    val asLit: Lit get() = on as Lit
    val asTransformer: Transformer get() = on as Transformer
    val asExp: Exp get() = on as Exp

    fun condition(constraint: Exp, log: Boolean = false, depth: Int = 0): Exp {

        val conditioned: Exp = when {
            isIdentity -> constraint
            isXorCube -> constraint.condition(asXorCube)
            isCube -> constraint.condition(asCube)
            isLit -> constraint.condition(asLit)
            isTransformer -> constraint.transform(asTransformer)
            else -> throw IllegalStateException()
        }


        if (conditioned != constraint && log) {
            println("$depth Conditioned: $constraint")
            println("     c: $on")
            println("     to: $conditioned")
        }

        return conditioned


    }

    override fun toString(): String {
        return when {
            isIdentity -> "ConditionIdentity"
            isLit -> "ConditionLit[$asLit]"
            isCube -> "ConditionCube[$asCube]"
            isXorCube -> "ConditionXorCube[$asXorCube]"
            isTransformer -> "ConditionTransformer[$asTransformer]"
            else -> throw IllegalStateException()
        }
    }

    val type: ConditionType
        get() = when (on) {
            ConditionType.Identity -> ConditionType.Identity
            is Lit -> ConditionType.Lit
            is Cube -> ConditionType.Cube
            is XorCube -> ConditionType.XorCube
            is Transformer -> ConditionType.Transformer
            else -> throw IllegalStateException()
        }

    companion object {
        @JvmStatic
        fun fromCube(cube: Cube): Condition = Condition(cube)

        @JvmStatic
        fun fromLit(lit: Lit): Condition = Condition(lit)

        val identity: Condition get() = Condition(on = ConditionType.Identity)
    }

    fun anyVarOverlap(exp: Exp): Boolean {
        return when (on) {
            is Exp -> on.anyVarOverlap(exp)
            else -> throw IllegalStateException()
        }
    }

    fun anyVarOverlap(complex: DynComplex): Boolean {
        val vars = complex.vars()
        return when (on) {
            is Exp -> on.anyVarOverlap(vars)
            else -> throw IllegalStateException()
        }
    }

    fun assignSafe(csp: Csp) {
        when (type) {
            ConditionType.Lit -> csp._assignSafe(asLit)
            ConditionType.Cube -> csp._assignSafe(asCube)
            ConditionType.XorCube -> csp._assignSafe(asCube)
            ConditionType.Transformer -> throw IllegalStateException()
            ConditionType.Identity -> Unit
        }
    }

    fun assign(csp: Csp) {
        when (type) {
            ConditionType.Lit -> csp.assign(asLit)
            ConditionType.Cube -> csp.assignAll(asCube)
            ConditionType.XorCube -> csp.assignAll(asCube)
            ConditionType.Transformer -> throw IllegalStateException()
            ConditionType.Identity -> Unit
        }
    }


}