package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;


/**
 * Replaces each ShortCircuit(a b) with an Or(!a !b)
 */
public class ConflictsToCnf extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {

        Exp a1 = in.arg1();
        Exp e1 = a1.flip();

        Exp a2 = in.arg2();
        Exp e2 = a2.flip();

        Space space = in.getSpace();

        return space.mkOr(e1,e2);
//        return space.mkPosComplex(PosOp.OR, e1, e2);
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isNand();
    }
}
