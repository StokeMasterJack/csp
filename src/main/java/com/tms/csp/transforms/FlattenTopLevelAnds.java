package com.tms.csp.transforms;

import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Op;

import java.util.List;

public class FlattenTopLevelAnds extends BaseTransformer {

    @Override
    protected Exp local(Exp in) {
        return flattenTopLevelAnds(in);
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

    private Exp flattenTopLevelAnds(Exp in) {
        boolean anyChange = false;

        ArgBuilder flatter = new ArgBuilder(in.getSpace(), Op.And);
        for (Exp arg : in.args()) {
            if (arg.isAnd()) {
                List<Exp> args = arg.args();
                flatter.addExpIt(args);
                anyChange = true;
            } else {
                flatter.addExp(arg);
            }
        }


        if (anyChange) {
            return flatter.mk();
//            return space.mkAnd(flatter);
        } else {
            return in;
        }

    }

}
