package com.tms.csp.ast

import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.formula.Formula
import com.tms.csp.ast.formula.KFormula

/**
 * csp - formula and XorSplit  or varSplit
 * are all c the same level
 */
class XorSplit(val formula: KFormula, val xor: Xor) {


    val argSeq = formula.argSeq
    val space: Space = formula.space;
    val xorVars = xor.vars
    val formulaVars = formula.vars


    val isSat: Boolean
        get() {




            for (trueVar in xorVars) {


                try {

                    val rr = mkCsp(trueVar)
                    if (rr.isSat()) {
                        return true
                    }
                } catch (e: ConflictingAssignmentException) {
                    throw UnsupportedOperationException()
                } catch (e: AlreadyFailedException) {
                    throw UnsupportedOperationException()
                }

            }

            return false
        }


    fun toDnnf(): Exp {

        val xorVars = xor.vars
        val space = xorVars.getSpace()

        val b = ArgBuilder(space, Op.DOr)
        for (trueVar in xorVars) {
            if (b.isShortCircuit) {
                return space.mkTrue()
            }
            try {
                val rr = mkCsp(trueVar)

                val n = rr.toDnnf()
                if (n.isFalse()) {
                    //                    System.err.println("XorSplit.toDnnf - skip");
                    //skip
                } else if (n.isTrue()) {
                    //                    System.err.println("XorSplit.toDnnf - true");
                    return space.mkTrue()
                } else {
                    //                    System.err.println("XorSplit.toDnnf - open");
                    assert(n.isOpen())
                    assert(n.isDnnf())
                    b.addExp(n)
                }
            } catch (e: ConflictingAssignmentException) {
                throw UnsupportedOperationException()
            }

        }

        val retVal = b.mk()

        if (b.isShortCircuit) {
            assert(retVal.isTrue)
        }
        if (b.isEmpty) {
            assert(retVal.isFalse)
        }

        if (!retVal.isDOr) {
            //            System.err.println("!DOr: " + retVal);
        }
        return retVal
    }


    fun plSatCount(): Long {



        var t: Long = 0
        for (trueVar: Var in xorVars) {


            try {
                val rr = mkCsp(trueVar)
                val satCount = rr.satCountPL(formulaVars)
                t += satCount
            } catch (e: ConflictingAssignmentException) {
                throw UnsupportedOperationException()
            } catch (e: AlreadyFailedException) {
                throw UnsupportedOperationException()
            }

        }

        return t


    }

    //unique to split (parent level): parent, xor
    //unique tp child level:trueVar
    fun mkCsp(trueVar: Var): Csp {
        val add = Add.xorSplit(formula = formula, xor = xor, trueVar = trueVar)
        return add.mkCsp()
    }
}