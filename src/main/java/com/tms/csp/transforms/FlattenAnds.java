package com.tms.csp.transforms;

import com.tms.csp.ast.And;
import com.tms.csp.ast.Exp;

public class FlattenAnds extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        return flattenAnd(in.asAnd());
    }

    @Override
    protected boolean executeLocal(Exp in) {
        if (in.isAnd()) {
            for (Exp arg : in.argIt()) {
                if (arg.isAnd()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Exp flattenAnd(And in) {
        assert !in.isNestedAnd();
        return in;
    }


}
