package com.tms.csp.transforms;

import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.ExpSetOld;
import com.tms.csp.ast.Op;
import com.tms.csp.ast.Space;

import java.util.List;

public class PushAndsOut extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        return pushAndsUp(in);
    }

    @Override
    protected boolean executeLocal(Exp in) {
        if (in.isOr()) {
            List<Exp> args = in.args();
            int argCount = args.size();
            for (int i = 0; i < argCount; i++) {
                Exp arg = args.get(i);
                if (arg.isAnd()) {
                    return true;
                }
            }
        }
        return false;
    }

    //a or (b and c and d) = (a or b) and (a or c) and (a or d)
    private Exp pushAndsUp(Exp e) {

        Space space = e.getSpace();

        List<Exp> args = e.getArgs();

        Exp firstAnd = ExpSetOld.getFirstAnd(args);
        if (firstAnd != null) {
            ArgBuilder orTerms = new ArgBuilder(space, Op.Or);
            for (Exp f : args) {
                if (f != firstAnd) {
                    orTerms.addExp(f);
                }
            }

            Exp x;
            if (orTerms.getSize() == 0) {
                throw new IllegalStateException();
            } else if (orTerms.getSize() == 1) {
                x = orTerms.getFirst();
            } else {
                x = orTerms.mk();
            }

            List<Exp> expressions1 = firstAnd.getArgs();


            ArgBuilder newAndTerms = new ArgBuilder(space, Op.And);

            for (int i = 0; i < expressions1.size(); i++) {
                Exp faExpr = expressions1.get(i);
                Exp or = e.mkOr(x, faExpr);
                newAndTerms.addExp(or);
            }

            return newAndTerms.mk();

        } else {
            ArgBuilder cnfArgs = new ArgBuilder(space, Op.Or);
            cnfArgs.addExpIt(args);
            return cnfArgs.mk();
        }


    }


}
