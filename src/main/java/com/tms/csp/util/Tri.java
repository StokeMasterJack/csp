package com.tms.csp.util;

public interface Tri {

    boolean isTrue();

    boolean isFalse();

    boolean isOpen();

    boolean isNonConstant();

    boolean isConstant();

    boolean isAssigned();

    boolean isUnassigned();

    boolean boolValue();

    boolean is(boolean that);

    boolean dup(boolean that);
}
