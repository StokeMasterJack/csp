package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

public class AndsToBinary extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        Exp first = in.arg1();
        Exp and = first.mkAnd(in.argsRest());
        return in.mkAnd(first, and);
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isAnd() && in.isNary();
    }


}
