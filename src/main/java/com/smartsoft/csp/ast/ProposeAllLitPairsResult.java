package com.smartsoft.csp.ast;

import java.util.HashSet;
import java.util.Set;

import static com.smartsoft.csp.ssutil.Console.prindent;

public class ProposeAllLitPairsResult {

    public Set<LitPair> failed = new HashSet<LitPair>();

    public void printFailedLitPairs(int depth) {
        prindent(depth, "Failed Lit Pairs:");
        for (LitPair litPair : failed) {
            prindent(depth + 1, litPair);
        }
    }

    public void addFailed(LitPair litPair) {
        failed.add(litPair);
    }

    public void addFailed(Exp... lits) {
        LitPair e = new LitPair(lits[0], lits[1]);
        failed.add(e);
    }

    public Set<Exp> toClauses() {
        Set<Exp> vv = new HashSet<Exp>();
        for (LitPair litPair : failed) {
            vv.add(litPair.toClause());
        }
        return vv;
    }


}
