package com.tms.csp.fm.dnnf;

import com.tms.csp.ast.Exp;
import com.tms.csp.fm.dnnf.visitor.NodeHandler;

public class ChildCounts extends NodeHandler {

    int argCount;
    int constantTrue;
    int constantFalse;
    int lit;
    int cube;
    int or;
    int and;

    boolean nested;

    public void onHead(Exp n) {

        clear();

        for (Exp a : n.getArgs()) {
            argCount++;
            if (a.isConstantTrue()) constantTrue++;
            if (a.isConstantFalse()) constantFalse++;
            if (a.isLit()) lit++;
            if (a.isOr()) or++;
            if (a.isAnd()) and++;

            if (a.isNested(n)) {
                nested = true;
            }
        }

        n.checkChildCounts(this);


    }

    private void clear() {
        argCount = 0;
        constantFalse = 0;
        constantTrue = 0;
        lit = 0;
        cube = 0;
        or = 0;
        and = 0;
        nested = false;
    }

}
