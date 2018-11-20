package com.smartsoft.csp.dnnf.products;

import com.google.common.collect.Iterators;
import com.smartsoft.csp.ast.Prefix;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.util.varSets.VarSet;

import java.util.EnumSet;
import java.util.Iterator;

public class EmptyCube extends AbstractCube {

    private final Space space;

    public EmptyCube(Space space) {
        this.space = space;
    }

    @Override
    public Space getSpace() {
        return space;
    }

    public VarSet getVars(EnumSet<Prefix> filter) {
        return space.mkEmptyVarSet();
    }

    public VarSet getVars(Prefix prefix) {
        return space.mkEmptyVarSet();
    }

    public VarSet getVars(String prefix) {
        return space.mkEmptyVarSet();
    }

    @Override
    public boolean containsVarId(int varId) {
        return false;
    }

    @Override
    public boolean isTrue(int varId) {
        return false;
    }

    @Override
    public VarSet getVars() {
        return space.mkEmptyVarSet();
    }

    @Override
    public VarSet getTrueVars() {
        return space.mkEmptyVarSet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof EmptyCube) {
            return true;
        }

        if (o instanceof Cube) {
            Cube c = (Cube) o;
            return c.isEmpty();
        }

        return false;
    }


    public boolean eq(Cube that) {
        return that == null || that.isEmpty();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public Var getVar(String prefix) {
        return null;
    }

    public Var getVar(Prefix prefix) {
        return null;
    }

    public Var getVar(EnumSet<Prefix> filter) {
        return null;
    }

    @Override
    public Iterator<Var> varIterator() {
        return Iterators.emptyIterator();
    }
}
