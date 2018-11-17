package com.tms.csp.fm.dnnf.visitor

import com.tms.csp.ast.Exp

open class Traversal {

    var traversalId: Int = 0

    init {
        nextTraversalId++
        this.traversalId = nextTraversalId
    }

    fun visited(n: Exp): Boolean {
        //        throw new UnsupportedOperationException();
        return n.traversalId == this.traversalId
    }

    fun markAsVisited(n: Exp) {
        //        throw new UnsupportedOperationException();
        n.traversalId = this.traversalId
    }

    companion object {

        private var nextTraversalId: Int = 0
    }

}
