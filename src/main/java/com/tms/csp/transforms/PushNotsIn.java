package com.tms.csp.transforms;


import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Op;
import com.tms.csp.argBuilder.ArgBuilder;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

//!and(a b)   => or(!a !b)


//DF: !or(a b) => and(!a !b)
public class PushNotsIn extends BaseTransformer {

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isNegAnd() || in.isNegOr();
    }

    //  !and(a b)   => or(!a !b)
    @Override
    protected Exp local(Exp in) {
        if (in.isNegAnd()) {
            return negAnd(in);
        } else if (in.isNegOr()) {
            return negOr(in);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * !and(a b)   => or(!a !b)
     */
    private Exp negAnd(Exp in) {
        checkArgument(in.isNegAnd());
        Exp pos = in.getPos();
        List<Exp> pArgs = pos.getArgs();

        ArgBuilder flippedArgs = new ArgBuilder(in.getSpace(),Op.Or);
        for (Exp arg : pArgs) {
            flippedArgs.addExp(arg.flip());
        }

        return flippedArgs.mk();
    }

    /**
     *  !or(a b) => and(!a !b)
     */
    protected Exp negOr(Exp in) {
        checkArgument(in.isNegOr());
        Exp pos = in.getPos();
        List<Exp> pArgs = pos.getArgs();

        Op oop;
        if (in.isAllLits()) {
            oop = Op.DAnd;
        } else {
            oop = Op.And;
        }
        ArgBuilder flippedArgs = new ArgBuilder(in.getSpace(),oop);
        for (Exp arg : pArgs) {
            flippedArgs.addExp(arg.flip());
        }

        return flippedArgs.mk();
//        return formula.getSpace().mkAnd(flippedArgs);
    }


}
