package com.smartsoft.csp.ast;

import java.util.ArrayList;
import java.util.List;

public final class Assignment {

    private final Var var;
    private final List<Cause> t = new ArrayList<Cause>();
    private final List<Cause> f = new ArrayList<Cause>();

    public Assignment(Lit lit, Cause cause) {
        this.var = lit.getVr();
        if (lit.isPos()) t.add(cause);
        else f.add(cause);
    }

    void assignTrue(Cause cause) {
        f.add(cause);
    }

}
