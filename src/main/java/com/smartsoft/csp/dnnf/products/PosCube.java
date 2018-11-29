package com.smartsoft.csp.dnnf.products;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.smartsoft.csp.ast.*;
import com.smartsoft.csp.util.ints.Ints;
import com.smartsoft.csp.varSet.VarSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class PosCube extends AbstractCube {

    public final VarSet vars;

    public PosCube(VarSet vars) {
        this.vars = vars.immutable();
    }

    public VarSet getVars(Var varToRemove) {
        return vars.minus(varToRemove);
    }

    @NotNull
    @Override
    public Iterable<Exp> argIt() {
        return () -> vars.litExpIterator(true);
    }

    @Override
    public Space getSpace() {
        return vars.getSpace();
    }

    public VarSet getVars(EnumSet<Prefix> filter) {
        VarSet that = getSpace().getVars(filter);
        return vars.overlap(that);
    }

    public VarSet getVars(VarSet filter) {
        return vars.overlap(filter);
    }

    public VarSet getVars(Prefix prefix) {
        VarSet that = getSpace().getVars(prefix);
        return vars.overlap(that);
    }

    public VarSet getVars(String prefix) {
        VarSet that = getSpace().getVars(prefix);
        return vars.overlap(that);
    }

    @Override
    public boolean containsVarId(int varId) {
        return vars.containsVarId(varId);
    }

    @Override
    public boolean isTrue(int varId) {
        return containsVarId(varId);
    }

    @Override
    public VarSet getVars() {
        return vars;
    }

    @Override
    public VarSet getTrueVars() {
        return vars;
    }

    public PosCube diff(VarSet filter) {
        VarSet diff = vars.minus(filter);
        return new PosCube(diff);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof PosCube) {
            return eq((PosCube) o);
        }

        return eq((Cube) o);
    }

    public boolean eq(PosCube that) {
        return vars.equals(that.vars);
    }

    public boolean eq(Cube that) {
        return getVars().equals(that.getVars()) && getTrueVars().equals(that.getTrueVars());
    }

    @Override
    public int hashCode() {
        int hash = vars.size();
        hash = Ints.superFastHashIncremental(vars.hashCode(), hash);
        hash = Ints.superFastHashIncremental(0, hash);
        return Ints.superFastHashAvalanche(hash);
    }




    public Var getVar(String prefix) {
        for (Var var : varIt()) {
            if (var.is(prefix)) {
                return var;
            }
        }
        return null;
    }

    public Var getVar(Prefix prefix) {
        return getVar(prefix.getName());
    }

    public Var getVar(EnumSet<Prefix> filter) {
        for (Var var : varIt()) {
            if (var.is(filter)) return var;
        }
        return null;
    }

    public List<Var> sortByVarCode() {
        ArrayList<Var> aa = Lists.newArrayList(varIt());
        Var.sortByVarCode(aa);
        return ImmutableList.copyOf(aa);
    }

    @Override
    public Iterator<Var> varIterator() {
        return vars.varIter();
    }


}
