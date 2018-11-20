package com.smartsoft.csp.fm;

import com.smartsoft.csp.ast.Lit;
import com.smartsoft.csp.ast.Space;

import java.util.Iterator;

public interface Lits extends Vars {

    public boolean isTrue(int varId);

    public int getVarCount();

    public Iterable<Lit> litIt(Space space);

    public Iterator<Lit> litIterator(Space space);

    public boolean anyVarIntersection(Lits that);

}
