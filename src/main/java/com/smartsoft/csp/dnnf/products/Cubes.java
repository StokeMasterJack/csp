package com.smartsoft.csp.dnnf.products;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.ast.Lit;
import com.smartsoft.csp.ast.Ser;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.util.Control;
import com.smartsoft.csp.util.IntPairCallback2;
import com.smartsoft.csp.util.Range;
import com.smartsoft.csp.util.ints.Ints;
import com.smartsoft.csp.util.varSets.VarSet;

import javax.annotation.Nullable;
import java.util.*;

public class Cubes {

    public static Set<VarSet> toTrueVarRecordSet(Set<Cube> cubes) {
        ImmutableSet.Builder<VarSet> b = ImmutableSet.builder();
        for (Cube cube : cubes) {
            VarSet record = cube.getTrueVars();
            b.add(record);
        }
        return b.build();
    }


    public static class CubeComparator1 implements Comparator<Cube> {
        @Override
        public int compare(Cube c1, Cube c2) {
            String s1 = c1.toString();
            String s2 = c2.toString();
            return s1.compareTo(s2);
        }
    }

    public static final Comparator<Cube> CUBE_COMPARATOR_1 = new CubeComparator1();
    public static final Comparator<Cube> CUBE_COMPARATOR_FAST = new CubeComparatorFast();

    public static void sortByToString(List<Cube> cubes) {
        Collections.sort(cubes, CUBE_COMPARATOR_1);
    }

    public static void sortFast(List<Cube> cubes) {
        Collections.sort(cubes, CUBE_COMPARATOR_FAST);
    }

    public static ImmutableList<Cube> sortedList(Collection<Cube> cubes) {
        // this causes GWT 2.5.1 to crash
        // ArrayList<Cube> aa = new ArrayList<Cube>(cubes);
        ArrayList<Cube> aa = new ArrayList<Cube>();
        for (Cube c : cubes) {
            aa.add(c);
        }
        sortByToString(aa);
        return ImmutableList.copyOf(aa);
    }

    public static List<Cube> sortedListFast(Collection<Cube> cubes) {
        ArrayList<Cube> aa = new ArrayList<Cube>(cubes);
        sortFast(aa);
        return aa;
    }

    public static boolean isPos(int lit) {
        return lit > 0;
    }

    public static boolean isNeg(int lit) {
        return lit < 0;
    }

    public static boolean getSign(int lit) {
        return isPos(lit);
    }

    public static int getVarId(int lit) {
        return Math.abs(lit);
    }

    public static boolean isVarDisjoint(final List<Cube> cubes) {
        return !anyVarIntersection(cubes);
    }


    public static boolean anyVarIntersection(final List<Cube> cubes) {

        Range range = new Range(0, cubes.size() - 1);

        Control<Boolean> control = new Control<Boolean>(false);

        range.forEachPair2(new IntPairCallback2<Boolean>() {
            @Override
            public void processPair(int i, int j, Control<Boolean> control) {
                control.setResult(true);
                Cube cube1 = cubes.get(i);
                Cube cube2 = cubes.get(j);
                if (cube1.anyVarOverlap(cube2)) {
                    control.stop(true);
                }
            }
        });

        return control.getResult();

    }


    public static ImmutableMap<Var, Boolean> toMap(Cube cube) {
        Space space = cube.getSpace();
        ImmutableMap.Builder<Var, Boolean> b = ImmutableMap.builder();
        for (Lit lit : cube.litIt()) {
            Var var = lit.getVr();
            boolean sign = lit.sign();
            b.put(var, sign);
        }
        return b.build();
    }

    public static ImmutableList<Lit> toSortedLitList(Cube cube) {
        ArrayList<Lit> aa = new ArrayList<Lit>();
        for (Lit lit : cube.litIt()) {
            aa.add(lit);
        }
        sortLits(aa);

        return ImmutableList.copyOf(aa);
    }

    public static void sortLits(List<Lit> lits) {
        Collections.sort(lits, LIT_COMPARATOR);
    }

    public void printCubes(List<Cube> cubes) {
        for (Cube cube : cubes) {
            System.err.println(cube);
        }
    }

    public static void serializeCube(Ser a, Cube cube) {
        int L = cube.getSize();
        int i = 0;
        for (Lit lit : cube.litIt()) {
            boolean last = (i == L - 1);
            lit.serialize(a);
            if (!last) {
                a.argSep();
            }
            i++;
        }
    }

    public static void serializeCubeTrueVars(Ser a, Cube cube) {
        int L = cube.getSize();
        int i = 0;
        for (Lit lit : cube.litIt()) {
            boolean last = (i == L - 1);
            if (lit.sign()) {
                lit.serialize(a);
                if (!last) {
                    a.argSep();
                }
            }
            i++;
        }
    }

    public static final Comparator<Lit> LIT_COMPARATOR = new Comparator<Lit>() {
        @Override
        public int compare(Lit o1, Lit o2) {
            String varCode1 = o1.getVarCode();
            String varCode2 = o2.getVarCode();
            int i = varCode1.compareTo(varCode2);
            assert i != 0;
            return i;
        }
    };


    public static Collection<Cube> heavyCopy(Collection<Cube> in) {
        if (true) return in;
//        ImmutableSet.Builder<Cube> xx = ImmutableSet.builder();
//        for (Cube cube : formula) {
//            Cube copy = cube.heavyCopy();
//            xx.add(copy);
//        }
//        return xx.build();
        return null;
    }


    public static boolean checkCubes(Iterable<Cube> cubes) {
        if (true) return true;
        for (Cube cube : cubes) {
            checkCube(cube);
        }
        return true;
    }

    public static boolean checkCube(Cube cube) {
        return true;
    }

    public static Function<Var, Lit> varToLitMapper(final Cube cube) {
        return new Function<Var, Lit>() {
            @Nullable
            @Override
            public Lit apply(@Nullable Var var) {
                assert cube.containsVar(var);
                return var.mkLit(cube.isTrue(var));
            }
        };
    }


//    public static Set<Cube> parseCubes(TinyDnnfParser space, String sCubes) {
//        return space.parseCubes(sCubes);
//    }

    public static boolean setsEquals(Set<?> s, @Nullable Object object) {
        if (s == object) {
            return true;
        }
        if (object instanceof Set) {
            Set<?> o = (Set<?>) object;

            try {
                return s.size() == o.size() && s.containsAll(o);
            } catch (NullPointerException ignored) {
                return false;
            } catch (ClassCastException ignored) {
                return false;
            }
        }
        return false;
    }


    public static class ConstantVarPredicate implements VarPredicate {

        private final boolean sign;

        public ConstantVarPredicate(boolean sign) {
            this.sign = sign;
        }

        @Override
        public boolean isTrue(Var var) {
            return sign;
        }
    }

    public static class SimpleVarPredicate implements VarPredicate {

        private final VarSet trueVars;

        public SimpleVarPredicate(VarSet trueVars) {
            this.trueVars = trueVars;
        }

        @Override
        public boolean isTrue(Var var) {
            return trueVars != null && trueVars.containsVar(var);
        }
    }

    public static class SingleTrueVarPredicate implements VarPredicate {

        private final Var trueVar;

        public SingleTrueVarPredicate(Var trueVar) {
            assert trueVar != null;
            this.trueVar = trueVar;
        }

        @Override
        public boolean isTrue(Var vr) {
            return trueVar.varId == vr.varId;
        }
    }

    public static VarPredicate T = new ConstantVarPredicate(true);
    public static VarPredicate F = new ConstantVarPredicate(false);

    public static VarPredicate constantVarPredicate(boolean sign) {
        if (sign) return T;
        return F;
    }

    public static VarPredicate alwaysTrue() {
        return T;
    }

    public static VarPredicate alwaysFalse() {
        return F;
    }

    public static VarPredicate simpleVarPredicate(VarSet trueVars) {
        return new SimpleVarPredicate(trueVars);
    }

    public static VarPredicate simpleVarPredicate(Var trueVar) {
        return new SingleTrueVarPredicate(trueVar);
    }

    public static int hashCode(VarSet v, VarSet t) {
        return Ints.superFastHash(v, t);
    }
}
