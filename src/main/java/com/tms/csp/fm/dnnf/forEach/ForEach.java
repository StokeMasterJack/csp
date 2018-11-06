package com.tms.csp.fm.dnnf.forEach;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tms.csp.ast.*;
import com.tms.csp.fm.dnnf.Dnnf;
import com.tms.csp.fm.dnnf.models.Solutions;
import com.tms.csp.fm.dnnf.partial.PartialPicsCallback;
import com.tms.csp.fm.dnnf.partial.PartialPicsGenerator;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.fm.dnnf.vars.VarGrp;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class ForEach implements PLConstants {

    public final int maxSatCount = 100000;

    private int defaultMaxPartialResults = 200;
    private int maxPartialResults = 200;

    private VarGrp outVarsKey;
    private VarSet outVars;

    public final Exp baseConstraint;

    private List<Lit> pics;

    private final Space space;

    private Exp conditioned;
    private Exp projection;

    public ForEach(Exp baseConstraint) {
        checkNotNull(baseConstraint);
        this.baseConstraint = baseConstraint.getSmooth();
        this.space = baseConstraint.getSpace();
    }

    public ForEach(Dnnf csp) {
        this(csp.getBaseConstraint());
        setPics(csp.getUserPics());
    }

    public void setDefaultMaxPartialResults(int defaultMaxPartialResults) {
        this.defaultMaxPartialResults = defaultMaxPartialResults;
    }


    public void setPics(String sPics) {
        this.pics = space.createLitList(sPics);
        this.conditioned = null;
        this.projection = null;
    }

    public void setPics(List<Lit> pics, Var var) {
        ImmutableList.Builder<Lit> b = ImmutableList.builder();
        b.add(var.mkPosLit());
        b.addAll(pics);
        ImmutableList<Lit> pp = b.build();
        setPics(pp);
    }


    public void setPics(List<Lit> pics) {
        this.pics = pics;
        this.conditioned = null;
        this.projection = null;
    }

    public void setPics(Set<Lit> pics) {
        this.pics = new ArrayList<Lit>(pics);
        this.conditioned = null;
        this.projection = null;
    }

//    public void setOutVarsAlwaysTrue() {
//        setOutVars(VarFilter.alwaysTrue());
//    }

//    public void setOutVars(Set<Var> outVars) {
//        this.outVars = outVars;
//        this.projection = null;
//    }


    public void setOutVars(VarGrp key) {
        this.outVarsKey = key;
        this.outVars = space.getVars(key);
        this.projection = null;
    }

//    public VarSet convert(VarSet _vars) {
//        VarSetBuilder b = space.varSetBuilder();
//        b.addVars(_vars);
//        return b.immutable();
//    }

    public void setOutVars(VarSet outVars) {
        this.outVarsKey = null;
        this.outVars = outVars;
        this.projection = null;
    }

    public void setOutVars(Set<Var> outVars) {
        VarSet oVarSet;
        if (outVars == null || outVars.isEmpty()) {
            oVarSet = space.getVars();
        } else {
            oVarSet = space.createVars(outVars);
        }
        setOutVars(oVarSet);
    }

//    public VarSet convert(Set<Var> outVars) {
//        return space.createVarsFromVarIdSet(outVars);
//    }

    public void setOutVars(String sOutVars) {
        VarSet outVars = space.createVars(sOutVars);
        setOutVars(outVars);
    }

    private void maybeCondition() {
        if (conditioned == null) {
            if (pics == null || pics.isEmpty()) {
                conditioned = baseConstraint;
            } else {
                conditioned = baseConstraint.condition(new DynCube(space, pics));
            }
        }
    }

    public void maybeProject() {
        maybeCondition();
        if (projection == null) {
            if (outVars != null) {
                conditioned.getVars();
                projection = conditioned.project(outVars);
            } else {
                projection = conditioned;
            }
        }
    }

    public long computeSatCount() {
//        printForEachParams();
        maybeCondition();
        maybeProject();

        VarSet dontCareVars = getDontCareVars();

        long dcPermCount = Exp.computeDcPermCountLong(dontCareVars.size());
        return projection.getSatCount() * dcPermCount;
    }

    public Set<Cube> execute() throws MaxSatCountExceededException {

        VarSet dontCareVars = getDontCareVars();
        long satCount = computeSatCount();


        if (false) {
            log.info("SatCounts: ");
            log.info("  baseConstraint[" + baseConstraint.getSatCount() + "]");
            log.info("  conditioned[" + conditioned.getSatCount() + "]");
            log.info("  projection[" + projection.getSatCount() + "]");
        }

        if (false) {
            boolean isProjectionSmooth = projection.isSmooth();
            System.err.println("isProjectionSmooth[" + isProjectionSmooth + "]");
        }

        //todo put this back
//        if (satCount > maxSatCount) {
//            throw new MaxSatCountExceededException();
//        }

//        ImmutableAssignments picsCube = ImmutableAssignments.createFromLitSet(getSpace(), pics);
//        Set<Var> picsVars = picsCube.get_vars();


        //cubes will not contain pics._vars
        //cubes will not contain any dcVars
        Set<Cube> cubes;
        if (projection.isTrue()) {
            cubes = ImmutableSet.of(space.mkEmptyCube());
        } else {
            cubes = projection.getCubesSmooth();
        }

        Cube prefix = computePrefix();

        Solutions solutions = new Solutions(space, cubes, dontCareVars, prefix);

        if (false) {
            //todo fix this: satCount must work after a projection
            if (solutions.size() != satCount) {
                System.err.println("cubeCount != satCount");
                System.err.println("  cubes   [" + solutions.size() + "]");
                System.err.println("  satCount[" + satCount + "]");
                throw new IllegalStateException("cubeCount != satCount");
            }
        }

        return solutions;


    }


    Cube computePrefix() {
        if (pics == null) {
            return null;
        }
        Set<Lit> picSet = ImmutableSet.copyOf(pics);

        DynCube aa = new DynCube(space);
        for (Lit lit : picSet) {
            if (outVars.containsVar(lit.getVr())) {
                aa.assign(lit);
            }
        }

        return aa;
    }


    /**
     * If an outVar is not present formula the formula it could mean:
     * <p>
     * the vr was assigned and thus no longer present formula the formula
     * the vr became a dont care an fell out of the formula
     */
    public VarSet getDontCareVars() {
        maybeCondition();
        maybeProject();

        VarSet outVars = getOutVars();
        VarSet picVars = getPicVars();
        VarSet formulaVars = getFormulaVars();

//        System.err.println("Formula Vars:");
//        for (Var formulaVar : formulaVars) {
//            System.err.println("  fVar: " + formulaVar);
//        }

        assert outVars != null;
        assert !outVars.isEmpty();
        VarSet nonFormulaOutVars = outVars.minus(formulaVars);

        if (picVars == null || picVars.isEmpty()) {
            return nonFormulaOutVars;
        } else {
            return nonFormulaOutVars.minus(picVars);
        }


    }

    public void test1() throws Exception {
//        Set<Var> outDcVars = Sets.difference(outVars, Sets.union(conditioned.get_vars(), getPicsSet()));
//        Set<Var> outCareVars = Sets.intersection(outVars, getConditioned().get_vars());
//        Exp projected = getConditioned().project(outVars);
//        return projected.getSatCount() * Exp.computeDcPermCount(outDcVars.size());
    }

    public VarSet toVarSet(Set<Var> vars) {
        VarSetBuilder b = space.newMutableVarSet();
        b.addVars(vars);
        return b.immutable();
    }


    public VarSet getFormulaVars() {
        maybeCondition();
        maybeProject();
        return projection.getVars();
    }

    public VarSet getPicVars() {
        if (pics == null || pics.isEmpty()) {
            return space.mkEmptyVarSet();
        }

        VarSetBuilder b = space.newMutableVarSet();
        for (Lit pic : pics) {
            b.add(pic);
        }

        return b.immutable();
    }

    public LinkedHashSet<Cube> executePartial() {


        final LinkedHashSet<Cube> results = new LinkedHashSet<Cube>();

        PartialPicsCallback<Lit> callback = new PartialPicsCallback<Lit>() {
            @Override
            public boolean onPics(List<Lit> pics) {

                System.err.println("callback[" + pics + "]");

                if (results.size() >= maxPartialResults) {
                    return false;
                }

                ForEach forEach = new ForEach(baseConstraint);
                forEach.setPics(pics);
                forEach.setOutVars(outVars);


                long satCount = forEach.computeSatCount();

                if (satCount > maxSatCount) {
                    System.err.println("Early stop");
                    return false;
                }

                Set<Cube> cubes = forEach.execute();

                System.err.println("  cubes.size[" + cubes.size() + "]");

                for (Cube cube : cubes) {
                    results.add(cube);
                    if (results.size() >= maxPartialResults) {
                        return false;
                    }
                }

                return results.size() < maxPartialResults;


            }
        };

        ImmutableList<Lit> initPics = ImmutableList.copyOf(pics);
        new PartialPicsGenerator<Lit>(callback, initPics);

        return results;
    }


    public void printForEachParams() {
        System.err.println("pics[" + pics + "]");
        System.err.println("outVarsKey[" + outVarsKey + "]");
        System.err.println("outVars[" + outVars + "]");
    }

    public Space getSpace() {
        return space;
    }

    public int getMaxSatCount() {
        return maxSatCount;
    }

    public int getMaxPartialResults() {
        return maxPartialResults;
    }

    public void setMaxPartialResults(int maxPartialResults) {
        this.maxPartialResults = maxPartialResults;
    }

    public VarSet getOutVars() {
        if (outVars == null) {
            return space.getVars();
        }
        return outVars;
    }

    public VarGrp getOutVarsKey() {
        return outVarsKey;
    }

    public Exp getBaseConstraint() {
        return baseConstraint;
    }

    public List<Lit> getPics() {
        return pics;
    }

    public int getDefaultMaxPartialResults() {
        return defaultMaxPartialResults;
    }

    public Exp getProjected() {
        return projection;
    }


    public static class MaxSatCountExceededException extends RuntimeException {
    }

    public boolean emptyPicks() {
        return pics == null || pics.isEmpty();
    }

    private static Logger log = Logger.getLogger(ForEach.class.getName());


}
