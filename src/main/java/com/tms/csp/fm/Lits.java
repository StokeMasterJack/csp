package com.tms.csp.fm;

import com.tms.csp.ast.Lit;
import com.tms.csp.ast.Space;

import java.util.Iterator;

public interface Lits extends Vars {

    public boolean isTrue(int varId);

    public int getVarCount();

    public Iterable<Lit> litIt(Space space);

    public Iterator<Lit> litIterator(Space space);

    public boolean anyVarIntersection(Lits that);

}
