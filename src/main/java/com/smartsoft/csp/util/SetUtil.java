package com.smartsoft.csp.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Lit;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.fm.dnnf.MaybeIntersects;

import java.util.Set;

public class SetUtil {

    public static <T> void forEachUniquePair(Set<T> set, final PairCallback<T> callback) {
        final ImmutableList<T> list = ImmutableList.copyOf(set);
        Range range = new Range(0, list.size());
        range.forEachPair(new IntPairCallback() {
            @Override
            public void processPair(int i, int j) {
                T m1 = list.get(i);
                T m2 = list.get(j);
                callback.processPair(m1, m2);
            }
        });
    }

    public static boolean anyIntersection(Set s1, Set s2) {
        for (Object e1 : s1) {
            if (s2.contains(e1)) {
                return true;
            }
        }
        return false;
    }

    public static boolean anyVarIntersection(Set<Var> s1, Set<Lit> s2) {
        for (Lit e2 : s2) {
            if (s1.contains(e2.getVr())) {
                return true;
            }
        }
        return false;
    }

    public static <T> Set<T> diff(Set<T> vars, T varToRemove) {
        ImmutableSet.Builder<T> b = ImmutableSet.builder();
        for (T var : vars) {
            if (var != varToRemove) {
                b.add(var);
            }
        }
        return b.build();
    }

    public static final class ConnectedCallback1<T extends MaybeIntersects> implements IntPairCallback {

        final ImmutableList<Exp> list;
        private boolean connected;

        public ConnectedCallback1(Set<Exp> sets) {
            this.list = ImmutableList.copyOf(sets);
            Range range = new Range(0, list.size() - 1);
            range.forEachPair(this);
        }

        @Override
        public void processPair(int i, int j) {
            Exp s1 = list.get(i);
            Exp s2 = list.get(j);

            boolean intersection = s1.anyVarOverlap(s2);

            if (intersection) {
                connected = true;
                throw new StopIteratingException();
            }


        }

        public boolean isConnected() {
            return connected;
        }
    }


    public static final class ConnectedCallback2 implements IntPairCallback {

        final ImmutableList<Set> list;
        private boolean connected;

        public ConnectedCallback2(Set<Set> sets) {
            this.list = ImmutableList.copyOf(sets);
            Range range = new Range(0, list.size() - 1);
            range.forEachPair(this);
        }

        @Override
        public void processPair(int i, int j) {
            Set s1 = list.get(i);
            Set s2 = list.get(j);

            boolean intersection = anyIntersection(s1, s2);

            if (intersection) {
                connected = true;
                throw new StopIteratingException();
            }


        }

        public boolean isConnected() {
            return connected;
        }
    }


    public static boolean isConnected2(Set<Set> sets) {
        ConnectedCallback2 cb = new ConnectedCallback2(sets);
        return cb.isConnected();
    }

    public static boolean isConnected1(Set<Exp> sets) {
        ConnectedCallback1 cb = new ConnectedCallback1(sets);
        return cb.isConnected();
    }

//    public static Set<Integer> cubeToSet(Cube cube) {
//        ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
//        IntList careVars = cube.get_complexVars();
//        for (int careVar : careVars) {
//            boolean tCon = cube.isTrue(careVar);
//            if (tCon) {
//                b.add(careVar);
//            } else {
//                b.add(-careVar);
//            }
//        }
//        return b.build();
//    }


}
