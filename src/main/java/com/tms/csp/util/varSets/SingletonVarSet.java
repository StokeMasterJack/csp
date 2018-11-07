package com.tms.csp.util.varSets;

import com.tms.csp.ast.Ser;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.Var;
import com.tms.csp.parse.VarSpace;
import com.tms.csp.util.ints.IntIterator;
import com.tms.csp.util.ints.Ints;

public class SingletonVarSet extends VarSet {

    private final Var var;

    public SingletonVarSet(Var var) {
        this.var = var;
    }

    @Override
    public VarSpace getVarSpace() {
        return var.getVarSpace();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int indexOf(int varId) {
        if (varId == var.getVarId()) return 0;
        return -1;
    }

    @Override
    public int getVarId(int index) throws IndexOutOfBoundsException {
        if (index == 0) return var.varId;
        throw new IndexOutOfBoundsException();
    }

    //    @Override
    public void serialize(Ser a) {
        a.ap(var.getVarCode());
    }

    @Override
    public SingletonVarSet asSingleton() {
        return this;
    }

    @Override
    public boolean containsVarId(int varId) {
        return var.varId == varId;
    }

    @Override
    final public Space getSpace() {
        return var.getSpace();
    }

    @Override
    final public int min() {
        return var.getVarId();
    }

    @Override
    final public int max() {
        return var.getVarId();
    }


    @Override
    public IntIterator intIterator() {
        return new SingletonIntIterator();
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return var.is(prefix);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsAllVars(VarSet that) {
        if (that == null || that.isEmpty()) {
            return true;
        } else if (that.size() == 1) {
            int v = that.min();
            return var.varId == v;
        } else {
            return false;
        }
    }

//    @Override
//    public boolean anyIntersection(VarSet other) {
//        if (other == null || other.isEmpty()) {
//            return false;
//        } else {
//            return other.containsVarId(vr.varId);
//        }
//    }

    @Override
    public VarSet minus(int varIdToRemove) {
        if (varIdToRemove == var.varId) return getSpace().mkEmptyVarSet();
        return this;
    }

    @Override
    public VarSet union(Var var) {
        if (var == this.var) return this;
        return new VarPair(var, this.var);
    }

    @Override
    public VarSet minus(VarSet varsToRemove) {
        if (varsToRemove.containsVarId(var.varId)) {
            return var.getSpace().mkEmptyVarSet();
        } else {
            return this;
        }
    }

    @Override
    public VarSet immutable() {
        return this;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public int computeContentHash() {
        int hash = 1;
        hash = Ints.superFastHashIncremental(var.getVarId(), hash);
        return Ints.superFastHashAvalanche(hash);
    }


    public class SingletonIntIterator implements IntIterator {

        private Var next;

        public SingletonIntIterator() {
            next = var;
        }

        public boolean hasNext() {
            return next != null;
        }

        public int next() {
            Var tmp = next;
            next = null;
            return tmp.getVarId();
        }

    }


    @Override
    public boolean checkMutable() {
        return false;
    }

    @Override
    public boolean containsAllBitSet(VarSetBuilder s) {
        if (s == null || s.isEmpty()) return true;
        if (s.size() > 1) return false;
        return s.containsVar(var);
    }


//    @Override
//    public boolean anyIntersection(VarSetBuilder that) {
//        if (that == null || that.isEmpty()) return false;
//        return that.containsVarId(vr.varId);
//    }


    @Override
    public boolean recomputeSize() {
        return false;
    }
}
