package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

/**
 * Replaces each imp(a b) with an or(!a b)
 */
public class RmpsToOr extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        return in.asRmp().toOr();
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isRmp();
    }


}
