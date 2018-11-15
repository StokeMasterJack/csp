package com.tms.csp.fm.dnnf;


import com.google.common.collect.ImmutableSet;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.*;
import com.tms.csp.fm.dnnf.models.Solution;
import com.tms.csp.fm.dnnf.models.Solutions;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.varSets.VarSet;

import java.math.BigInteger;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class DOr extends Or {

    private static final String O_SP = "O ";

    private Exp smooth;

    private Boolean isSmooth;
    private Integer value;  //for counting graph

    public DOr(Space space, int id, Exp[] args) {
        super(space, id, args);
        for (Exp arg : args) {
            assert arg.isDnnf() : arg;
        }
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

        VarSet parentCareVars = getVars();

        for (Exp child : args) {


            child = child.getSmooth();


            VarSet childCareVars = child.getVars();
            VarSet dontCares = parentCareVars.minus(childCareVars);


            Exp smoothChild;
            if (dontCares.isEmpty()) {
                smoothChild = child;
            } else {
                smoothChild = child.smooth(dontCares);
            }


            b.addExp(smoothChild);
        }


        Exp smooth = b.mk();
        smooth._setIsSmooth();

        return smooth;

    }

    @Override
    public Exp project(VarSet outVars) {
        checkNotNull(outVars);

        VarSet vars = this.getVars();
        if (outVars.containsAllVars(vars)) {
            return this;
        }

        ArgBuilder b = new ArgBuilder(_space, Op.DOr);


        for (int i = 0; i < args.length; i++) {
            Exp arg = args[i];
            Exp s = arg.project(outVars);

            if (s.isTrue()) {
                return mkTrue();
            } else if (s.isFalse()) {
                //skip
            } else {
                assert s.isOpen();
                b.addExp(s);

            }

        }

        return b.mk();
    }




    public boolean isOr() {
        return true;
    }


    @Override
    public void serializeTinyDnnf(Ser a) {
        a.append('O');
        a.append(' ');
        serializeArgsTinyDnnf(a);
    }


    @Override
    public boolean isDOr() {
        return true;
    }

    @Override
    public Exp toDnnf() {
        return this;
    }

    @Override
    public Op getOp() {
        return Op.DOr;
    }

    @Override
    public DOr asDOr() {
        return this;
    }

    @Override
    public boolean checkDnnf() {
        checkDeterministic();
        for (Exp arg : args) {
            arg.checkDnnf();
        }
        return true;
    }

    public boolean checkDeterministic() {
        return true;
    }

    public Exp flatten() {
        if (isFlat()) {
            return this;
        }


        ArgBuilder b = new ArgBuilder(_space, Op.DOr);
        for (Exp arg : args) {
            arg = arg.flatten();
            if (arg.isOr()) {
                for (Exp aa : arg.getArgs()) {
                    aa = aa.flatten();
                    b.addExp(aa);
                }
            } else {
                b.addExp(arg);
            }
        }


//        for (Exp arg : retVal) {
//            assert !arg.isNested(this);
//        }

        return b.mk();
    }


    public Set<Cube> computeCubesRough() {
        VarSet parentCareVars = getVars();

        ImmutableSet.Builder<Cube> b = ImmutableSet.builder();
        for (Exp arg : args) {
            VarSet argVars = arg.getVars();
            VarSet dcVars = parentCareVars.minus(argVars);

            if (arg.isLit()) {
                Solution solution = new Solution(_space, arg.asLit().asCube(), dcVars);
                b.addAll(solution);
            } else {
                Set<Cube> argCubes = arg.getCubesRough();
                Set<Cube> cubes = new Solutions(_space, argCubes, dcVars);
                b.addAll(cubes);
            }

        }
        return b.build();
    }


    public Set<Cube> computeCubesSmooth() {
        ImmutableSet.Builder<Cube> b = ImmutableSet.builder();
        for (Exp arg : args) {
            if (arg.isLit()) {
                b.add(arg.asLit().asCube());
            } else {
                Set<Cube> argCubes = arg.getCubesSmooth();
                b.addAll(argCubes);
            }
        }
        return b.build();
    }

    public BigInteger computeSatCount() {
        BigInteger satCount = BigInteger.ZERO;
        for (Exp arg : args) {
//            satCount += arg.getSatCount();
            satCount = satCount.add(arg.getSatCount());
        }
        return satCount;
    }


    @Override
    public Set<Cube> computeCubesNoVarSet() {
        VarSet parentCareVars = getVars();

        ImmutableSet.Builder<Cube> b = ImmutableSet.builder();
        for (Exp arg : args) {
            VarSet argVars = arg.getVars();
            VarSet dcVars = parentCareVars.minus(argVars);

            if (arg.isLit()) {
                Solution solution = new Solution(_space, arg.asLit().asCube(), dcVars);
                b.addAll(solution);
            } else {
                Set<Cube> argCubes = arg.getCubesSmooth();
                Set<Cube> cubes = new Solutions(_space, argCubes, dcVars);
                b.addAll(cubes);
            }

        }
        //        return new DisjointCubeSet(b);
        return b.build();
    }

    @Override
    public int getCubeCount() {
        return computeCubeCount();
    }


    public int computeCubeCount() {
        VarSet parentCareVars = getVars();

        ImmutableSet.Builder<Cube> b = ImmutableSet.builder();
        for (Exp arg : args) {
            VarSet argVars = arg.getVars();
            VarSet dcVars = parentCareVars.minus(argVars);

            if (arg.isLit()) {
                Solution solution = new Solution(_space, arg.asLit().asCube(), dcVars);
                b.addAll(solution);
            } else {
                Set<Cube> argCubes = arg.getCubesSmooth();
                Set<Cube> cubes = new Solutions(_space, argCubes, dcVars);
                b.addAll(cubes);
            }

        }
        //        return new DisjointCubeSet(b);
        return b.build().size();
    }


    /**
     * replace unmatched lits with: lit or (!lit and false)
     */
    public Exp litMatch() {
        Space space = getSpace();

        ImmutableSet.Builder<Exp> newArgs = ImmutableSet.builder();

        for (Exp arg : args) {
            Exp newArg = arg.litMatch();
            newArgs.add(newArg);
        }

        Exp retVal = space.mkDOr(newArgs.build());


        return retVal;

    }

    @Override
    public void _setIsSmooth() {
        //        assert smooth == null;
//        assert isSmooth == null;
        smooth = this;
        isSmooth = true;
    }

    @Override
    public boolean isSmooth() {
        if (isSmooth == null) {
            isSmooth = computeIsSmooth();
        }
        return isSmooth;
    }

    private boolean computeIsSmooth() {
        VarSet parentVars = getVars();
        for (Exp child : args) {
            VarSet childVars = child.getVars();
            if (!parentVars.equals(childVars)) {
                return false;
            }
            if (!child.isSmooth()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public int computeValue(Cube cube) {
        int totalValue = 0;
        for (Exp child : args) {
            int childValue = child.computeValue(cube);
            totalValue += childValue;
        }
        return totalValue;
    }

    @Override
    public int computeValue() {
        assert _space.pics != null;
        if (this.value == null) {
            int totalValue = 0;
            for (Exp child : args) {
                int childValue = child.computeValue();
                totalValue += childValue;
            }
            this.value = totalValue;
        }
        return this.value;
    }

    public boolean computeSat(Lit lit) {
        for (Exp arg : args) {
            boolean sat = arg.computeSat(lit);
            if (sat) return true;
        }
        return false;
    }

    public boolean computeSat(Cube cube) {
        for (Exp arg : args) {
            boolean sat = arg.computeSat(cube);
            if (sat) return true;
        }
        return false;
    }

    public boolean computeSat(VarSet trueVars) {
        for (Exp arg : args) {
            boolean sat = arg.computeSat(trueVars);
            if (sat) return true;
        }
        return false;
    }

    public long computeSatCount1(Lit lit) {
        long satCount = 0;

        VarSet parentCareVars = getVars();

        for (Exp child : args) {

            long satCountWithOutDcs = child.computeSatCount1(lit);

            VarSet childCareVars = child.getVars();
            VarSet dontCares = parentCareVars.minus(childCareVars);

            int dcCount = dontCares.size();

            long dcSatCount = Exp.computeDcPermCount(dcCount);

            if (dcSatCount < 0) {
                throw new IllegalStateException();
            }

            long satCountWithDcs = satCountWithOutDcs * dcSatCount;

            satCount += satCountWithDcs;


        }

        return satCount;
    }

    @Override
    public boolean computeIsSat() {
        for (Exp arg : args) {
            boolean sat = arg.isSat();
            if (sat) {
                return true;
            }
        }
        return false;
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

//    @Override
//    public Set<Lit> getLits() {
//        ImmutableSet.Builder<Lit> b = ImmutableSet.builder();
//        for (Exp arg : args) {
//            b.addAll(arg.getLits());
//        }
//        return b.build();
//    }

//    @Override
//    public boolean isLitMatched() {
//        for (Exp arg : args) {
//            if (!arg.isLitMatched()) {
//                return false;
//            }
//        }
//
//        return true;
//    }

}

