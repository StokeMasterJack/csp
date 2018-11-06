package com.tms.csp.fm.dnnf;

import java.util.HashSet;

public class ChildParentRelation {

    private final Integer child;
    private final HashSet<Integer> parents;

    public ChildParentRelation(Integer child, Integer firstParent) {
        this.child = child;
        parents = new HashSet<Integer>();
        parents.add(firstParent);
    }

}
