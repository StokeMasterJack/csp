package com.smartsoft.csp.varSet;

import com.smartsoft.csp.ast.Ser;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.parse.VarSpace;
import com.smartsoft.csp.util.ints.IntIterator;

import java.util.NoSuchElementException;

public class EmptyVarSet extends VarSet {

    private static VarSet _INSTANCE;

    public static VarSet getInstance() {
        if (_INSTANCE == null) _INSTANCE = new EmptyVarSet();
        return _INSTANCE;
    }

    private EmptyVarSet() {
    }

    @Override
    public int indexOf(int varId) {
        return -1;
    }

    public VarSpace getVarSpace() {
        throw new UnsupportedOperationException();
    }

    public Space getSpace() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serialize(Ser a) {

    }

    @Override
    public int getVarSetId() {
        return 0;
    }



    @Override
    public int minVrId() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public int maxVrId() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public IntIterator intIterator() {
        return IntIterator.EMPTY;
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }



//    @Override
//    public boolean anyIntersection(VarSet that) {
//        return false;
//    }

    @Override
    public VarSet immutable() {
        return this;
    }

    @Override
    public int computeContentHash() {
        return 0;
    }

    @Override
    public int getVarId(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean recomputeSize() {
        return false;
    }



}
