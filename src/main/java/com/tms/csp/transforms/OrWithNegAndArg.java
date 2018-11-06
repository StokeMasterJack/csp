package com.tms.csp.transforms;

import com.google.common.collect.ImmutableList;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.ExpSetOld;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

//or(a b c !and(x y z)
// to: or(a b c or(!x !y !z))
// to: or(a b c !x !y !z)


/**
 *  or(a b c !and(x y z)
 *  to: or(a b c or(!x !y !z))
 *  to: or(a b c !x !y !z)
 */
public class OrWithNegAndArg extends BaseTransformer {

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isOr() && hasNegAndArg(in);
    }

    @Override
    protected Exp local(Exp in) {
        checkArgument(in.isOr() && hasNegAndArg(in));
        int i = indexOfNegAndArg(in);

        Exp negAndArg = in.getArg(i);
        assert negAndArg.isNegAnd();


        ImmutableList<Exp> argsCopy = in.copyOfArgs();

        throw new UnsupportedOperationException();
    }

//    static Exp convertNegAndToOr(Exp negAnd) {
//        assert negAnd.isNegAnd();
//        Exp arg = negAnd.getArg();
//        List<Exp> args = arg.getArgs();
//        List<Exp> flippedArgs = ExpSetOld.flipAll(args);
//        return negAnd.mkOr(flippedArgs);
//    }

    boolean hasNegAndArg(Exp in) {
        return indexOfNegAndArg(in) != -1;
    }

    int indexOfNegAndArg(Exp in) {
        List<Exp> args = in.args();
        for (int i = 0; i < args.size(); i++) {
            Exp e = args.get(i);
            if (e.isNegAnd()) {
                return i;
            }
        }
        return -1;
    }


}
