package com.tms.csp.util.varSets;


import com.tms.csp.ast.Ser;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.Var;
import com.tms.csp.parse.VarSpace;
import com.tms.csp.util.ints.IntIterator;
import com.tms.csp.util.ints.Ints;

import java.util.NoSuchElementException;

public class VarPair extends VarSet {

    public final Var var1;
    public final Var var2;

    public VarPair next;

    public VarPair(Var var1, Var var2) {
        assert var1.getVarId() < var2.getVarId();
        this.var1 = var1;
        this.var2 = var2;
    }

    @Override
    public VarSpace getVarSpace() {
        return var1.getVSpace();
    }

//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        VarPair that = (VarPair) o;
//        return var1.equals(that.var1) && var2.equals(that.var2);
//    }

    @Override
    public void serialize(Ser a) {
        a.append(var1.getVarCode());
        a.argSep();
        a.append(var2.getVarCode());
    }

    @Override
    public VarPair asVarPair() {
        return this;
    }

    @Override
    public int min() throws NoSuchElementException {
        return var1.getVarId();
    }

    @Override
    public int max() throws NoSuchElementException {
        return var2.getVarId();
    }


    @Override
    public boolean containsVarId(int varId) {
        return var1.getVarId() == varId || var2.getVarId() == varId;
    }

    @Override
    public Space getSpace() {
        return var1.getSpace();
    }

    @Override
    public IntIterator intIterator() {
        return new PairIntIterator();
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return var1.is(prefix) || var2.is(prefix);
    }

    @Override
    public int indexOf(int varId) {
        if (varId == var1.getVarId()) return 0;
        if (varId == var2.getVarId()) return 1;
        return -1;
    }

    public class PairIntIterator implements IntIterator {

        private Var next;

        public PairIntIterator() {
            next = var1;
        }

        private Var computeNext(Var previous) {
            if (previous == var1) {
                return var2;
            } else if (previous == var2) {
                return null;
            } else if (previous == null) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }

        public boolean hasNext() {
            return next != null;
        }

        public int next() {
            Var tmp = next;
            next = computeNext(next);
            return tmp.getVarId();
        }

    }

    @Override
    final public int size() {
        return 2;
    }

    @Override
    final public boolean isEmpty() {
        return false;
    }

    @Override
    final public boolean containsAllVars(VarSet that) {
        if (that == null || that.isEmpty()) return true;
        if (that.size() == 1) {
            return containsVarId(that.min());
        }
        if (that.size() == 2) {
            int v1 = that.min();
            int v2 = that.max();
            return containsVarId(v1) && containsVarId(v2);
        }
        if (that.size() > 2) {
            return false;
        }

        return false;
    }

//    @Override
//    public boolean anyIntersection(VarSet other) {
//        if (other == null || other.isEmpty()) {
//            return false;
//        } else if (other.size() == 1) {
//            return containsVarId(other.min());
//        } else if (other.size() == 2) {
//            int v1 = other.min();
//            int v2 = other.max();
//            return containsVarId(v1) || containsVarId(v2);
//        } else {
//            assert other.size() > 2;
//            return other.containsVar(var1) || other.containsVar(var2);
//        }
//
//    }

    @Override
    public VarSet minus(int varIdToRemove) {
        if (varIdToRemove == var1.varId) {
            return var2.mkSingletonVarSet();
        } else if (varIdToRemove == var2.varId) {
            return var1.mkSingletonVarSet();
        }
        return this;
    }

    @Override
    public VarSet union(Var var) {
        if (var == var1 || var == var2) return this;
        VarSetBuilder b = getSpace().newMutableVarSet();
        b.addVar(var1);
        b.addVar(var2);
        b.addVar(var);
        return b.build();
    }

    @Override
    public VarSet minus(VarSet varsToRemove) {
        if (varsToRemove == null || varsToRemove.isEmpty()) return this;

        boolean c1 = varsToRemove.containsVar(var1);
        boolean c2 = varsToRemove.containsVar(var2);

        if (c1 && c2) {
            return getSpace().mkEmptyVarSet();
        } else if (!c1 && !c2) {
            return this;
        } else if (c1) {
            return getSpace().mkVarSet(var2.varId);
        } else {
            return getSpace().mkVarSet(var1.varId);
        }

    }


    @Override
    public VarSet immutable() {
        return this;
    }

    @Override
    public boolean isVarPair() {
        return true;
    }

    @Override
    public int computeContentHash() {
        int hash = 2;
        hash = Ints.superFastHashIncremental(var1.getVarId(), hash);
        hash = Ints.superFastHashIncremental(var2.getVarId(), hash);
        return Ints.superFastHashAvalanche(hash);
    }

    @Override
    final public int getVarId(int index) throws IndexOutOfBoundsException {
        if (index == 0) return var1.getVarId();
        if (index == 1) return var2.getVarId();
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean recomputeSize() {
        return false;
    }

    @Override
    public boolean containsAllBitSet(VarSetBuilder s) {
        if (s.size() > 2) return false;
        for (Var var : s) {
            if (!containsVar(var)) return false;
        }
        return true;
    }


//    @Override
//    public boolean anyIntersection(VarSetBuilder that) {
//        if (that == null || that.isEmpty()) return false;Car
//        return that.containsVarId(var1.varId) || that.containsVarId(var2.varId);
//    }

    public int getMinVarId() {
        assert var1.varId != var2.varId;
        if (var1.varId < var2.varId) {
            return var1.varId;
        } else {
            return var2.varId;
        }
    }

    public int getMaxVarId() {
        assert var1.varId != var2.varId;
        if (var1.varId > var2.varId) {
            return var1.varId;
        } else {
            return var2.varId;
        }
    }
}
