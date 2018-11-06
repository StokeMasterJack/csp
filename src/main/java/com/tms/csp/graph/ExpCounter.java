package com.tms.csp.graph;

import com.tms.csp.ast.Exp;

public class ExpCounter implements Processor {

    int count = 0;

    @Override
    public void process(Exp e, Context context) {
        context.count++;
        count++;
    }

    public int getCount() {
        return count;
    }
}
