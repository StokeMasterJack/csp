package com.smartsoft.csp.varSets;

import com.smartsoft.csp.ast.Ser;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.parse.VarSpace;
import com.smartsoft.csp.util.ints.IntIterator;
import com.smartsoft.csp.util.ints.Ints;

public class SingletonVarSet extends VarSet {

    private final Var vr;

    public SingletonVarSet(Var var) {
        this.vr = var;
    }

    @Override
    public VarSpace getVarSpace() {
        return vr.getVarSpace();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int indexOf(int varId) {
        if (varId == vr.getVarId()) return 0;
        return -1;
    }

    public Var getVr() {
        return vr;
    }

    @Override
    public int getVarId(int index) throws IndexOutOfBoundsException {
        if (index == 0) return vr.varId;
        throw new IndexOutOfBoundsException();
    }

    //    @Override
    public void serialize(Ser a) {
        a.ap(vr.getVarCode());
    }

    @Override
    public SingletonVarSet asSingleton() {
        return this;
    }

    @Override
    public boolean containsVarId(int varId) {
        return vr.varId == varId;
    }

    @Override
    final public Space getSpace() {
        return vr.getSpace();
    }

    @Override
    final public int min() {
        return vr.getVarId();
    }

    @Override
    final public int max() {
        return vr.getVarId();
    }


    @Override
    public IntIterator intIterator() {
        return new SingletonIntIterator();
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return vr.is(prefix);
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
            return vr.varId == v;
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
        if (varIdToRemove == vr.varId) return getSpace().mkEmptyVarSet();
        return this;
    }

    @Override
    public VarSet plus(VarSet that) {
        if (that instanceof EmptyVarSet) {
            return this;
        } else if (that instanceof SingletonVarSet) {
            SingletonVarSet singleton = that.asSingleton();
            if (this.vr == singleton.vr) {
                return this;
            } else {
                return new VarPair(this.vr, singleton.vr);
            }
        } else if (that instanceof VarPair) {
            if (that.containsVar(vr)) {
                return that;
            } else {
                VarSetBuilder b = that.copyToVarSetBuilder();
                b.add(vr);
                return b;
            }
        } else if (that instanceof VarSetBuilder) {
            if (that.containsVar(vr)) {
                return that;
            } else {
                VarSetBuilder b = that.copyToVarSetBuilder();
                b.add(vr);
                return b;
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public VarSet plus(Var var) {
        if (var == this.vr) return this;
        return new VarPair(var, this.vr);
    }

    @Override
    public VarSet minus(VarSet varsToRemove) {
        if (varsToRemove.containsVarId(vr.varId)) {
            return vr.getSpace().mkEmptyVarSet();
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
        hash = Ints.superFastHashIncremental(vr.getVarId(), hash);
        return Ints.superFastHashAvalanche(hash);
    }


    public class SingletonIntIterator implements IntIterator {

        private Var next;

        public SingletonIntIterator() {
            next = vr;
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
        return s.containsVar(vr);
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
