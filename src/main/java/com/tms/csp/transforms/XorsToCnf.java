package com.tms.csp.transforms;


import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Op;
import com.tms.csp.ast.Space;
import com.tms.csp.util.IntPairCallback;
import com.tms.csp.util.Range;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Replaces each Xor with an And(Or,And(ShortCircuit))
 * <p>
 * Level 1: And(Or,And(ShortCircuit))
 * Level 2: flatten top and: And(Or,conflict1,conflict2,conflict2)
 * Level 3: convert conflicts to ors: And(Or,conflict1,conflict2,conflict2)
 * Level 3: convert conflicts to ors: And(Or,or1,or2,or3)
 * Level 4: flatten ors
 */
public class XorsToCnf extends Transformer {

    @Nonnull
    @Override
    public Exp transform(@Nonnull Exp in) {
        checkNotNull(in);
        if (!in.isXorOrContainsXor()) {
            return in;
        } else if (!in.isXor()) {
            Exp retVal = transformArgsOnly(in);
            checkNotNull(retVal);
            return retVal;
        } else {

            return in.asXor().toCnf();

//            checkNotNull(formula.args());
//            checkNotNull(!formula.args().isEmpty());
//
//            List<Exp> inArgs = formula.args();
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

    private Exp toOrAndConflicts(List<Exp> xorArgs) {
        if (xorArgs.size() == 0) {
            throw new IllegalStateException();
        } else if (xorArgs.size() == 1) {
            throw new IllegalStateException();
        } else {
            Exp exp = xorArgs.get(0);
            Space space = exp.getSpace();
            ArgBuilder aa = new ArgBuilder(space, Op.And);
            Exp or = space.mkOr(xorArgs);
            aa.addExp(or);
            getConflictsFromXorArgs(xorArgs, aa);
            return aa.mk();
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


    private void getConflictsFromXorArgs(final List<Exp> xorArgs, final ArgBuilder aa) {
        Range range = new Range(xorArgs.size() - 1);
        range.forEachPair(new IntPairCallback() {
            @Override
            public void processPair(int i, int j) {
                Exp ai = xorArgs.get(i);
                Exp aj = xorArgs.get(j);
                Exp conflict = createConflict(ai, aj);
                aa.addExp(conflict);
            }
        });

    }

    protected Exp createConflict(Exp e1, Exp e2) {
        return e1.mkOr(e1.flip(), e2.flip());
    }


}
