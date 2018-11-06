package com.tms.csp.util.varSets;

import com.tms.csp.ast.Ser;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.Var;
import com.tms.csp.parse.VarSpace;
import com.tms.csp.util.ints.IntIterator;

import java.util.NoSuchElementException;

public class EmptyVarSet extends VarSet {

    private final VarSpace varMap;

    public EmptyVarSet(VarSpace varMap) {
        this.varMap = varMap;
    }

    @Override
    public int indexOf(int varId) {
        return -1;
    }

    public VarSpace getVarSpace() {
        return varMap;
    }

    public Space getSpace() {
        return varMap.getSpace();
    }

    @Override
    public void serialize(Ser a) {

    }

    @Override
    public int getVarSetId() {
        return 0;
    }

    @Override
    public boolean containsVarId(int varId) {
        return false;
    }

    @Override
    public int min() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public int max() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public IntIterator intIterator() {
        return new IntIterator() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public int next() {
                throw new IllegalStateException();
            }

        };
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

    @Override
    public boolean containsAllVars(VarSet that) {
        return false;
    }

//    @Override
//    public boolean anyIntersection(VarSet that) {
//        return false;
//    }

    @Override
    public VarSet minus(int varIdToRemove) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public VarSet union(Var var) {
        return var.mkSingletonVarSet();
    }

    @Override
    public VarSet minus(VarSet varsToRemove) {
        return this;
    }

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
    public boolean containsAllBitSet(VarSetBuilder other) {
        return false;
    }

}
