package com.smartsoft.csp.dnnf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.smartsoft.csp.OrConstraint;
import com.smartsoft.csp.ProductHandler;
import com.smartsoft.csp.RangeConstraint;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.ast.*;
import com.smartsoft.csp.dnnf.forEach.ForEach;
import com.smartsoft.csp.dnnf.models.Solutions;
import com.smartsoft.csp.dnnf.products.Cube;
import com.smartsoft.csp.dnnf.products.Cubes;
import com.smartsoft.csp.util.BadVarCodeException;
import com.smartsoft.csp.util.Bit;
import com.smartsoft.csp.varSets.VarSet;
import com.smartsoft.csp.varSets.VarSetBuilder;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Dnnf implements PLConstants, HasSpace {

    private final Space space;
    private final Exp baseConstraint;


    //set by client
    private List<Lit> pics;  //ordered by weight    - all pics
    private Set<Lit> hardPics;  //softPics = pics - hardPics

    //computed - cache - compute each time pics change
    private Exp conditioned;

    public Dnnf(Exp baseConstraint) {
        checkNotNull(baseConstraint);
        this.baseConstraint = baseConstraint;
        this.space = baseConstraint.getSpace();
        this.pics = ImmutableList.of();
    }

    public Set<Lit> getHardPics() {
        return hardPics;
    }

    public void createAndSetHardPics(Set<String> hardPics) {
        Set<Lit> hardPicsSet = new HashSet<Lit>();
        for (String s : hardPics) {
            try {
                Lit hardPicUnit = this.space.mkLit(s);
                if (hardPicUnit != null) {
                    hardPicsSet.add(hardPicUnit);
                }
            } catch (Exception e) {
                Space.log.log(Level.SEVERE, "createAndSetHardPics", e);
            }
        }
        this.hardPics = hardPicsSet;
    }

    public void setHardPics(Set<Lit> hardPics) {
        this.hardPics = hardPics;
    }
//    private void init() {
//        //compute bb of base constraint
//        Assignments bb = baseConstraint.getBB();
//    }


    private void clearSessionState() {
        pics = ImmutableList.of();
        conditioned = null;
    }


    private void processIntCspConstraints(RangeConstraint[] intCspConstraints, ImmutableList.Builder<Lit> b) {

        if (intCspConstraints == null) {
            return;
        }

        if (intCspConstraints.length == 0) {
            return;
        }

        for (RangeConstraint intCspConstraint : intCspConstraints) {
            processIntCspConstraint(intCspConstraint, b);
        }
    }


    private void processIntCspConstraint(RangeConstraint constraint, ImmutableList.Builder<Lit> b) {
        String int32prefix = constraint.getInt32Prefix();
        if (constraint.isEqualityConstraint()) {
            int int32value = constraint.getMin();
            Set<Exp> lits = space.getLitsForInt32(int32value, int32prefix);
            for (Exp lit : lits) {
                b.add(lit.getAsLit());
            }
        } else {
            throw new UnsupportedOperationException("todo"); //todo
        }
    }


    private void processOrConstraints(OrConstraint[] orConstraints, ImmutableList.Builder<Lit> b) {

        if (orConstraints == null) {
            return;
        }

        if (orConstraints.length == 0) {
            return;
        }


        for (OrConstraint orConstraint : orConstraints) {

            VarSetBuilder orVars = space.newMutableVarSet();

            for (String varCode : orConstraint.getArgs()) {
                orVars.addVar(varCode);
            }

            Set<String> prefixes = orVars.getPrefixes();
            checkState(prefixes.size() == 1);
            String prefix = prefixes.iterator().next();

            VarSet xorVars = space.getVars().filter(prefix);

            VarSet falseVars = xorVars.minus(orVars);
            for (Var falseVar : falseVars) {
                b.add(falseVar.mkNegLit());
            }


        }

    }

    public boolean setUserConstraint3(List<Lit> lits) {
        if (lits == null) {
            lits = ImmutableList.of();
        }
        clearSessionState();
        this.pics = lits;
        return true;
    }

    public Set<Var> getTrueVars() {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Lit lit : getBB()) {
            if (lit.isPos()) {
                b.add(lit.getVr());
            }
        }
        for (Lit lit : pics) {
            if (lit.isPos()) {
                b.add(lit.getVr());
            }
        }
        return b.build();
    }


    public Set<Var> getOpenVars() {
        Set<Var> all = ImmutableSet.copyOf(getAllVarsAsSet());
        Set<Var> assigned = getAssignedVars();
        return Sets.difference(all, assigned);
    }

    public Set<Var> getFalseVars() {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Lit lit : getBB()) {
            if (lit.isNeg()) {
                b.add(lit.getVr());
            }
        }
        for (Lit lit : pics) {
            if (lit.isNeg()) {
                b.add(lit.getVr());
            }
        }
        return b.build();
    }


    public Exp getConditioned() {
        if (conditioned == null) {
            if (pics == null || pics.isEmpty()) {
                conditioned = baseConstraint;
            } else {
                conditioned = baseConstraint.condition(getPicsAsCube());
            }
        }
        return conditioned;
    }

    public Cube getPicsAsCube() {
        return new DynCube(space, getPicsSet());
    }

    public Cube getPicsAsCube(Iterable<Lit> lits) {
        return new DynCube(space, lits);
    }

    public Bit getValue(String varCode) {
        Var var = space.getVar(varCode);
        return getValue(var);
    }

    /**
     * return value of 0 would be the same as getValue() returning false
     *
     * @param varCode
     * @return the number of valid unique configurations if this were to be picked
     */
    public long getFacetCount(String varCode) {
        Var var = space.getVar(varCode);
        return getFacetCount(var);
    }

    public long getFacetCount(Var var) {
        VarSet invVars = getInvVars();

        ForEach forEach = new ForEach(baseConstraint);
        forEach.setPics(this.pics, var);
        forEach.setOutVars(invVars);
        BigInteger satCount = forEach.computeSatCount();
        return satCount.longValue();
    }


    public VarSet getInvVars() {
        return space.invVars;
    }

    public int getCubeCount(String var) {
        VarSet invVars = getInvVars();

        Exp constraint = getBaseConstraint();
        if (var == null) {
            constraint = baseConstraint.condition(new DynCube(this.space, this.pics));
        } else {
            constraint = constraint.condition(var);
        }
        constraint = constraint.project(invVars);
        return constraint.getCubeCount();
    }


    // vr is an array of features to facet c;
    // multiple Features per vr will be separated by -
    public int[] getCubeCountBatch(String[] var) {
        VarSet invVars = getInvVars();

        int[] facetArray = new int[var.length];
        for (int i = 0; i < var.length; i++) {
            Exp constraintConditioned = baseConstraint;
            try {
                if (var[i].contains("-")) {
                    String[] varUnits = var[i].split("-");
                    constraintConditioned = constraintConditioned.condition(new DynCube(this.space, space.createLitList(varUnits)));
                } else {
                    constraintConditioned = constraintConditioned.condition(var[i]);
                }
                constraintConditioned = constraintConditioned.project(invVars);
                facetArray[i] = constraintConditioned.getCubeCount();
            } catch (BadVarCodeException e) {

            }
        }
        return facetArray;
    }

    public int getCubeCount() {
        return getCubeCount(null);
    }

    public long getFacetCountExact(Var var) {
        VarSet invVars = getInvVars();

        ForEach forEach = new ForEach(baseConstraint);
        forEach.setPics(this.pics, var);
        forEach.setOutVars(invVars);

        return forEach.execute().size();

    }

    public long getFacetCountExact(String varCode) {
        Var var = getVar(varCode);
        return getFacetCountExact(var);
    }

    public Bit getValue(Var var) {
        return getValue2(var);
    }

    private Bit getValue1(Var var) {
        for (Lit pic : pics) {
            if (pic.getVr() == var) {
                return Bit.fromBool(pic.sign());
            }
        }
        for (Lit bb : getBB()) {
            if (bb.getVr() == var) {
                return Bit.fromBool(bb.sign());
            }
        }
        return Bit.OPEN;
    }


    private Bit getValue2(Var var) {
        for (Lit pic : pics) {
            if (pic.getVr() == var) {
                return Bit.fromBool(pic.sign());
            }
        }

        Exp conditioned = getConditioned();
        if (!conditioned.isSat()) {
            return Bit.FALSE;
        }

        return conditioned.getValue(var);
    }


    public Collection<Cube> getCubes(String... sOutVars) {
        Set<Var> outVars = space.createVarSetFromCodes(sOutVars);
        return getCubes(outVars);
    }


    public Collection<Cube> getCubes(Iterable<String> sOutVars) {
        Set<Var> outVars = space.createVarSetFromCodes(sOutVars);
        return getCubes(outVars);
    }

    public Collection<Cube> getCubes(Set<Var> outVars) {
        ForEach forEach = new ForEach(this);
        forEach.setOutVars(outVars);
        return forEach.execute();
    }


    public Collection<Cube> getProducts(String... sOutVars) {
        Set<Var> outVars = space.createVarSetFromCodes(sOutVars);
        return getCubes(outVars);
    }


    public Collection<Cube> getProducts(Iterable<String> sOutVars) {
        Set<Var> outVars = space.createVarSetFromCodes(sOutVars);
        return getCubes(outVars);
    }

    public Collection<Cube> getProducts(Set<Var> outVars) {
        ForEach forEach = new ForEach(this);
        forEach.setOutVars(outVars);
        return forEach.execute();
    }

    public Set<Var> getAssignedVars() {
        Set<Lit> assignments = getAssignments();
        return space.createVarSetFromLitSet(assignments);
    }

    public Set<Lit> getAssignments() {
        return Sets.union(ImmutableSet.copyOf(pics), getBB());
    }

    public Space getSpace() {
        return space;
    }


    public long forEach(ProductHandler ph, Set<Var> outVars) {
        Collection<Cube> products = getCubes(outVars);
        List<Cube> list = Cubes.sortedListFast(products);

        for (Cube product : list) {
            ph.onProduct(product);
        }

        return products.size();
    }


    public long forEach(ProductHandler ph, String... sOutVars) {
        Set<Var> outVars = space.createVarSetFromCodes(sOutVars);
        Collection<Cube> products = getCubes(outVars);

        List<Cube> list = Cubes.sortedListFast(products);
        for (Cube product : list) {
            ph.onProduct(product);
        }

        return products.size();
    }

    public Set<String> mkProduct(Cube p, String[] outVars) {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (String outVar : outVars) {
            Var var = space.getVar(outVar);
            if (p.isTrue(var.varId)) {
                b.add(outVar);
            }
        }
        return b.build();
    }

    public BigInteger getSatCount() {
        return getConditioned().getSatCount();
    }

    public void setVarInfo(VarInfo varInfo) {
        this.space.setVarInfo(varInfo);
    }

    public boolean isSat() {
        return getConditioned().isSat();
    }

    public void serialize(Appendable a) {
        getConditioned().serialize(new Ser(a));
    }

    public BigInteger satCount() {
        return getSatCount();
    }

    public Set<Var> getAllVarsAsSet() {
        return space.getVarSpace().toSet();
    }

    public long forEachSatCount() {
        ProductHandler.CountingProductHandler ph = new ProductHandler.CountingProductHandler();
        forEach(ph);
        return ph.getCount();
    }

    public boolean isTrue(String varCode) {
        return getValue(varCode).isTrue();
    }

    public boolean isFalse(String varCode) {
        return getValue(varCode).isFalse();
    }

    public boolean isOpen(String varCode) {
        return getValue(varCode).isOpen();
    }

    public boolean isOpen(Var var) {
        return getValue(var).isOpen();
    }

    public boolean isTrue(Var var) {
        return getValue(var).isTrue();
    }

    public boolean isFalse(Var var) {
        return getValue(var).isFalse();
    }

    public long satCount(@Nullable String... sOutVars) {
        Set<Var> outVars = space.createVarSet(sOutVars);
        return satCount(outVars);
    }

    public long satCount(@Nullable Iterable<String> sOutVars) {
        Set<Var> outVars = space.createVarSet(sOutVars);
        return satCount(outVars);
    }

    public long satCount(@Nullable Set<Var> outVars) {
        return satCount2(outVars);
    }


    /**
     * Ordered by weight
     */
    public List<Lit> getUserPics() {
        return pics;
    }

    public Set<Lit> getPicsSet() {
        return ImmutableSet.copyOf(pics);
    }

    private long satCount2(@Nullable Set<Var> outVars) {
        if (!getConditioned().isSat()) {
            return 0;
        }

        ForEach forEach = new ForEach(this);
        forEach.setOutVars(outVars);
        return forEach.computeSatCount().longValue();

    }


    public Set<Lit> getBB() {
        assert isSat();
        Exp conditioned = getConditioned();
        DynCube bb = conditioned.getBb();
        return ImmutableSet.copyOf(bb.litIt());
    }


    public List<String> getAllVarsCodes() {
        Set<String> set = space.createVarCodeSet(getAllVarsAsSet());
        return ImmutableList.copyOf(set);
    }

    public int getVarId(String varCode) {
        return getVar(varCode).getVarId();
    }

    public List<String> getTrueVarsCodes() {
        Set<String> set = space.createVarCodeSet(getTrueVars());
        return ImmutableList.copyOf(set);
    }

    public List<String> getOpenVarsCodes() {
        Set<String> set = space.createVarCodeSet(getOpenVars());
        return ImmutableList.copyOf(set);
    }

    public List<String> getFalseVarsCodes() {
        Set<String> set = space.createVarCodeSet(getFalseVars());
        return ImmutableList.copyOf(set);
    }

    public boolean propose(String constraint) {
        Lit dLit = space.mkLit(constraint);
        Var var = dLit.getVr();

        Bit value = getValue(var);

        if (value.isOpen()) return true;
        if (value.boolValue() == dLit.sign()) return true;

        return false;
    }

    public void printVars() {

        Set<Var> all = getAllVarsAsSet();
        Set<Var> a = getAssignedVars();
        Set<Var> o = getOpenVars();
        Set<Var> t = getTrueVars();
        Set<Var> f = getFalseVars();

        System.err.println("Vars");
        System.err.println("\t all:      " + all.size() + ":\t" + all);
        System.err.println("\t assigned: " + a.size() + ":\t" + a);
        System.err.println("\t open:     " + o.size() + ":\t" + o);
        System.err.println("\t true:     " + t.size() + ":\t" + t);
        System.err.println("\t false:    " + f.size() + ":\t" + f);

        assert a.size() + o.size() == all.size();
        assert t.size() + f.size() == a.size();

        System.err.println("\t satCount: " + getSatCount());
    }

//    @Override
//    public void dump() {
//        System.err.println(getConditioned().serialize());
//    }


    public Var getVar(String varCode) throws BadVarCodeException {
        return getSpace().getVar(varCode);
    }

    private static Logger log = Logger.getLogger(Dnnf.class.getName());

    public SortedSet<String> getFioVarsCodes() {
        return space.getFioVarsCodes();
    }

    public Set<Var> getFioVars() {
        return space.getFioVars();
    }


    public Exp getBaseConstraint() {
        return baseConstraint;
    }


    final public boolean hasDealers() {
        return getBaseConstraint().hasDealers();
    }


    public List<String> getDontCareVars(String[] outVars) {
        // All Vars for the entire session
        VarSet allVars = this.space.getVars();
        VarSet outVarsSet = allVars;

        //create a Var Set out of the provided outVars
        if (outVars != null && outVars.length > 0) {
            VarSetBuilder outVarsBuilder = allVars.builder();
            for (String s : outVars) {
                outVarsBuilder.addVar(s);
            }
            outVarsSet = outVarsBuilder.build();
        }
        // All Vars based c currently conditioned Constraint
        VarSet bcVars = this.baseConstraint.getVars();

        // Get the delta
        VarSet dontCares = outVarsSet.minus(bcVars);

        List<String> dontCaresList = new ArrayList<String>();
        for (Var v : dontCares) {
            dontCaresList.add(v.getVarCode());
        }
        return dontCaresList;
    }

    public List<String> getFixListRemoves(List<String> picks, List<String> varCode) {
        long t0 = System.currentTimeMillis();
        VarSetBuilder newvs = this.space.newMutableVarSet();
        for (String s : picks) {
            Var temp = this.space.getVar(s);
            newvs.add(temp);
        }
        newvs.build();

        long t1 = System.currentTimeMillis();
        //        cspLog("Space:getFixList:computeFixListRemove: " + (System.currentTimeMillis() - t1) + " ms");


//        cspLog("Space:getFixList:overall: " + (System.currentTimeMillis() - t0) + " ms");

        return this.space.computeFixListRemoves(this.baseConstraint, newvs, varCode);
    }

    public Set<Cube> getFixList(List<String> picks, List<String> varCode, List<String> doCareVars) {
        long t0 = System.currentTimeMillis();
        VarSetBuilder newvs = this.space.newMutableVarSet();
        for (String s : picks) {
            Var temp = this.space.getVar(s);
            newvs.add(temp);
        }
        newvs.build();


        long t1 = System.currentTimeMillis();
        Exp fixListExp = this.space.computeFixListExp(this.baseConstraint, newvs, varCode);
//        cspLog("Space:getFixList:computeFixListExp: " + (System.currentTimeMillis() - t1) + " ms");


        Solutions solutions = null;
        if (fixListExp != null) {
            long t2 = System.currentTimeMillis();
            List<String> doCareVarSet = new ArrayList<String>();
            for (String dcv : doCareVars) {
                if (!fixListExp.getValue(dcv).isFalse())
                    doCareVarSet.add(dcv);
            }
            Cube prefix = this.space.computePrefix(this.space.createLitList(doCareVarSet));
//            cspLog("Space:getFixList:computePrefix: " + (System.currentTimeMillis() - t2) + " ms");

            long t3 = System.currentTimeMillis();
            Set<Cube> cubes = fixListExp.getCubesSmooth();
//            cspLog("Space:getFixList:getCubes: " + (System.currentTimeMillis() - t3) + " ms");

            long t4 = System.currentTimeMillis();
            solutions = new Solutions(this.space, cubes, this.space.newMutableVarSet().build(), prefix);
//            cspLog("Space:getFixList:Solutions: " + (System.currentTimeMillis() - t4) + " ms");

        }
//        cspLog("Space:getFixList:overall: " + (System.currentTimeMillis() - t1) + " ms");

        return solutions;
    }

    public List<String> getCodesByPrefix(String prefix) {
        List<String> codes = new ArrayList<String>();
        VarSet vars = this.space.getVars(prefix);
        for (Var v : vars) {
            codes.add(v.getCode());
        }

        return codes;
    }

//    public Collection<Cube> computeInventory() {
//        throw new UnsupportedOperationException();
//
//
//    }

    /**
     * Maybe adds msrp
     */
    public VarSet computeInventoryOutVars() {

        VarSet vars = baseConstraint.getVars();

        VarSetBuilder b = space.newMutableVarSet();

        VarSet invVars = getInvVars();
        assert invVars != null;

        b.addVars(invVars);

        assert !invVars.containsPrefix(MSRP_PREFIX);
        assert !invVars.containsPrefix(DLR_PREFIX);

        if (vars.containsPrefix(MSRP_PREFIX)) {
            VarSet msrpVars = baseConstraint.getMsrpVars();
            assert msrpVars != null && !msrpVars.isEmpty();
            b.addVars(msrpVars);
        }

        return b.build();

    }


    public Collection<Cube> computeInventory() {

        VarSet outVars = computeInventoryOutVars();
//        VarSet outVars = getInvVars();

        ForEach forEach = new ForEach(baseConstraint);
        forEach.setPics(this.getPicsSet());
        forEach.setOutVars(outVars);

        return forEach.execute();
    }


    //    public native void cspLog(String value) /*-{
    //        top.console.info(value);
    //    }-*/;

//       public ModelGroups getCubesGroupedByModel(VarSet outVars) {
//           ForEach forEach = new ForEach(baseConstraint);
//
//           forEach.setPics(this.getPicsSet());
//           forEach.setOutVars(outVars);
//
//           return forEach.executeGroupedByModel();
//       }

}
