package com.smartsoft.csp.varSet;


import com.smartsoft.csp.ast.Ser;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.parse.VarSpace;
import com.smartsoft.csp.util.ints.IntIterator;
import com.smartsoft.csp.util.ints.Ints;

import java.util.NoSuchElementException;

public class VarPair extends VarSet {

    public final Var vr1;
    public final Var vr2;

    public VarPair next;

    public VarPair(Var var1, Var var2) {
        if (var1.getVarId() < var2.getVarId()) {
            this.vr1 = var1;
            this.vr2 = var2;
        } else if (var1.getVarId() > var2.getVarId()) {
            this.vr1 = var2;
            this.vr2 = var1;
        } else {
            throw new IllegalStateException("equal vars: var1:" + var1 + "  var2:" + var2);
        }
        assert var1.getVarId() < var2.getVarId();

    }

    public VarPair(Space space, int var1, int var2) {
        this(space.getVar(var1), space.getVar(var2));
    }

    @Override
    public VarSpace getVarSpace() {
        return vr1.getVSpace();
    }

//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        VarPair that = (VarPair) o;
//        return var1.equals(that.var1) && var2.equals(that.var2);
//    }

    public int getVr1Id() {
        return vr1.varId;
    }

    public int getVr2Id() {
        return vr2.varId;
    }


    @Override
    public void serialize(Ser a) {
        a.append(vr1.getVarCode());
        a.argSep();
        a.append(vr2.getVarCode());
    }

    @Override
    public VarPair asVarPair() {
        return this;
    }

    @Override
    public int minVrId() throws NoSuchElementException {
        return vr1.getVarId();
    }

    @Override
    public int maxVrId() throws NoSuchElementException {
        return vr2.getVarId();
    }

    @Override
    public String toString() {
        return "VarPair(" + vr1 + "," + vr2 + ")";  //todo tmp
    }


    @Override
    public Space getSpace() {
        return vr1.getSpace();
    }

    @Override
    public IntIterator intIterator() {
        return new PairIntIterator();
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return vr1.is(prefix) || vr2.is(prefix);
    }

    @Override
    public int indexOf(int varId) {
        if (varId == vr1.getVarId()) return 0;
        if (varId == vr2.getVarId()) return 1;
        return -1;
    }


    public class PairIntIterator implements IntIterator {

        private Var next;

        public PairIntIterator() {
            next = vr1;
        }

        private Var computeNext(Var previous) {
            if (previous == vr1) {
                return vr2;
            } else if (previous == vr2) {
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
        hash = Ints.superFastHashIncremental(vr1.getVarId(), hash);
        hash = Ints.superFastHashIncremental(vr2.getVarId(), hash);
        return Ints.superFastHashAvalanche(hash);
    }

    @Override
    final public int getVarId(int index) throws IndexOutOfBoundsException {
        if (index == 0) return vr1.getVarId();
        if (index == 1) return vr2.getVarId();
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean recomputeSize() {
        return false;
    }


//    @Override
//    public boolean anyIntersection(VarSetBuilder that) {
//        if (that == null || that.isEmpty()) return false;Car
//        return that.containsVarId(var1.varId) || that.containsVarId(var2.varId);
//    }

    public int getMinVarId() {
        assert vr1.varId != vr2.varId;
        if (vr1.varId < vr2.varId) {
            return vr1.varId;
        } else {
            return vr2.varId;
        }
    }

    public int getMaxVarId() {
        assert vr1.varId != vr2.varId;
        if (vr1.varId > vr2.varId) {
            return vr1.varId;
        } else {
            return vr2.varId;
        }
    }
}
