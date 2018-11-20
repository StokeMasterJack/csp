package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

import com.google.common.base.Preconditions.checkArgument

//or(a b c !and(x y z)
// to: or(a b c or(!x !y !z))
// to: or(a b c !x !y !z)


/**
 * or(a b c !and(x y z)
 * to: or(a b c or(!x !y !z))
 * to: or(a b c !x !y !z)
 */
class OrWithNegAndArg : BaseTransformer() {

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isOr && hasNegAndArg(inExp)
    }

    override fun local(inExp: Exp): Exp? {
        checkArgument(inExp.isOr && hasNegAndArg(inExp))
        val i = indexOfNegAndArg(inExp)

        val negAndArg = inExp.getArg(i)
        assert(negAndArg.isNegAnd)


        @Suppress("UNUSED_VARIABLE") val argsCopy = inExp.copyArgs()

        throw UnsupportedOperationException()
    }

    //    static Exp convertNegAndToOr(Exp negAnd) {
    //        assert negAnd.isNegAnd();
    //        Exp arg = negAnd.getArg();
    //        List<Exp> args = arg.getArgs();
    //        List<Exp> flippedArgs = ExpSetOld.flipAll(args);
    //        return negAnd.mkOr(flippedArgs);
    //    }

    internal fun hasNegAndArg(inExp: Exp): Boolean {
        return indexOfNegAndArg(inExp) != -1
    }

    internal fun indexOfNegAndArg(inExp: Exp): Int {
        val args = inExp.args
        for ((i,e) in args.withIndex()) {
            if (e.isNegAnd) {
                return i
            }
        }
        return -1
    }


}
