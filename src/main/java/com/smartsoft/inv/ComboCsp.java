package com.smartsoft.inv;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.smartsoft.csp.TinyConstraint;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.argBuilder.ArgBuilder;
import com.smartsoft.csp.ast.*;
import com.smartsoft.csp.ssutil.TT;
import com.smartsoft.csp.varSets.VarSet;
import com.smartsoft.csp.varSets.VarSetBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class ComboCsp implements PLConstants {

    private Space space;

    private Csp cspFactory;
    private Inv inv;

    private Csp cspInv;

    //different spaces
    private Exp dFactory;
    private Exp dFactoryInv;
    private Exp dFactoryInvGc;
    private String dFactoryInvGcTinyDnnf;

    private VarSet invVars;

    public ComboCsp(String clobFactory, String clobInv, VarInfo varInfo) {
        checkNotNull(clobFactory);
        checkNotNull(clobInv);
        checkNotNull(varInfo);

        Set<String> vars1 = KInv.extractVarCodes(clobInv);
        Set<String> vars2 = Parser.extractVarCodes(clobFactory);
        Set<String> vars = Sets.union(vars1, vars2);


        System.err.println("vars.contains(\"ICOL_LCFC13\"): " + vars.contains("ICOL_LCFC13"));


        this.space = new Space(vars);
        space.csp = new Csp(space, clobFactory);

        VarSetBuilder b = space.varSetBuilder();
        b.addVarCodes1(vars1);
        space.invVars = b.build();

        space.setVarInfo(varInfo);
        this.cspFactory = space.getCsp();
        this.inv = Inv.parse(space, clobInv);


    }

    /**
     * This only exists because Space requires all vars be predefined - before adding fact.
     * Eventually, I think this requirement should be dropped.
     * <p>
     * Examples of Extra vars:
     * <p>
     * dealer vars: extracted from invClob file
     * msrp strict vars: extracted from invClob file
     * msrp bucket vars
     * msrp bit vars
     */
    private static Set<String> buildExtraVars(String clobInv) {
        return KInv.buildExtraVars(clobInv);
    }


    //  buildFactoryDnnf delta: 93268
//  maybeProcessThresholds delta: 554
//  failedCoreLines[4]
//  failedFullLines[698]
//  computeInvalidLines delta: 19511
//    computeConstraintsToRelax delta: 158437
//    initCspInv delta: 10
//    removeRelaxedConstraints delta: 0
//    addInvConstraints delta: 1474
//    compileInvDnnf delta: 680167
//    maybeProcessThresholds delta: 0
//    toDnnf delta: 73810

    public void processInventory() {

        log.info("processInventory...");

        long t1 = System.currentTimeMillis();
        processInventoryInternal();
        long t2 = System.currentTimeMillis();
        log.info("processInventory Delta: " + (t2 - t1));
    }

    public void processInventoryInternal() {

        boolean relax = false;

        checkNotNull(inv);

        TT tt = new TT();

        if (true) {
            log.info("    atVars...");
            this.cspFactory.conditionOutAtVars();
            tt.t("    atVars");
        }


        if (false) {

            cspFactory.toNnf(true);
            tt.t("toNnf");

            vvSimplify();
            tt.t("vvSimplify");

        }

        //Relax
        if (relax) {
            log.info("    compileFactoryDnnf...");
            compileFactoryDnnf();
            tt.t("    compileFactoryDnnf");
        }

        checkNotNull(inv);

        if (true) {
            log.info("    maybeProcessThresholds...");
            maybeProcessThresholds();
            tt.t("    maybeProcessThresholds");
        }

        //Relax
        if (relax) {
            log.info("    computeInvalidInventoryLines...");
            Set<Line> invalidInvLines = computeInvalidInvLines(inv, dFactory);
            tt.t("    computeInvalidInventoryLines");

            //Relax
            log.info("    computeInvalidInvLinesToKill...");
            Set<Line> invalidInvLinesToKill = computeInvalidInvLinesToKill(cspFactory, invalidInvLines, inv);
            tt.t("    computeInvalidInvLinesToKill");

            //Relax
            log.info("    computeConstraintsToRelax...");
            Set<Line> invalidInventoryLinesToKeep = Sets.difference(invalidInvLines, invalidInvLinesToKill);
            Set<Exp> factoryConstraintsToRelax = computeFactoryConstraintsToRelax(cspFactory, invalidInventoryLinesToKeep, getInv());
            tt.t("    computeConstraintsToRelax");

            //Relax
            log.info("    removeFactoryConstraintsToRelax...");
            cspFactory.removeComplexConstraints(factoryConstraintsToRelax);
            tt.t("    removeFactoryConstraintsToRelax");

        }

        initCspInv();

        // .8s
        // 43s w dealer codes as distinct fact
        log.info("    addInvConstraints...");
//        addInvConstraints();
        addInvConstraints2();
        tt.t("    addInvConstraints");


        // 128s
        // 2m 35s
        //compileInvDnnf delta: 706s
        //compileInvDnnf delta: 534s

        log.info("    compileInvDnnf...");
        compileInvDnnf();
        tt.t("    compileInvDnnf");

        log.info("    smoothInvDnnf...");
        dFactoryInv = dFactoryInv.getSmooth();
        tt.t("    smoothInvDnnf");

        log.info("    gc...");
        dFactoryInvGc = dFactoryInv.gc();
        tt.t("    gc");

        log.info("    serializeTinyDnnf...");
        dFactoryInvGcTinyDnnf = dFactoryInvGc.getSpace().serializeTinyDnnf();
        tt.t("    serializeTinyDnnf");
    }

    public void toNnfKeepXors() {
        cspFactory.toNnfKeepXors();
    }

    public void initCspInv() {
        cspInv = cspFactory.copy();
    }

    public void compileFactoryDnnf() {
        dFactory = cspFactory.toDnnf();
    }


    public void compileInvDnnf() {
        cspInv.propagateIntersection();
        dFactoryInv = cspInv.toDnnf();
    }

    public VarSet getOptionBundleOutVars() {
        if (inv == null) {
            return space.newMutableVarSet();
        }
        return inv.getAcyVars();
    }

    public void vvSimplify() {
        cspFactory.simplifyBasedOnVvs();
    }

    public void maybeProcessThresholds() {
        if (inv != null) {
            inv = inv.processAllThreshold();
        }
    }

    public void setInvConstraint(String invClobWithQty) {
        assert this.inv == null;
        if (invClobWithQty == null) {
            this.inv = null;
        }
        this.inv = Inv.parse(space, invClobWithQty);
    }


    public static Set<Line> computeInvalidInvLines(Inv inv, Exp dFactory) {
        checkNotNull(inv);
        checkNotNull(dFactory);

        Set<Line> lines = inv.getLines();

        ImmutableSet.Builder<Line> failedLines = ImmutableSet.builder();

        for (Line line : lines) {
            DynCube lineCube = line.toFullCube(inv);
            Exp n = dFactory.condition(lineCube);

            if (!n.isSat()) {
//                log.info("InvalidInventoryLine: " + line);
                failedLines.add(line);
            }

        }

        return failedLines.build();
    }


    public VarSet getAcyVarsFromInvTable() {
        return inv.getAcyVars();
    }

    public VarMeta getVarMeta() {
        return space.getVarMeta();
    }

    public VarSet getInvAcyVarsFromFactory() {
        VarSet b = space.newMutableVarSet();
        VarMeta varMeta = getVarMeta();
        VarSet vars = cspFactory.mkFormula().getVars();
        for (Var var : vars) {
            if (varMeta.isInvAcyVar(var)) {
                b.addVar(var);
            }
        }
        return b;
    }

    public static TinyConstraint minimize(String factoryConstraint, String inventoryConstraint, VarInfo varInfo) {
        ComboCsp comboCsp = new ComboCsp(factoryConstraint, inventoryConstraint, varInfo);
        comboCsp.processInventory();
        Exp dNode = comboCsp.getFactoryInvDnnf();
        String tinyDnnf = dNode.getSpace().serializeTinyDnnf();
        return new TinyConstraint(tinyDnnf);
    }

    public Exp getFactoryInvDnnf() {
        return dFactoryInv;
    }

    public Exp getFactoryInvDnnfGc() {
        return dFactoryInvGc;
    }

    public String getFactoryInvTinyDnnf() {
        return dFactoryInvGcTinyDnnf;
    }

    private void addInvConstraints() {
        inv.addConstraintsToCsp(cspInv);
    }

    private void addInvConstraints2() {
        inv.addConstraintsToCsp2(cspInv);
    }

    //todo why is this never called?
    private void addNegatedFactoryOnlyInvVars() {
        //create negated fact-only invAcyVars
        VarSet deadAcyVars = computeDeadAcyVars();
        if (deadAcyVars != null && !deadAcyVars.isEmpty()) {
            Exp deadAcyAnd = mkAndForDeadAcyVars(deadAcyVars);
            cspInv.addConstraint(deadAcyAnd);
        }
    }

    private void addGiantOr() {

        assert inv != null;
        assert !inv.isEmpty();

        ArgBuilder orArgs = new ArgBuilder(space, Op.Or);

        for (Line line : inv.getLines()) {
            Exp featureRecord = line.mkFeatureRecord();
            orArgs.addExp(featureRecord);
        }

        Exp giantOr = orArgs.mk();

        cspInv.addConstraint(giantOr);
    }


    private VarSet computeDeadAcyVars() {
        VarSet acyVars1 = cspFactory.getInvAcyVars();
        VarSet acyVars2 = inv.getAcyVars();

        if (!acyVars1.containsAllVars(acyVars2)) {
            VarSet invAcyMinusFactoryAcy = acyVars2.minus(acyVars1);
//            log.warning("invAcyMinusFactoryAcy: " + invAcyMinusFactoryAcy.toVarCodeSet());
        }

        VarSet retVal = acyVars1.minus(acyVars2);

        return retVal;

    }

    public void addCoreXorsFromInv() {
        if (inv == null) return;

        cspInv.addConstraint(inv.mkYearXor());
        cspInv.addConstraint(inv.mkModelXor());
        cspInv.addConstraint(inv.mkXColXor());
        cspInv.addConstraint(inv.mkIColXor());

    }

    public Exp createXor(VarSet vars) {
        return space.mkXor(vars);
    }

    private Exp mkAndForDeadAcyVars(VarSet deadVars) {
        ImmutableSet.Builder<Exp> b = ImmutableSet.builder();
        for (Var var : deadVars.varIt()) {
            b.add(var.mkNegLit());
        }
        return space.mkAnd(b.build());
    }

    public boolean isInvVar(Var var) {
        return space.isInvVar(var);
    }


    private static Logger log = Logger.getLogger(ComboCsp.class.getName());


    public Inv getInv() {
        return inv;
    }


    public String getTinyDnnfFactory() {
        return dFactory.serializeTinyDnnfSpace();
    }

    public String getTinyDnnfInv() {
        return dFactoryInv.serializeTinyDnnfSpace();
    }

    public VarSet getVarsFromInvTable() {
        return inv.getVars();
    }

    public Exp getModelXor() {
        return cspFactory.getModelXor();
    }


    public static Set<Line> computeInvalidInvLinesToKill(Csp factoryCsp, Set<Line> failedInvLines, Inv inv) {

        if (failedInvLines.isEmpty()) {
            return ImmutableSet.of();
        }

        factoryCsp = factoryCsp.copy();
        factoryCsp.conditionOutAtVars();

        Set<Exp> xors = factoryCsp.computeAllXorConstraints();

        Csp masterCsp = new Csp(factoryCsp.getSpace());
        for (Exp xor : xors) {
            masterCsp.addConstraint(xor);
        }
        masterCsp.conditionOutAtVars();


        HashSet<Line> invalidInvLinesToKill = new HashSet<Line>();

        for (Line failedLine : failedInvLines) {


//            failedLine.setInv(factoryCsp.getSpace().getInvClob());
            assert failedLine.getInv() == inv;

            Exp failedLineExp = failedLine.mkFeatureRecord();


            try {

                Csp tmpCsp = masterCsp.copy();
                tmpCsp.addConstraint(failedLineExp);
                tmpCsp.conditionOutAtVars();

                boolean sat = tmpCsp.isSat();
                if (!sat) {
//                    log.info("Rejecting invClob line [" + failedLine + "] because it conflicts with core xor fact");
                    invalidInvLinesToKill.add(failedLine);
                }

            } catch (AlreadyFailedException e) {
//                log.info("Rejecting invClob line [" + failedLine + "] because it conflicts with core xor fact");
                invalidInvLinesToKill.add(failedLine);
            }
        }


        return invalidInvLinesToKill;

    }


    /**
     * Idea #1: do a satLite check instead of a satDeep check
     */
    public static Set<Exp> computeFactoryConstraintsToRelax(Csp factoryCsp, Set<Line> invalidInventoryLines, Inv inv) {

        if (invalidInventoryLines.isEmpty()) {
            return ImmutableSet.of();
        }

        ArrayList<Exp> factoryConstraintsToRelax = new ArrayList<Exp>();

        List<Exp> constraints = factoryCsp.getComplexList();

        Space space = factoryCsp.getSpace();

        Set<Exp> xors = factoryCsp.computeAllXorConstraints();

        for (Exp factoryConstraint : constraints) {


            for (Line failedLine : invalidInventoryLines) {

                Csp tmpCsp = new Csp(space);

                assert failedLine.getInv() == inv;

                Exp failedLineExp = failedLine.mkFeatureRecord();

                try {

                    tmpCsp.addConstraint(factoryConstraint);
                    tmpCsp.addConstraint(failedLineExp);
                    for (Exp coreXor : xors) {
                        tmpCsp.addConstraint(coreXor);
                    }

                    tmpCsp.conditionOutAtVars();

                    boolean sat = tmpCsp.isSat();
                    if (!sat) {

                        if (factoryConstraint.isXorOrContainsXor()) {
                            throw new IllegalStateException();
                        } else {
                            log.info("Relaxing fact constraint 1 [" + factoryConstraint + "] due to invClob line [" + failedLine + "]");
                            factoryConstraintsToRelax.add(factoryConstraint);
                            break;
                        }

                    }

                } catch (AlreadyFailedException e) {

                    if (factoryConstraint.isXorOrContainsXor()) {
                        throw new IllegalStateException();
                    } else {
                        log.info("Relaxing fact constraint 2 [" + factoryConstraint + "] due to invClob line [" + failedLine + "]");
                        factoryConstraintsToRelax.add(factoryConstraint);
                        break;
                    }
                }
            }

        }


        return ImmutableSet.copyOf(factoryConstraintsToRelax);


    }

    public static Set<Exp> findSmallestRelaxSet(Csp factoryCsp, Line failedInventoryLine) {

        assert factoryCsp != null;
        assert factoryCsp.isStable();
        assert factoryCsp.isOpen();

        factoryCsp.conditionOutAtVars();

        Set<Exp> xors = factoryCsp.computeAllXorConstraints();
        Set<Exp> maybeRelax = factoryCsp.getAllNonXorConstraints();
        Set<Exp> relax = new HashSet<Exp>();


        Exp failedLineExp = failedInventoryLine.mkFeatureRecord();

        return findSmallestRelaxSet(failedLineExp, xors, maybeRelax, relax);

    }

    /**
     * Initially:
     * xors + maybeRelaxSet = factoryConstraints
     * relaxSet is empty
     *
     * @param xors
     * @param failedInventoryLine
     * @param maybeRelaxSet       initially contains all non-xor fact fact
     * @param relaxSet            initially empty
     * @return
     */
    public static Set<Exp> findSmallestRelaxSet(Exp failedInventoryLine, Set<Exp> xors, Set<Exp> maybeRelaxSet, Set<Exp> relaxSet) {

        if (relaxSet == null) {
            relaxSet = new HashSet<Exp>();
        }

        assert Sets.intersection(maybeRelaxSet, relaxSet).isEmpty();

        //init
        Space space = failedInventoryLine.getSpace();
        Csp csp = new Csp(space);
        csp.addConstraint(failedInventoryLine);
        for (Exp xor : xors) {
            csp.addConstraint(xor);
        }

        for (Exp exp : relaxSet) {
            csp.addConstraint(exp);
        }

        if (!csp.isSat()) {
            //relaxSet is sufficient to cause un-sat
            return relaxSet;
        } else {
            HashSet<Exp> newMaybeRelax = new HashSet<Exp>();
            for (Exp maybeRelax : maybeRelaxSet) {
                csp.addConstraint(maybeRelax);
                if (!csp.isSat()) {
                    relaxSet.add(maybeRelax);
                    return findSmallestRelaxSet(failedInventoryLine, xors, newMaybeRelax, relaxSet);
                } else {
                    newMaybeRelax.add(maybeRelax);
                }
            }
        }

        throw new IllegalStateException();

    }

    /**
     * Idea #1: do a satLite check instead of a satDeep check
     */
    public static RelaxResult computeFactoryConstraintCombosToRelax(Csp factoryCsp, Line failedLine) {
        checkNotNull(factoryCsp);
        checkNotNull(failedLine);

        assert factoryCsp.isStable();
        assert factoryCsp.isOpen();

        Exp failedLineExp = failedLine.mkFeatureRecord();

        Space space = factoryCsp.getSpace();
        Csp tmpCsp1 = new Csp(space);


        tmpCsp1.addConstraint(failedLineExp);
        tmpCsp1.conditionOutAtVars();

        Set<Exp> xors = factoryCsp.computeAllXorConstraints();
        for (Exp xor : xors) {
            tmpCsp1.addConstraint(xor);
        }

        List<Exp> factoryConstraints = factoryCsp.getAllConstraints();

        assert factoryConstraints.size() == factoryCsp.getComplexConstraintCount() + factoryCsp.getSimpleConstraintCount();


        RelaxResult relaxResult = null;


        //first sweep: keep adding fact until you find the one constraint that makes it fail
        ArrayList<Exp> possibleCollaborators = new ArrayList<Exp>();
        for (Exp factoryConstraint : factoryConstraints) {
            try {
                tmpCsp1.addConstraint(factoryConstraint);
                boolean sat = tmpCsp1.isSat();
                if (!sat) {
                    relaxResult = new RelaxResult(factoryConstraint, failedLine, FailType.FAILED_IS_SAT);
                    break;
                }
            } catch (AlreadyFailedException e) {
                relaxResult = new RelaxResult(factoryConstraint, failedLine, FailType.FAILED_IN_CATCH_BLOCK);
                break;
            }
            possibleCollaborators.add(factoryConstraint);
        }

        //conflictingConstraint1 + xors + failedLine + other fact cause failure

        if (relaxResult == null) {
            throw new IllegalStateException("Failed to find conflictingConstraint1");
        }


        Csp tmpCsp2 = new Csp(space);
        tmpCsp2.addConstraint(failedLineExp);
        for (Exp xor : xors) {
            tmpCsp2.addConstraint(xor);
        }
        tmpCsp2.addConstraint(relaxResult.conflictingConstraint1);
        tmpCsp2.conditionOutAtVars();

        assert tmpCsp2.isSat();


        factoryConstraints = factoryCsp.getAllConstraints();


        //2nd pass
        for (Exp factoryConstraint : factoryConstraints) {
            try {
                tmpCsp2.addConstraint(factoryConstraint);
                boolean sat = tmpCsp1.isSat();
                if (!sat) {
                    relaxResult.conflictingConstraint2 = factoryConstraint;
                    relaxResult.failType2 = FailType.FAILED_IS_SAT;
                    break;
                }
            } catch (AlreadyFailedException e) {
                relaxResult.conflictingConstraint2 = factoryConstraint;
                relaxResult.failType2 = FailType.FAILED_IN_CATCH_BLOCK;
                break;
            }

        }


        Csp tmpCsp3 = new Csp(space);
        for (Exp xor : xors) {
            tmpCsp3.addConstraint(xor);
        }
        tmpCsp3.addConstraint(relaxResult.conflictingConstraint1);
        tmpCsp3.addConstraint(relaxResult.conflictingConstraint2);
        tmpCsp3.conditionOutAtVars();
        tmpCsp3.addConstraint(failedLineExp);

        System.err.println("tmpCsp3:");
        System.err.println("  " + failedLineExp);
        System.err.println("  " + relaxResult.conflictingConstraint1);
        System.err.println("  " + relaxResult.conflictingConstraint2);
        tmpCsp3.print();

        throw new IllegalStateException();


    }

    public static enum FailType {
        FAILED_IN_CATCH_BLOCK, FAILED_IS_SAT;
    }

    public static class RelaxResult {
        public final Exp conflictingConstraint1;
        public final Line failedLine;
        public final FailType failType1;
        public Exp conflictingConstraint2;
        public FailType failType2;

        public RelaxResult(Exp conflictingConstraint1, Line failedLine, FailType failType1) {
            this.conflictingConstraint1 = conflictingConstraint1;
            this.failedLine = failedLine;
            this.failType1 = failType1;
        }

        public void print() {
            System.err.println("conflictingConstraint1: " + conflictingConstraint1);
            System.err.println("conflictingConstraint2: " + conflictingConstraint2);
            System.err.println("failedLine: " + failedLine);
            System.err.println("failType1: " + failType1);
            System.err.println("failType2: " + failType2);
        }
    }
//
//    public Inv getInv() {
//        throw new UnsupportedOperationException();
//    }


}
