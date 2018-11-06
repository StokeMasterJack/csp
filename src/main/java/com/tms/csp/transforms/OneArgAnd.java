package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

/**
 * Replaces each And(a) with a
 */
public class OneArgAnd extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        return in.getArg();
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isAnd() && in.argCount() == 1;
    }

}
