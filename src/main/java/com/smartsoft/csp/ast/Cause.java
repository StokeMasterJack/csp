package com.smartsoft.csp.ast;

public abstract class Cause {

    private final Assignment assignment;

    public Cause(Assignment assignment) {
        this.assignment = assignment;
    }
}

//class Decision extends Cause {
//
//}

class Inference extends Cause {

    private final Assignment assignment;
    private final Exp constraint;

    public Inference(Assignment assignment, Assignment assignment1, Exp constraint) {
        super(assignment);
        this.assignment = assignment1;
        this.constraint = constraint;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public Exp getConstraint() {
        return constraint;
    }
}
