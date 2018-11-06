package com.tms.csp.transforms;

import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Op;

public class FlattenImps extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        Exp e1 = in.getExpr1();
        Exp e2 = in.getExpr2();

        ArgBuilder and = new ArgBuilder(in.getSpace(), Op.And);
        for (Exp e2Arg : e2.getArgs()) {
            Exp imp = in.mkImp(e1, e2Arg);
            and.addExp(imp);
        }

        return and.mk();
    }

    @Override
    protected boolean executeLocal(Exp in) {
        return in.isImp() && in.isPair() && in.arg2().isAnd();
    }
}
