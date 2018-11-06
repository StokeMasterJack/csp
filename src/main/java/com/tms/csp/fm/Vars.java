package com.tms.csp.fm;

import com.tms.csp.ast.Space;
import com.tms.csp.ast.Var;

import java.util.Iterator;

public interface Vars {

    public boolean containsVar(int varId);

    public int getVarCount();

    public Iterable<Var> varIt(Space space);

    public Iterator<Var> varIterator(Space space);

    public boolean anyVarIntersection(Vars that);

}
