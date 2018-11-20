package com.smartsoft.csp.util.it;

import com.google.common.collect.UnmodifiableIterator;
import com.smartsoft.csp.ast.Exp;

import java.util.Iterator;

public abstract class ExpFilterIterator extends UnmodifiableIterator<Exp> {

    private final Iterator<Exp> all;
    private Exp next;

    public ExpFilterIterator(Iterator<Exp> all) {
        this.all = all;
        computeNext();
    }

    private void computeNext() {
        while (all.hasNext()) {
            Exp aNext = all.next();
            if (accept(aNext)) {
                this.next = aNext;
                return;
            }
        }
        next = null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Exp next() {
        assert hasNext();
        Exp tmp = next;
        computeNext();
        return tmp;
    }

    abstract public boolean accept(Exp e);
}
