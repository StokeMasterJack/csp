package com.tms.csp.graph;

import com.tms.csp.ast.Exp;

import static com.tms.csp.ssutil.Console.prindent;

public class Prindent implements Processor {
    @Override
    public void process(Exp e, Context context) {
        prindent(context.depth, null);

    }
}
