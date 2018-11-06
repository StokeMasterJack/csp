package com.tms.csp.solver2.core;

import java.io.Serializable;
import java.util.Comparator;

public final class DefaultComparator<A extends Comparable<A>> implements
        Comparator<A>, Serializable {
    private static final long serialVersionUID = 1L;

    public int compare(A a, A b) {
        return a.compareTo(b);
    }
}
