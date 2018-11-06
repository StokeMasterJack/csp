package com.tms.csp.transforms;

import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Op;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

//  !or(a b) => and(!a !b)
public class NegOrToAnd extends BaseTransformer {

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isNegOr();
    }

    //  !and(a b)   => or(!a !b)
    @Override
    protected Exp local(Exp in) {
        checkArgument(in.isNegOr());
        List<Exp> args = in.getPos().getArgs();

        ArgBuilder flippedArgs = new ArgBuilder(in.getSpace(), Op.And);
        for (Exp arg : args) {
            flippedArgs.addExp(arg.flip());
        }

        return flippedArgs.mk();
    }


}
