package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp
import java.util.ArrayList

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Preconditions.checkState

abstract class BaseTransformer : Transformer {

    protected val repeatUntilStable: Boolean

    protected constructor(repeatUntilStable: Boolean) {
        this.repeatUntilStable = repeatUntilStable
    }

    protected constructor() {
        this.repeatUntilStable = true
    }

    override fun transform(inExp: Exp): Exp {
        var expIn = inExp
        checkNotNull(expIn)

        while (true) {
            val out: Exp?
            if (expIn.isSimple || expIn.isConstant) {
                out = expIn
            } else {
                out = transformComplex(expIn)
            }

            if (out === expIn) {
                return out
            }  else {
                expIn = out
            }

        }


    }

    protected fun transformComplex(expIn: Exp): Exp {
        val out = processArgs(expIn)

        if (executeLocal(out)) {
            val retVal = local(out) ?: throw IllegalStateException(this.javaClass.name)
            return retVal
        } else {
            return out
        }
    }

    protected fun processArgs(before: Exp): Exp {
        checkState(before.isComplex || before.isNot)

        if (before.isNot) {
            val argBefore = before.arg
            val argAfter = this.transform(argBefore)
            return if (argAfter === argBefore) {
                before
            } else {
                before.space.mkNot(argAfter)
            }

        } else if (before.isComplex) {
            val beforeArgs: MutableList<Exp>
            if (before.isPosVarsExp) {
                beforeArgs = ArrayList()
                for (exp in before.argIt) {
                    beforeArgs.add(exp)
                }
            } else {
                beforeArgs = before.asPosComplex.args.toMutableList()
            }

            val argsAfter = transformArgs(beforeArgs)

            if (argsAfter === beforeArgs) {
                return before
            } else {
                val op = before.asPosComplex.posOp
                val space = before.space
                return space.mkPosComplex(op, argsAfter)
            }
        } else {
            throw IllegalStateException()
        }


    }

    protected abstract fun executeLocal(inExp: Exp): Boolean

    protected abstract fun local(inExp: Exp): Exp?


}
