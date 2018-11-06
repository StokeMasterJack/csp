package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Or;

/**
 *
 */
public class OrToAnd extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        assert in.isOr();

//        if(true) throw new IllegalStateException();
        Or or = in.asOr();
        return or.createEquivAnd();
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isOr();
    }


}
