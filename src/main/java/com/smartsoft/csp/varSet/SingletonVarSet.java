package com.smartsoft.csp.varSet;

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

    public int getVrId() {
        return vr.varId;
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
    final public Space getSpace() {
        return vr.getSpace();
    }

    @Override
    final public int minVrId() {
        return vr.getVarId();
    }

    @Override
    final public int maxVrId() {
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


//    @Override
//    public boolean anyIntersection(VarSet other) {
//        if (other == null || other.isEmpty()) {
//            return false;
//        } else {
//            return other.containsVarId(vr.varId);
//        }
//    }


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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof SingletonVarSet) {
            SingletonVarSet s = (SingletonVarSet) o;
            return s.vr == vr;
        } else if (o instanceof VarSetBuilder) {
            VarSetBuilder s = (VarSetBuilder) o;
            return s.size() == 1 && s.containsVar(vr);
        } else {
            return false;
        }
    }

//    @Override
//    public boolean anyIntersection(VarSetBuilder that) {
//        if (that == null || that.isEmpty()) return false;
//        return that.containsVarId(vr.varId);
//    }


}
