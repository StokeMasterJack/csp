package com.tms.csp.parse.raw;

import com.tms.csp.ast.Space;
import com.tms.csp.util.ConstraintSink;

public class LineConstraintParser extends ConstraintParser {

    public LineConstraintParser(Space space, ConstraintSink out) {
        super(space);
        this.out = out;
    }

    protected ConstraintSink out;

    public void setOut(ConstraintSink out) {
        this.out = out;
    }

    @Override
    public void parseConstraint(String line) {

    }
}
