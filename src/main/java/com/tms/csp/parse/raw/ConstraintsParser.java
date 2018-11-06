package com.tms.csp.parse.raw;

import com.tms.csp.util.SpaceBase;
import com.tms.csp.ast.Space;

/**
 * Clob represents a set of fact.
 *
 * Parses a set of fact (formula the form of a clob) into individual line fact
 */
public abstract class ConstraintsParser extends SpaceBase {

    protected ConstraintsParser(Space space) {
        super(space);
    }

    public abstract void setOut(ConstraintParser out);

    public abstract void parseConstraints(String clob);

}
