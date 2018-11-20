package com.smartsoft.csp.graph;

import com.smartsoft.csp.ast.Exp;

import static com.smartsoft.csp.ssutil.Console.prindent;

public class Prindent implements Processor {
    @Override
    public void process(Exp e, Context context) {
        prindent(context.depth, null);

    }
}
