package com.tms.csp.ast;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExpComparator implements Comparator<Exp> {


    public final static ExpComparator INSTANCE = new ExpComparator();

    @Override
    public int compare(Exp e1, Exp e2) {

        String s1 = sortArity(e1);
        String s2 = sortArity(e2);

        int aa = s1.compareTo(s2);
        if (aa != 0) {
            return aa;
        } else {
            if (e1.isConstant()) {
                assert e2.isConstant();
                return aa;
            } else if (e1.isLit()) {
                assert e2.isLit();
                return aa;
            } else {
                if (e1.isNot()) {
                    assert e2.isNot();
                    e1 = e1.getArg();
                    e2 = e2.getArg();
                }

                assert e1.getArgCount() >= 2;
                assert e1.getArgCount() == e2.getArgCount();

                int L = e1.getArgCount();

                List<Exp> args1 = new ArrayList<Exp>(e1.args());
                List<Exp> args2 = new ArrayList<Exp>(e2.args());

                args1.sort(INSTANCE);
                args2.sort(INSTANCE);

                for (int i = 0; i < L; i++) {
                    Exp a1 = args1.get(i);
                    Exp a2 = args2.get(i);

                    aa = compare(a1, a2);
                    if (aa != 0) {
                        return aa;
                    }

                }


                return 0;

            }
        }


    }

    public static String sortArity(Exp e) {

        boolean negated = e.isNegated();
        StringBuilder sb = new StringBuilder();

        if (negated) {
            sb.append(0);
            if (e.isNegLit()) {
                e = e.flip();
            } else {
                e = e.getArg();
            }
        } else {
            sb.append(1);
        }

        sb.append('-');


        if (e.isConstant() || e.isAnd() && e.size() == 0) {
            sb.append(0);
        } else if (e.isPosLit()) {
            sb.append(1);
        } else if (e.isPair()) {
            sb.append(2);
        } else if (e.isNary()) {
            sb.append(e.getArgCount());
        } else {
            throw new IllegalStateException(e.toString() + " " + e.getClass());
        }


        sb.append('-');

        if (e.isConstant() || e.isAnd() && e.size() == 0) {
            sb.append(0);
        } else if (e.isPosLit()) {
            sb.append(e.getVarCode());
        } else if (e.isPair()) {
            sb.append(e.getComplexOpToken());
        } else if (e.isNary()) {
            sb.append(e.getComplexOpToken());
        } else {
            throw new IllegalStateException();
        }

        return sb.toString();


    }

    public static List<Exp> sortCopyIt(Iterable<Exp> args) {
        ArrayList<Exp> copy = Lists.newArrayList(args);
        Collections.sort(copy, INSTANCE);
        return copy;
    }

    public static List<Exp> sortCopyArray(Exp[] args) {
        ArrayList<Exp> copy = Lists.newArrayList(args);
        Collections.sort(copy, INSTANCE);
        return copy;
    }

    public static List<Exp> sortCopyList(List<Exp> args) {
        ArrayList<Exp> copy = Lists.newArrayList(args);
        Collections.sort(copy, INSTANCE);
        return copy;
    }
}
