package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

/**
 * Replaces each iff(a b) with an and(imp(a b) imp(b a))
 */
public class RmpsToCnf extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        return in.asRmp().toOr();
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isRmp();
    }


}
