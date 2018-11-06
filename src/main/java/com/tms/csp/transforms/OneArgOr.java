package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

/**
 * Replaces each Or(a) with a
 */
public class OneArgOr extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        return in.getArg();
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isOr() && in.argCount() == 1;
    }

}
