package com.tms.csp.fm.dnnf;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.*;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.varSets.VarSet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class DAnd extends And {

    private static final String A_SP = "A ";

    private Exp smooth;
    public Boolean isSmooth;
    private Integer value;  //for counting graph

    public DAnd(Space space, int id, Exp[] args) {
        super(space, id, args);

        for (Exp arg : args) {
            if (!arg.isDnnf()) {
                throw new IllegalStateException(arg.getSimpleName() + " " + arg.toString());
            }
        }
    }

    @Override
    public Exp project(VarSet outVars) {
        checkNotNull(outVars);

        VarSet vars = this.getVars();
        if (outVars.containsAllVars(vars)) {
            return this;
        }

        ArgBuilder b = new ArgBuilder(_space, Op.DAnd);

        for (int i = 0; i < args.length; i++) {
            Exp arg = args[i];
            Exp s = arg.project(outVars);

            if (s.isFalse()) {
                return mkFalse();
            } else if (s.isTrue()) {
                //skip
            } else {
                assert s.isOpen();
                b.addExp(s);
            }

        }


        return b.mk();

    }

    public Op getOp() {
        return Op.DAnd;
    }

    @Override
    public DAnd asDAnd() {
        return this;
    }

    @Override
    public boolean checkDnnf() {
        boolean disjoint = checkDisjointConjuncts();
        if (!disjoint) return false;
        for (Exp arg : args) {
            arg.checkDnnf();
        }
        return true;
    }


    public void test1() throws Exception {

    }


//    @Override
//    public void print(int depth) {
//        prindent(depth, "<and>");
//        for (Exp arg : args) {
//            arg.print(depth + 1);
//        }
//        prindent(depth, "</and>");
//    }

    @Override
    public boolean isDAnd() {
        return true;
    }

    @Override
    public Exp toDnnf() {
        return this;
    }


    @Override
    public void checkChildCounts(ChildCounts cc) {

        try {

            assert cc.cube + cc.or + cc.and == cc.argCount;

            assert (cc.cube == 0 || cc.cube == 1);
            assert (cc.constantFalse == 0);
            assert (cc.constantTrue == 0);
            assert (cc.lit == 0);

            boolean o1 = cc.nested && cc.argCount == 2 && cc.cube == 1 && cc.and == 1;
            boolean o2 = !cc.nested && cc.cube == 1 && cc.or >= 1 && cc.cube + cc.or == cc.argCount;
            boolean o3 = !cc.nested && cc.cube == 0 && cc.or >= 1 && cc.or == cc.argCount && cc.argCount > 1;


            assert o1 || o2 || o3;
        } catch (Error e) {
            System.err.println(toXml());
            throw e;
        }
    }


    @Override
    public Exp copyToOtherSpace(Space destSpace) {
        if (getSpace() == destSpace) {
            return this;
        }
        return copyArgsExpToOtherSpace(destSpace, op(), argIt());
    }

    @Override
    public boolean isDnnf() {
        return true;
    }

    @Override
    public boolean isSmooth() {
        if (isSmooth == null) {
            isSmooth = computeIsSmooth();
        }
        return isSmooth;
    }

    private boolean computeIsSmooth() {
        if (smooth != null) {
            return smooth == this;
        }
        for (Exp arg : args) {
            if (!arg.isSmooth()) {
                return false;
            }
        }
        return true;
    }

    public Set<Cube> computeCubesRough() {

        ArrayList<Set<Cube>> a = new ArrayList<Set<Cube>>(args.length);
        for (Exp n : args) {
            if (n.isLit()) {
                ImmutableSet<Cube> litCubeSet = ImmutableSet.of(n.asLit().asCube());
                a.add(litCubeSet);
            } else {
                Set<Cube> cubes = n.getCubesRough();
                a.add(cubes);
            }

        }

        Set<List<Cube>> cartesianProduct = Sets.cartesianProduct(a);
        Set<Cube> flatCubes = flattenCubes(cartesianProduct);

        assert cartesianProduct.size() == flatCubes.size();

        return flatCubes;
    }

    public Set<Cube> computeCubesSmooth() {

        ArrayList<Set<Cube>> a = new ArrayList<Set<Cube>>(args.length);

        for (Exp n : args) {
            Set<Cube> cubes = n.getCubesSmooth();
            a.add(cubes);
        }

        Set<List<Cube>> cartesianProduct = Sets.cartesianProduct(a);
        Set<Cube> flatCubes = flattenCubes(cartesianProduct);

        assert cartesianProduct.size() == flatCubes.size();

        return flatCubes;
    }

    @Override
    public int getCubeCount() {
        return computeCubeCount();
    }

    public int computeCubeCount() {
        int c = 1;
        for (Exp arg : args) {
            int cc = arg.getCubeCount();
            c = c * cc;
        }
        return c;
    }

    public BigInteger computeSatCount() {
        BigInteger c = BigInteger.ONE;
        for (Exp arg : args) {
            BigInteger satCount = arg.getSatCount();
//            c = c * satCount;
            c = satCount.multiply(c);
        }
        return c;
    }

    public Set<Cube> flattenCubes(Set<List<Cube>> cartesianProduct) {
        Set<Cube> flats = new HashSet<Cube>();

        for (List<Cube> cubes : cartesianProduct) {
            Cube flat = flattenCube(cubes);
            flats.add(flat);
        }

        return flats;
    }

    public Cube flattenCube(List<Cube> cubes) {
        return flattenCube4(cubes);
//        return flattenCube5(cubes);
//        return flattenCube3(cubes);
    }


    public Cube flattenCube4(List<Cube> cubes) {
        Space space = getSpace();
        DynCube a = new DynCube(space);
        a.assignCubes(cubes);
        return a;
    }


    @Override
    public int computeValue(Cube cube) {
        int value = 1;
        for (Exp arg : args) {
            value *= arg.computeValue(cube);
        }
        return value;
    }

    @Override
    public int computeValue() {
        assert _space.pics != null;
        if (this.value == null) {
            int v = 1;
            for (Exp arg : args) {
                v *= arg.computeValue();
            }
            this.value = v;
        }
        return this.value;
    }

    @Override
    public boolean computeSat(Lit lit) {
        for (Exp child : args) {
            boolean sat = child.computeSat(lit);
            if (!sat) return false;
        }
        return true;
    }

    @Override
    public boolean computeSat(Cube cube) {
        for (Exp child : args) {
            boolean sat = child.computeSat(cube);
            if (!sat) return false;
        }
        return true;
    }

    @Override
    public boolean computeSat(VarSet trueVars) {
        for (Exp child : args) {
            boolean sat = child.computeSat(trueVars);
            if (!sat) return false;
        }
        return true;
    }

    public long computeSatCount1(Lit lit) {
        long c = 1L;
        for (Exp arg : args) {
            long satCount = arg.computeSatCount1(lit);
            c = c * satCount;
        }
        return c;
    }


    @Override
    public boolean computeIsSat() {
        for (Exp arg : args) {
            boolean sat = arg.isSat();
            if (!sat) {
                return false;
            }
        }
        return true;
    }

    public Exp flatten() {
        Space space = getSpace();

        if (isFlat()) {
            return this;
        }

        ImmutableSet.Builder<Exp> b = ImmutableSet.builder();
        for (Exp arg : args) {
            arg = arg.flatten();
            if (arg.isDAnd()) {
                for (Exp aa : arg.getArgs()) {
                    aa = aa.flatten();
                    b.add(aa);
                }
            } else {
                b.add(arg);
            }
        }

        ImmutableSet<Exp> retVal = b.build();

        for (Exp arg : retVal) {
            assert !arg.isNested(this);
        }

        return space.mkDAnd(retVal);
    }

    @Override
    public void serializeTinyDnnf(Ser a) {
        a.append(A_SP);
        serializeArgsTinyDnnf(a);
    }

    public DAnd smooth(VarSet dontCares) {
        if (dontCares == null || dontCares.isEmpty()) {
            return this;
        }

        ArgBuilder bAnd = new ArgBuilder(_space, Op.DAnd);


        //add current args
        for (Exp arg : args) {
            bAnd.addExp(arg);
        }

        //add special dontCare DOrs
        for (Var dontCare : dontCares) {
            DcOr dcOr = dontCare.mkDcOr();
            bAnd.addExp(dcOr);
        }


        return bAnd.mk().asDAnd();
    }

    public Exp getSmooth() {
        if (smooth == null) {
            if (isSmooth()) {
                smooth = this;
            } else {
                smooth = computeSmooth();
            }
        }
        return smooth;
    }

    private Exp computeSmooth() {
        assert smooth == null;
        if (isSmooth != null && isSmooth) {
            smooth = this;
            return this;
        }

        ArgBuilder b = new ArgBuilder(_space, op());
        for (Exp child : args) {
            Exp smoothChild = child.getSmooth();
            b.addExp(smoothChild);
        }
        Exp smooth = b.mk();
        smooth._setIsSmooth();

        return smooth;
    }

    @Override
    public void _setIsSmooth() {
        smooth = this;
        isSmooth = true;
    }
}
