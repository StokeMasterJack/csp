package com.tms.csp.parse.raw;

import com.tms.csp.util.SpaceBase;
import com.tms.csp.ast.Space;
import com.tms.csp.util.ConstraintSink;

/**
 * Clob represents a single constraint formula text set of fact.
 *
 * Parses a single constraint (formula the form of text ) into individual line fact
 */
public abstract class ConstraintParser extends SpaceBase {

    protected ConstraintParser(Space space) {
        super(space);
    }

    public abstract void setOut(ConstraintSink out);

    public abstract void parseConstraint(String line);

}
