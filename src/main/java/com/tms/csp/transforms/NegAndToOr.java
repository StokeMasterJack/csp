package com.tms.csp.transforms;

import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Op;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

//!and(a b)   => or(!a !b)


//DF: !or(a b) => and(!a !b)
public class NegAndToOr extends BaseTransformer {

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isNegAnd();
    }

    //  !and(a b)   => or(!a !b)
    @Override
    protected Exp local(Exp in) {
        checkArgument(in.isNegAnd());
        List<Exp> args = in.getPos().getArgs();

        ArgBuilder flippedArgs = new ArgBuilder(in.getSpace(), Op.Or);
        for (Exp arg : args) {
            flippedArgs.addExp(arg.flip());
        }

        assert flippedArgs.op().isOrLike();
        return flippedArgs.mk();
//        return ff.getSpace().mkOr();
    }


}
