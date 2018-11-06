package com.tms.csp.util;

public interface ConstraintSet {

    int getConstraintCount();

    boolean areConstraintsDirectlyRelated(int constraint1, int constraint2);
}
