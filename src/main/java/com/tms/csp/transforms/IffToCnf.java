package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

/**
 *
 * iff(a and(z y z)).toCnf =
 *
 *      or(!a x)
 *      or(!a y)
 *      or(!a z)
 *      or(!z !y !x a)
 *
 *      all horn clauses
 *
 */
public class IffToCnf extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        Exp e1 = in.arg1();
        Exp e2 = in.arg2();

        Exp imp1 = in.mkOr(e1.flip(), e2);
        Exp imp2 = in.mkOr(e2.flip(), e1);



        return in.mkAnd(imp1, imp2);
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isIff();
    }


}
