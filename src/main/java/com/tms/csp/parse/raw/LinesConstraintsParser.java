package com.tms.csp.parse.raw;

import com.tms.csp.ast.PLConstants;
import com.tms.csp.ast.Space;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Constraints are formula the form of text lines
 */
public class LinesConstraintsParser extends ConstraintsParser {

    protected ConstraintParser out;

    public LinesConstraintsParser(Space space, ConstraintParser out) {
        super(space);
        this.out = out;
    }

    @Override
    public void setOut(ConstraintParser out) {
        this.out = out;
    }

    @Override
    public void parseConstraints(String clob) {
        checkNotNull(clob);
        checkArgument(clob.indexOf(PLConstants.LF) != -1);
        clob = clob.trim();
        String[] lineArray = clob.split("\n");
        for (String line : lineArray) {
            if (line == null) {
                continue;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            out.parseConstraint(line);
        }
    }

}
