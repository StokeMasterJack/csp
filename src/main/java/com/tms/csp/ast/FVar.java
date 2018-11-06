package com.tms.csp.ast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FVar {

    FVars fVars;
    public Var vr;
    Exp xorParent;
    List<Exp> vvConstraints;
    List<Exp> complexConstraints;

    Boolean value;

    int score = -1;

    public FVar(Var vr) {
        this.vr = vr;
    }

    public void addConstraint(Exp constraint) {
        assert constraint.isComplex();

        if (!constraint.containsVar(vr)) {
            System.err.println("constraint: " + constraint);
            System.err.println("constraint: " + constraint.getClass());
            System.err.println("vr: " + vr);
            System.err.println();
        }
        assert constraint.containsVar(vr);
        if (constraint.isXor()) {
            assert xorParent == null;
            xorParent = constraint;
            assert xorParent.getVars().containsVar(vr);
        } else if (constraint.isVv()) {
            assert !constraint.isXor();
            addVV(constraint);
        } else if (constraint.isComplex()) {
            assert !constraint.isXor();
            addComplex(constraint);
        }
    }

    public void addVV(Exp constraint) {
        if (vvConstraints == null) {
            vvConstraints = new ArrayList<Exp>();
        }
        vvConstraints.add(constraint);
    }

    public int getVVCount() {
        if (vvConstraints == null) return 0;
        return vvConstraints.size();
    }

    public int getComplexCount() {
        if (complexConstraints == null) return 0;
        return complexConstraints.size();
    }

    public void addComplex(Exp constraint) {
        if (complexConstraints == null) {
            complexConstraints = new ArrayList<Exp>();
        }
        complexConstraints.add(constraint);
    }

    public int getScore() {
        if (score == -1) {
            int s = 0;

            if (xorParent != null) {
                int argCount = xorParent.asXor().getArgCount();
                s += 1000 * argCount;
            }
            s += getVVCount() * 100;

            s += getComplexCount() * 10;

            score = s;
        }
        return score;
    }

    public static void sort(List<FVar> fVars) {
        Collections.sort(fVars, F_VAR_COMPARATOR);
    }

    public static final Comparator<FVar> F_VAR_COMPARATOR = new Comparator<FVar>() {
        @Override
        public int compare(FVar v1, FVar v2) {
            Integer s1 = v1.getScore();
            Integer s2 = v2.getScore();
            return -s1.compareTo(s2);
        }
    };

    public void assign(boolean value) {
        assert this.value == null;
        this.value = value;
    }


    public FVars getFVars() {
        return fVars;
    }

    public void print() {
        System.err.println("vr[" + vr + "]  score[" + getScore() + "]");
    }

}

