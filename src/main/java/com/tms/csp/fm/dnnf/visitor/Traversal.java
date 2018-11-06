package com.tms.csp.fm.dnnf.visitor;

import com.tms.csp.ast.Exp;

public class Traversal {

    private static int nextTraversalId;

    public int traversalId;

    public Traversal() {
        nextTraversalId++;
        this.traversalId = nextTraversalId;
    }

    public boolean visited(Exp n) {
        throw new UnsupportedOperationException();
//        return n.traversalId == this.traversalId;
    }

    public void markAsVisited(Exp n) {
        throw new UnsupportedOperationException();
//        n.traversalId = this.traversalId;
    }

}
