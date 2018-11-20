package com.smartsoft.csp.transforms


import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Op
import com.smartsoft.csp.util.Range

import com.google.common.base.Preconditions.checkNotNull

/**
 * Replaces each Xor with an And(Or,And(ShortCircuit))
 *
 *
 * Level 1: And(Or,And(ShortCircuit))
 * Level 2: flatten top and: And(Or,conflict1,conflict2,conflict2)
 * Level 3: convert conflicts to ors: And(Or,conflict1,conflict2,conflict2)
 * Level 3: convert conflicts to ors: And(Or,or1,or2,or3)
 * Level 4: flatten ors
 */
class XorsToCnf : Transformer() {

    override fun transform(inExp: Exp): Exp {
        checkNotNull(inExp)
        if (!inExp.isXorOrContainsXor) {
            return inExp
        } else if (!inExp.isXor) {
            val retVal = transformArgsOnly(inExp)
            checkNotNull(retVal)
            return retVal
        } else {

            return inExp.asXor.toCnf()

            //            checkNotNull(formula.args);
            //            checkNotNull(!formula.args.isEmpty());
            //
            //            List<Exp> inArgs = formula.args;
            //            List<Exp> transformedArgs = transformArgs(inArgs);
            //
            //            if (transformedArgs == null || transformedArgs.isEmpty()) {
            //                return formula.getSpace().mkTrue();
            //            }
            //
            //            checkNotNull(transformedArgs);
            //            checkState(!transformedArgs.isEmpty());
            //
            //            Exp retVal = toOrAndConflicts(transformedArgs);
            //            checkNotNull(retVal);
            //            return retVal;
        }
    }

    private fun toOrAndConflicts(xorArgs: List<Exp>): Exp {
        if (xorArgs.size == 0) {
            throw IllegalStateException()
        } else if (xorArgs.size == 1) {
            throw IllegalStateException()
        } else {
            val exp = xorArgs[0]
            val space = exp.space
            val aa = ArgBuilder(space, Op.And)
            val or = space.mkOr(xorArgs)
            aa.addExp(or)
            getConflictsFromXorArgs(xorArgs, aa)
            return aa.mk()
        }
    }

    //    private Exp getConflictsFromXorArgs(final List<Exp> xorArgs) {
    //        Range range = new Range(xorArgs.size() - 1);
    //        final ArgBuilder aa = new ArgBuilder();
    //        range.forEachPair(new Range.PairCallback() {
    //            @Override
    //            public void processPair(int i, int j) {
    //                Exp ai = xorArgs.get(i);
    //                Exp aj = xorArgs.get(j);
    //                Exp conflict = createConflict(ai, aj);
    //                aa.add(conflict);
    //            }
    //        });
    //
    //        if (aa.size() == 1) {
    //            return aa.get(0);
    //        } else {
    //            return aa.mkAnd();
    //        }
    //    }


    private fun getConflictsFromXorArgs(xorArgs: List<Exp>, aa: ArgBuilder) {
        val range = Range(xorArgs.size - 1)
        range.forEachPair { i, j ->
            val ai = xorArgs[i]
            val aj = xorArgs[j]
            val conflict = createConflict(ai, aj)
            aa.addExp(conflict)
        }

    }

    protected fun createConflict(e1: Exp, e2: Exp): Exp {
        return e1.mkOr(e1.flip, e2.flip)
    }


}
