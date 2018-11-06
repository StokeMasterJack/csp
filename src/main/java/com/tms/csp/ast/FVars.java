package com.tms.csp.ast;

import com.tms.csp.ast.formula.NoVarsException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class FVars {

    private HashMap<Var, FVar> map = new HashMap<Var, FVar>();

    private ArrayList<Xor> xors;
    public ArrayList<FVar> sortedList;

    public FVars(Iterable<Exp> complex) {
        checkNotNull(complex);
        addConstraints(complex);
        if(map.isEmpty()) throw new IllegalStateException();
        sortedList = new ArrayList<FVar>(map.values());
        FVar.sort(sortedList);
    }

    private void addConstraints(Iterable<Exp> e) {
        for (Exp constraint : e) {
            assert constraint.isComplex() : "Non-complex: " + constraint;
            addConstraint(constraint);
        }
    }

    private void addConstraint(Exp constraint) {
        assert constraint.isComplex();
        if (constraint.isXor()) {
            if (xors == null) {
                xors = new ArrayList<Xor>();
            }
            xors.add(constraint.asXor());
        }
        for (Var var : constraint.varIt()) {
            FVar fVar = getFVar(var);
            fVar.addConstraint(constraint);
        }
    }


    private FVar getFVar(Var var) {
        FVar fVar = map.get(var);
        if (fVar == null) {
            fVar = new FVar(var);
            map.put(var, fVar);
        }
        return fVar;
    }

    public static final Comparator<FVar> F_VAR_COMPARATOR = new Comparator<FVar>() {
        @Override
        public int compare(FVar v1, FVar v2) {
            Integer s1 = v1.getScore();
            Integer s2 = v2.getScore();
            return -s1.compareTo(s2);
        }
    };


    public List<FVar> getSortedFVarList() {
        if (this.sortedList == null) {
            this.sortedList = createFVarList();
        }
        return sortedList;
    }

    public ArrayList<FVar> createFVarList() {
        ArrayList<FVar> a = new ArrayList<FVar>();
        for (FVar fVar : map.values()) {
            a.add(fVar);
        }
        return a;
    }


    public void print() {
        for (FVar fVar : sortedList) {
            fVar.print();
        }
    }

    public FVar decide() throws NoVarsException {
        if(sortedList.isEmpty()){
            throw new NoVarsException();
        }
        return sortedList.get(0);
    }

}

