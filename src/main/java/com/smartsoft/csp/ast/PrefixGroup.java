package com.smartsoft.csp.ast;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.smartsoft.csp.varSet.VarSet;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class PrefixGroup implements Iterable<Var> {

    private final Space space;
    public final String prefix;
    public VarSet _vars;

    public PrefixGroup(Space space, String prefix) {
        checkNotNull(space);
        checkNotNull(prefix);
        this.space = space;
        this.prefix = prefix;
    }

    @Nonnull
    public String getPrefix() {
        return prefix;
    }


    public VarSet getVars() {
        if (space == null) {
            throw new IllegalStateException();
        }
        if (_vars == null) {
            VarSet vars = space.getVars();   //boom
            _vars = vars.filter(prefix);
        }
        return _vars;
    }

    public Iterable<Var> varIt() {
        return getVars().varIt();
    }

    public List<Var> sortVarsByVarCode() {
        List<Var> vars1 = toVarList();
        Var.sortByVarCode(vars1);
        return vars1;
    }

    @Override
    public Iterator<Var> iterator() {
        return getVars().varIter();
    }

    public List<Var> toVarList() {
        return Lists.newArrayList(varIt());
    }

    public Set<Var> toVarSet() {
        return Sets.newHashSet(varIt());
    }

    public int getVarCount() {
        return getVars().size();
    }

    public int size() {
        return getVars().size();
    }


    @Override
    public String toString() {
        return getPrefix() + ":" + getVarCount() + ":" + sortVarsByVarCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixGroup that = (PrefixGroup) o;
        return prefix.equals(that.prefix) && getVars().equals(that.getVars());
    }

    @Override
    public int hashCode() {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        int result = prefix.hashCode();
        VarSet vars = getVars(); //boom
        int varsHashCode = vars.hashCode();
        result = 31 * result + varsHashCode;
        return result;
    }

    public boolean isXor() {
        return Prefix.isXor(prefix);
    }

    public boolean isCore() {
        return Prefix.isCore(prefix);
    }

    private static Logger log = Logger.getLogger(PrefixGroup.class.getName());
}
