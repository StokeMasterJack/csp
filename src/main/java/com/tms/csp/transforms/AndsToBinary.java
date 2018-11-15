package com.tms.csp.transforms;

import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Op;
import com.tms.csp.ast.Space;

public class AndsToBinary extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        Exp first = in.arg1();
        Exp and = first.mkAnd(in.argsRest()).transform(AND_TO_BINARY);
        System.err.println(in.arg1());
        System.err.println(in.args());
        System.err.println(in.argsRest());

        System.err.println("and[" + and + "]");

        Space space = in.getSpace();

        ArgBuilder b = new ArgBuilder(space, Op.And, false);
        b.addExp(first);
        b.addExp(and);
        return b.mk();

    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isAnd() && in.isNary();
    }


}
