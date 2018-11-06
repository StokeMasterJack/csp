package com.tms.csp.ast

import com.tms.csp.ast.formula.Formula
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.DynComplex
import com.tms.csp.util.XorCube

data class Add(
        val space: Space,
        val constraints: Constraints = Constraints.noAction,
        val condition: Condition = Condition.identity
) {

    //simple var split
    constructor(c: Formula, cc: Lit) : this(cc.space, Constraints(c), Condition(cc))

    //xor split
    constructor(c: Formula, cc: XorCube) : this(cc.space, Constraints(c), Condition(cc))

    constructor(c: DynComplex, cc: Lit) : this(cc.space, Constraints(c), Condition(cc))
    constructor(c: DynComplex, cc: Cube) : this(cc.space, Constraints(c), Condition(cc))

    constructor(c: String, space: Space) : this(space, Constraints(c))

    constructor(c: Sequence<Exp>, cc: Lit) : this(cc.space, Constraints(c), Condition(cc))
    constructor(c: Sequence<Exp>, cc: Cube) : this(cc.space, Constraints(c), Condition(cc))
    constructor(c: Sequence<Exp>, space: Space) : this(space, Constraints(c), Condition.identity)
    constructor(c: Iterable<Exp>, cc: Lit) : this(cc.space, Constraints(c), Condition(cc))


    fun toExpSeq(): Sequence<Exp> {

        val parser = space.parser

        val seq: Sequence<Exp> = when {
            constraints.isNoAction -> {
                emptySequence<Exp>()
            }
            constraints.isClob -> {
                parser.parsePL(constraints.asClob)
            }
            constraints.isDynComplex -> {
                constraints.asDynComplex.asSeq();
            }
            constraints.isExpIt -> {
                constraints.asExpIt.asSequence()
            }
            constraints.isExpSeq -> {
                constraints.asExpSeq
            }
            constraints.isStrSeq -> {
                val strSeq: Sequence<String?> = constraints.asStrSeq
                parser.parseLines(strSeq)
            }
            constraints.isFormula -> {
                constraints.asFormula.argSeq
            }
            else -> {
                throw IllegalStateException(constraints.type.toString())
            }
        }

        return seq.filterNot(skipXor)
    }

    private val skipXor: ConstraintFilter = { condition.isXorCube && condition.asXorCube == it }

    val isNoAction: Boolean get() = constraints.isNoAction
    val isDynComplex: Boolean get() = constraints.isDynComplex


    fun addConstraints(csp: Csp): Boolean {
        if (isNoAction) return false

        val ch = if (isDynComplex) {
            addConstraintsDynComplex(csp, constraints.asDynComplex);
        } else {
            addConstraintsExpSeq(csp, toExpSeq());
        }

        if (csp.isFailed) return ch
//        println("condition = ${condition}")
//        condition.assignSafe(csp)
        condition.assign(csp) //todo

        return ch
    }

    private fun addConstraintsDynComplex(csp: Csp, complex: DynComplex): Boolean {
        val constraintVars = complex.vars()

        if (condition.isExp) {
            val overlap = condition.asExp.anyVarOverlap(constraintVars)
            if (!overlap) {
                csp.complex = complex
                return false;
            } else {
                return addConstraintsExpSeq(csp, toExpSeq())
            }
        } else if (condition.isIdentity) {
            csp.complex = complex
            return false;
        } else if (condition.isTransformer) {
            return addConstraintsExpSeq(csp, toExpSeq())
        } else {
            throw IllegalStateException()
        }


    }

    private fun addConstraintsExpSeq(csp: Csp, expSeq: Sequence<Exp>): Boolean {
        var anyChange = false

        expSeq.forEach {
            if (csp.isFailed) return true
            val conditioned = condition.condition(it, log = false, depth = csp.depth)
            val ch = csp.addConstraint(conditioned)
            if (csp.isFailed) return true
            if (ch) anyChange = true
        }
        csp.propagate()
        return anyChange
    }

    private fun addConstraintsClob(csp: Csp, clob: String): Boolean {
        val lines: Sequence<String?> = clob.lineSequence()
        return addConstraintsLines(csp, lines)
    }

    private fun addConstraintsLines(csp: Csp, strSeq: Sequence<String?>): Boolean {


        fun f(expText: String?): Exp? {
            return space.parser.parseExpOrNull(expText);
        }

        val expSeq: Sequence<Exp> = strSeq.map(::f).filterNotNull()
        return addConstraintsExpSeq(csp, expSeq);
    }

    fun mkCsp(): Csp {
        return Csp(space = space, add = this)
    }

    fun print() {
        println("Add:")
        println("  constraints: $constraints")
        println("  condition: $condition")
    }

    companion object {

        //actions

        @JvmStatic
        fun decisionSplit(formula: Formula, decisionVar: Var, sign: Boolean): Add {
            val add = Add(c = formula, cc = decisionVar.lit(sign))
            return add
        }

        @JvmStatic
        fun xorSplit(formula: Formula, xor: Xor, trueVar: Var): Add {
            val xorCube = XorCube(xor, trueVar)
            val add = Add(c = formula, cc = xorCube)
            return add
        }

        @JvmStatic
        fun mixed(and: And): Add {
            return Add(c = and.complex, cc = and.simple)
        }
    }


}

