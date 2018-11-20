package com.smartsoft.csp.ast;

import java.util.ArrayList;

public class Q {

    private ArrayList<Propagator> q;

    public void add(Propagator p) {
        if (q == null) q = new ArrayList<Propagator>();
        q.add(p);
    }

    public void propagate(Csp csp) {
        if (csp.isFailed()) {
            q = null;
            return;
        }

        if (q == null) {
            return;
        }
        if (q.isEmpty()) {
            q = null;
            return;
        }

        Propagator next = q.remove(0);
        next.execute(csp);

        propagate(csp);
    }

    public boolean isDirty() {
        return q != null && !q.isEmpty();
    }


}
