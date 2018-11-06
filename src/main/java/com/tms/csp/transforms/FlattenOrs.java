package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Or;

import static com.google.common.base.Preconditions.checkArgument;

public class FlattenOrs extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        return flattenOr(in.asOr());
    }

    @Override
    protected boolean executeLocal(Exp in) {
        if (in.isOr()) {
            for (Exp arg : in.argIt()) {
                if (arg.isOr()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Exp flattenOr(Or in) {
        checkArgument(in.argList().size() >= 2);
        assert !in.isNestedOr() : in.toString();
        return in;

    }

}
