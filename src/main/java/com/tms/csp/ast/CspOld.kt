package com.tms.csp.ast

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableList
import com.google.common.collect.Multimap
import com.tms.csp.util.CodeResolver
import com.tms.csp.util.EvalContext
import com.tms.csp.util.SpaceUtil
import java.util.Collections


class CspOld(private val k: Csp) : SpaceUtil() {
    //
    //    public void printYearSeriesModels2() {
    //        ImmutableSet<Exp> set = getModelImpliesSeriesYear();
    //        for (Exp exp : set) {
    //            System.err.println(exp);
    //        }
    //    }
    //
    //
    //    public boolean anyConstants() {
    //        for (Exp exp : complex) {
    //            if (exp.isOrContainsConstant()) return true;
    //        }
    //        return false;
    //    }
    //
    ////    public void addConstraints(Iterable<Exp> fact) {
    ////        for (Exp constraint : fact) {
    ////            addConstraint(constraint);
    ////        }
    ////    }
    //
    //    public boolean checkNoConstants() {
    //        if (anyConstants()) {
    //            throw new IllegalStateException();
    //        }
    //        return true;
    //    }
    //
    //
    ////    public boolean isVvCareVar(Var vr) {
    ////        assert isOpen(vr);
    ////        for (Exp e : getVVConstraints()) {
    ////            IntList careVars = e.getVars();
    ////            if (careVars.contains(vr.getId())) {
    ////                return true;
    ////            }
    ////        }
    ////        return false;
    ////    }
    //
    //
    //    public boolean isCareVar(Var var) {
    //
    //        for (Exp exp : complex) {
    //            if (exp.caresAbout(var)) return true;
    //        }
    //
    //        return false;
    //    }
    //
    //
    //    public void sortConstraintsByArity() {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //
    //    public void sortConstraintsByStrLenDesc() {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public void sortXorsByVarCount() {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public void sortConstraintsByVarCount() {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public Exp getLargestNonXorNonColorConstraintByVarCount() {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public int getLargestConstraintVarCount() {
    //        Exp L = getLargestComplexConstraintBasedOnVarCount();
    //        if (L == null) {
    //            return 0;
    //        } else {
    //            return L.getVarCount();
    //        }
    //    }
    //
    //    public Exp getLargestComplexConstraintBasedOnVarCount() {
    //        Exp largest = null;
    //        for (Exp e : complex) {
    //            if (e.isXor()) {
    //                throw new IllegalStateException();
    //            }
    //            if (largest == null || e.getVarCount() > largest.getVarCount()) {
    //                largest = e;
    //            }
    //        }
    //
    //
    //        if (largest != null && largest.isXor()) {
    //            throw new IllegalStateException();
    //        }
    //
    //        return largest;
    //    }
    //
    //

    //
    //
    //    public void convertRequiresOrToConflictForModels() {
    //        Exp modelXor = this.getModelXor();
    //        ImmutableSet<Exp> modelVars = ImmutableSet.copyOf(modelXor.getArgs());
    //        DynComplex tmp = complex;
    //        complex = new DynComplex(space);
    //        for (Exp e : tmp) {
    //            Exp exp = e.requiresOrToConflicts(modelVars);
    //            addConstraint(exp);
    //        }
    //}


    //
    //    public boolean removeSimple(Exp lit) {
    //        if (simple.isEmpty()) return false;
    //        assert lit.isSimple();
    //        return simple.removeLit(lit.asLit());
    //    }
    //
    //    public boolean removeComplex(Exp exp) {
    //        if (complex.isEmpty()) return false;
    //        assert exp.isComplex();
    //        boolean contains = complex.contains(exp);
    //        boolean removed = complex.remove(exp);
    //        assert contains == removed;
    //        return removed;
    //    }
    //
    //    public boolean removeComplex(String expText) {
    //        Exp exp = space.parseExp(expText);
    //        return removeComplex(exp);
    //    }
    //
    //
    //    public int getOpenCareVarCount() {
    //        return getFormulaVars().size();
    //    }
    //
    //    public int nestedXorCount() {
    //        int nxc = 0;
    //        for (Exp complexConstraint : complex) {
    //            int xc = complexConstraint.xorCount();
    //            if (complexConstraint.isXor()) {
    //                xc--;
    //            }
    //            nxc += xc;
    //        }
    //        return nxc;
    //    }
    //

    //    public boolean isNnf() {
    //        if (complex.isEmpty()) {
    //            return true;
    //        }
    //        for (Exp constraint : complex) {
    //            if (!constraint.isNnf()) {
    //                return false;
    //            }
    //        }
    //        return true;
    //    }
    //
    //    public boolean isBnf() {
    //        if (complex.isEmpty()) {
    //            return true;
    //        }
    //        for (Exp constraint : complex) {
    //            if (!constraint.isBnf()) {
    //                return false;
    //            }
    //        }
    //        return true;
    //    }
    //

    //
    //    public boolean isClean() {
    //        return !isDirty();
    //    }
    //
    //
    ////    public boolean isVvDirty() {
    ////        if (qq == null) {
    ////            return false;
    ////        } else if (isFailed()) {
    ////            qq = null;
    ////            return false;
    ////        } else if (qq.isEmpty()) {
    ////            qq = null;
    ////            return false;
    ////        }
    ////
    ////        assert qq.size() > 0;
    ////        return true;
    ////    }
    //
    //
    ////    public void propagateVV() {
    ////
    ////        if (!isVvDirty()) {
    ////            return;
    ////        }
    ////
    ////        Exp nextVv = qq.remove(0);
    ////
    ////
    ////        simplifyForNewVV(nextVv);
    ////
    ////        propagate();
    ////    }
    //
    //    public boolean canSimplify(Iterable<Exp> constraints, Exp lit) throws FailedCspException {
    //        Var var = lit.getVr();
    //        for (Exp constraint : constraints) {
    //            if (constraint.caresAbout(var)) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
    //
    //    public void simplifyComplex() throws FailedCspException {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public ImmutableList<Exp> getComplexConstraintsContaining(Var var) {
    //        ArrayList<Exp> a = new ArrayList<Exp>();
    //        for (Exp e : complex) {
    //            if (e.containsVar(var)) {
    //                a.add(e);
    //            }
    //        }
    //        return ImmutableList.copyOf(a);
    //    }
    //
    ////    public ImmutableList<Exp> getMsrpConstraints() {
    ////        ArrayList<Exp> a = new ArrayList<Exp>();
    ////        for (Exp e : complex) {
    ////            if (e.containsVarsWithPrefix(MSRP_PREFIX)) {
    ////                a.add(e);
    ////            }
    ////        }
    ////        return ImmutableList.copyOf(a);
    ////    }
    //
    //    public ImmutableList<Exp> getVVpsSubsumingVV(Exp vv) {
    //        ArrayList<Exp> a = new ArrayList<Exp>();
    //        for (Exp e : complex.vvpIt()) {
    //            if (e.vvpSubsumesVV(vv)) {
    //                a.add(e);
    //            }
    //        }
    //        return ImmutableList.copyOf(a);
    //    }
    //
    //    public boolean isAssignedFlip(Lit lit) {
    //        if (simple.isEmpty()) {
    //            return false;
    //        }
    //        return simple.isAssignedFlip(lit);
    //    }
    //
    ////    public Csp simplify(String sLit) throws FailedCspException {
    ////        Lit lit = Lit.parse(sLit).asLit();
    ////        simplify(lit);
    ////        return copy();
    ////    }
    //
    //
    //    public void simplify2(Lit lit) throws FailedCspException {
    //
    //        if (isSolved() || isFailed()) {
    //            return;
    //        }
    //
    //        assert lit.isLit();
    //        Var var = lit.getVr();
    //
    //        ImmutableList<Exp> dirty = getComplexConstraintsContaining(var);
    //
    //        for (Exp before : dirty) {
    //            assert before.containsVar(lit);
    //            if (isFailed()) {
    //                return;
    //            }
    //
    //            boolean removed = complex.remove(before);
    //            assert removed;
    //
    //            Exp after = before.condition(lit);
    //            if (before == after) {
    //                System.err.println("lit[" + lit + "]");
    //                System.err.println("before[" + before + "]");
    //                System.err.println("after[" + after + "]");
    //                throw new IllegalStateException();
    //            }
    //            logSimplified(lit, before, after);
    //
    //            if (after.isFalse()) {
    //                failCspConstraintSimplifiedToFalse(before, lit);
    //                return;
    //            }
    //
    //
    //            if (after.isTrue()) {
    //                //ignore
    //                continue;
    //            }
    //
    //            assert after.isOpen();
    //
    //            addConstraint(after);
    //
    //        }
    //
    //    }
    //
    //
    //    private void logSimplified(Exp lit, Exp before, Exp after) {
    //        if (false && before != after) {
    //            System.err.println("Simplified from lit: " + lit);
    //            System.err.println("\t before: " + before);
    //            System.err.println("\t after:  " + after);
    //        }
    //    }
    //
    //    private void logSimplified(EvalContext ctx, Exp before, Exp after) {
    //        if (false && before != after) {
    //            System.err.println("Simplified from ctx: " + ctx);
    //            System.err.println("\t before: " + before);
    //            System.err.println("\t after:  " + after);
    //        }
    //    }
    //
    //    private void logSimplified(Object ctx, Exp before, Exp after) {
    //        if (true && before != after) {
    //            System.err.println("Simplified from ctx: " + ctx);
    //            System.err.println("\t before: " + before);
    //            System.err.println("\t after:  " + after);
    //        }
    //    }
    //
    //    public void addSeriesModelImps() {
    //        Multimap<Var, Var> map = computeSeriesModelMultiMap();
    //        for (Var seriesVar : map.keySet()) {
    //            Collection<Var> models = map.get(seriesVar);
    //            ArgBuilder b = new ArgBuilder(space, Op.Or);
    //            b.addAll1(models);
    //            Exp exp = b.mk();
    //            addConstraint(exp);
    //        }
    //    }
    //

    //
    //    public ImmutableMap<Var, Var> computeModelToSeriesMap() {
    //        HashMap<Var, Var> b = new HashMap<Var, Var>();
    //        Multimap<SeriesYear, Var> mm = computeSeriesYearToModelMultiMap();
    //        for (SeriesYear seriesYear : mm.keySet()) {
    //            Collection<Var> models = mm.get(seriesYear);
    //            for (Var model : models) {
    //                b.put(model, seriesYear.getSeries());
    //            }
    //        }
    //        return ImmutableMap.copyOf(b);
    //    }
    //
    //    /**
    //     * (series year) => (model1 model2 model3)
    //     */
    //    public Multimap<SeriesYear, Var> computeSeriesYearToModelMultiMap() {
    //        Multimap<SeriesYear, Var> map = HashMultimap.create();
    //        ImmutableSet<Exp> constraints = getModelImpliesSeriesYear();
    //        for (Exp constraint : constraints) {
    //            Var model = null;
    //            ArrayList<SeriesYear> seriesYears = new ArrayList<SeriesYear>();
    //            for (Exp arg : constraint.argIt()) {
    //                if (arg.isModelNegLit()) {
    //                    model = arg.getVr();
    //                } else {
    //                    assert arg.isSeriesYearAnd();
    //                    Var seriesVar = arg.getSeriesVar();
    //                    Var yearVar = arg.getYearVar();
    //                    SeriesYear seriesYear = new SeriesYear(seriesVar, yearVar);
    //                    seriesYears.add(seriesYear);
    //                }
    //            }
    //            assert model != null;
    //            assert seriesYears.size() > 0;
    //
    //            for (SeriesYear seriesYear : seriesYears) {
    //                map.put(seriesYear, model);
    //            }
    //        }
    //        return map;
    //    }
    //
    //
    //    public ImmutableSet<Exp> getModelImpliesSeriesYear() {
    //        assert isOpen();
    //
    //        ImmutableSet.Builder<Exp> b = ImmutableSet.builder();
    //        for (Exp exp : complex) {
    //            if (exp.isModelImpliesSeriesYear()) {
    //                b.add(exp);
    //            }
    //        }
    //
    //        return b.build();
    //    }
    //


    //


    //
    //
    //    final public String serializeCnf() {
    //        Ser a = new Ser();
    //        serializeCnf(a);
    //        return a.toString();
    //    }
    //
    //    final public void serializeCnf(Ser a) {
    //        space.serializeVarMap(a);
    //        a.newLine();
    //
    //        serializeSimpleConstraints(a);
    //
    //        for (Exp e : complex) {
    //            e.serializeCnf(a);
    //            a.newLine();
    //        }
    //
    //    }
    //

    //
    //
    //    /**
    //     * careVarCount of exp with greatest care
    //     *
    //     * @return
    //     * @throws Exception
    //     */
    //    public int getMaxCareVarCount() throws Exception {
    //        if (complex.isEmpty()) return 0;
    //        int maxCareVarCount = 0;
    //        for (Exp e : complex) {
    //            int careVarCount = e.getVarCount();
    //            if (careVarCount > maxCareVarCount) {
    //                maxCareVarCount = careVarCount;
    //            }
    //
    //        }
    //        return maxCareVarCount;
    //    }
    //
    //
    //    public void flattenTopLevelNands() {
    //        if (complex.isEmpty()) return;
    //        DynComplex tmp = complex;
    //        complex = new DynComplex(space);
    //        for (Exp b : tmp) {
    //            Exp a;
    //            if (b.isNand()) {
    //                a = b.asNand().flattenNand();
    //            } else {
    //                a = b;
    //            }
    //            addConstraint(a);
    //        }
    //    }
    //
    //    public void flattenImpsRmps() {
    //        transform(Transformer.FLATTEN_IMPS); //3
    //        transform(Transformer.FLATTEN_RMPS); //4
    //    }
    //
    //
    //    public Set<String> toStringSet() {
    //        HashSet<String> set = new HashSet<String>();
    //        toStringSetSimple(set);
    //        toStringSetComplex(set);
    //        return set;
    //    }
    //
    //    public void toStringSetSimple(Set<String> set) {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public void toStringSetComplex(Set<String> set) {
    //        if (complex.isEmpty()) return;
    //        for (Exp e : complex) {
    //            set.add(e.toString());
    //        }
    //    }
    //
    //    public boolean isReduced() {
    //        boolean rv = getOpenCareVarCount() == space.getVarCount();
    //        if (rv) {
    //            assert simple.isEmpty();
    //        }
    //        return rv;
    //    }
    //
    //
    //    public Xor getLargestXor() {
    //        if (complex.isEmpty()) return null;
    //        Xor largest = null;
    //        for (Exp exp : complex) {
    //            if (exp.isXor()) {
    //                if (largest == null || (exp.getArgCount() > largest.getArgCount())) {
    //                    largest = exp.asXor();
    //                }
    //            }
    //        }
    //        return largest;
    //    }
    //
    //    public boolean checkStable() {
    //        if (isStable()) {
    //            return true;
    //        }
    //        throw new IllegalStateException();
    //    }
    //
    //    public CspStableState getStableState() {
    //        checkStable();
    //        if (isFailed()) {
    //            return CspStableState.FAILED;
    //        } else if (isSolved()) {
    //            return CspStableState.SOLVED;
    //        } else {
    //            return CspStableState.OPEN;
    //        }
    //    }
    //

    //
    //
    //    public void print(int depth) {
    ////        if (isFailed()) {
    ////            print();
    ////        }
    //
    ////        if (ass != null && !ass.isEmpty()) {
    ////            String label = Strings.rpad("Cube:", ' ', 20);
    ////            String line = label + ass.serializeSingleLine();
    ////            prindent(depth, line);
    ////        }
    //
    ////        if (isOpen()) {
    ////            if (complex != null) {
    ////                for (Exp exp : complex) {
    ////                    String label = Strings.rpad("Complex:", ' ', 20);
    ////                    String line = label + exp;
    ////                    prindent(depth, line);
    ////                }
    ////            }
    ////
    ////        }
    //
    //    }
    //
    //    public boolean containsConstraint(Exp e) {
    //        if (e.isSimple()) {
    //            return simple != null && simple.containsLit(e.asLit());
    //        } else if (e.isComplex()) {
    //            return complex != null && complex.contains(e);
    //        } else {
    //            throw new IllegalStateException();
    //        }
    //    }
    //
    //
    //    public static boolean isContainedByAll(List<Csp> nonNullCsps, Exp e) {
    //        for (Csp csp : nonNullCsps) {
    //            if (!csp.containsConstraint(e)) {
    //                return false;
    //            }
    //        }
    //        return true;
    //    }
    //
    //    public void forEachSimpleConstraint(ConstraintHandler h) {
    //        if (simple.isEmpty()) return;
    //        for (Lit lit : simple.litIt()) {
    //            h.onConstraint(lit);
    //        }
    //    }
    //
    //    public void forEachComplexConstraint(ConstraintHandler h) {
    //        if (complex.isEmpty()) return;
    //        for (Exp exp : complex) {
    //            h.onConstraint(exp);
    //        }
    //    }
    //
    //    public void forEachConstraint(ConstraintHandler h) {
    //        forEachSimpleConstraint(h);
    //        forEachComplexConstraint(h);
    //    }
    //
    //    public void removeAll(Csp that) {
    //        assert this.space == that.space;
    //        removeAllSimpleConstraints(that);
    //        removeAllComplexConstraints(that);
    //    }
    //
    //    public void removeAllSimpleConstraints(Csp that) {
    //        assert this.space == that.space;
    //        if (this.simple.isEmpty()) return;
    //        if (that.simple.isEmpty()) return;
    //        simple.removeAll(that.simple);
    //    }
    //
    //    public void removeAllComplexConstraints(Csp that) {
    //        assert this.space == that.space;
    //        DynComplex complex1 = that.complex;
    //        complex.removeAll(complex1);
    //    }
    //
    //    public void removeComplexConstraints(Set<Exp> complexConstraintsToRemove) {
    //        complex.removeAll(complexConstraintsToRemove);
    //    }
    //
    //
    //    public boolean isEmpty() {
    //        return getSize() == 0;
    //    }
    //
    //
    //    public Csp conditionOnAtVars() {
    //        Exp c = space.getAtVarsAsCube();
    //        return condition(c.asCubeExp());
    //    }
    //
    //    public Csp condition(Cube c) {
    //        assignAll(c);
    //        propagate();
    //        return this;
    //    }
    //
    //
    ////    public Csp createEfcModLocal(final Mod mod) {
    ////        final Csp blank = new Csp(space);
    ////        this.forEachConstraint(new ConstraintHandler() {
    ////            @Override
    ////            public void onConstraint(Exp constraint) {
    ////                if (mod.isLocal(constraint)) {
    ////                    blank.addConstraint(constraint);
    ////                }
    ////            }
    ////        });
    ////        return blank;
    ////    }
    //
    //
    //    public boolean isFailed() {
    //        return k.isFailed();
    //    }

    //    public Set<String> getTrueVars() {
    //        return simple.getTrueVarCodes();
    //    }
    //
    //    public Set<String> getFalseVars() {
    //        return simple.getFalseVarCodes();
    //    }
    //
    //    public Set<String> getAssignedVars() {
    //        if (simple.isEmpty()) {
    //            return ImmutableSortedSet.of();
    //        }
    //        return simple.getVarCodes();
    //    }
    //
    ////
    ////    public void printVarReport() {
    ////        System.err.println("Space VarReport :");
    ////        System.err.println("  all:      " + getVarSet().size() + ":" + getVarSet());
    ////        System.err.println("  true:     " + getTrueVars().size() + ":" + getTrueVars());
    ////        System.err.println("  false:    " + getFalseVars().size() + ":" + getFalseVars());
    ////        System.err.println("  care:     " + computeFormulaVars().size() + ":" + computeFormulaVars());
    ////        System.err.println();
    ////    }
    //
    //    public static Csp parse(String clob) {
    //        Space space = new Space();
    //        Sequence<String> lines = space.parseLines(clob);
    //        return new Csp(space, lines);
    //    }
    //
    //    public static Csp parse(CspSample cspSample) {
    //        Path path = cspSample.getPath();
    //        String clob = SpaceJvm.loadResource(path);
    //        return parse(clob);
    //    }
    //
    //
    //    public int getAssignmentCount() {
    //        return getSimpleConstraintCount();
    //    }
    //
    ////    public void serializeLocalCubes(Ser a) {
    ////        Set<Map.Entry<Integer, Assignment>> entries = ass.map.entrySet();
    ////        int size = entries.size();
    ////        int i = 0;
    ////        for (Map.Entry<Integer, Assignment> entry : entries) {
    ////            Exp lit = entryToLit(entry);
    ////            a.append(lit.toString());
    ////            if (i != size - 1) {
    ////                a.append(' ');
    ////            }
    ////            i++;
    ////        }
    ////    }
    //
    //
    //    public int getIffCount() {
    //        return 0;
    //    }
    //
    //    public boolean removeConstraint(Exp constraint) {
    ////        System.err.println("Removing constraint: " + constraint);
    //        if (constraint.isSimple()) {
    //            return removeSimple(constraint);
    //        } else if (constraint.isComplex()) {
    //            return removeComplex(constraint);
    //        } else {
    //            throw new IllegalArgumentException();
    //        }
    //    }
    //
    ////    public void addConstraint(Line line, VarSet allInvAcy) {
    ////        VarSet vars = line.getVars();
    ////
    ////
    ////        for (int varId : vars) {
    ////            assign(varId, true);
    ////        }
    ////
    ////        for (Var vr : line.getVars()) {
    ////
    ////        }
    ////    }
    //

    //
    //    public void enqueueNewVv(final Exp vv) {
    ////        if (qq == null) qq = new ArrayList<Exp>();
    ////        qq.add(vv);
    //    }
    //
    ////    public void enqueueNewVV(Exp vv) {
    ////        System.err.println("enqueueNewVV");
    ////        if (q == null) q = new Queues();
    ////        q.enqueueNewVV(vv);
    ////
    ////    }
    //
    //
    ////    public Exp toDnnf() {
    ////        Set<String> vars = space.getVars();
    ////        return toDnnf();
    ////    }
    //
    //
    ////    Element toElement() {
    ////        SimpleConstraintSet simple;
    ////        complexConstraintSet complex;
    ////
    ////        simple = ass;
    ////        complex = this.complex;
    ////
    ////        return new Element(simple, complex);
    ////    }
    //
    //
    ////    private Cube computeOverlapLits() {
    ////        if (simple.isEmpty()) return null;
    ////        if (complex.isEmpty()) return null;
    ////        VarSet vs2 = getFormulaVars();
    ////        return simple.intersection(vs2);
    ////    }
    //
    //
    ////    public void transform(Transformer t) {
    ////        Formula formula = getFormula();
    ////        DynFormula tmp = fDyn;
    ////        fDyn = new DynFormula(space);
    ////        for (Exp b : tmp) {
    ////            if (isFailed()) {
    ////                return;
    ////            }
    ////            Exp a = t.transform(b);
    ////            addConstraint(a);
    ////        }
    ////    }
    //
    //
    //    //            /**
    //    //                * !and(a b)   => or(!a !b)
    //    //                */
    //    //               private Exp negAnd(Exp formula) {
    //    //                   checkArgument(formula.isNegAnd());
    //    //                   Exp pos = formula.getPos();
    //    //                   List<Exp> pArgs = pos.getArgs();
    //    //
    //    //                   ArgBuilder flippedArgs = new ArgBuilder(Op.Or);
    //    //                   for (Exp arg : pArgs) {
    //    //                       flippedArgs.add(arg.flip());
    //    //                   }
    //    //
    //    //                   return formula.getSpace().mkOr(flippedArgs);
    //    //               }
    //
    //
    //    public Exp mkExp() {
    //        return null;
    //    }
    //
    //
    //    public Exp getGiantOr() {
    //        Exp giant = null;
    //        for (Exp arg : k.getComplexConstraintsIt()) {
    //            int currentArgCount = (giant == null ? 0 : giant.getArgCount());
    //            if (arg.isOr() && arg.getArgCount() > currentArgCount) {
    //                giant = arg;
    //            }
    //        }
    //        return giant;
    //    }
    //
    //    public DynComplex stripSeriesVarsFromYsmAnds() {
    //        assert !k.isDirty();
    //        assert isOpen();
    //        DynComplex stripped = new DynComplex(k.getSpace());
    //        DynComplex cc = k.getComplexDyn();
    //
    //
    //        if (cc != null) {
    //            for (Exp e : cc.getArgIt()) {
    //                Exp ee = e.stripSeriesVarsFromYsmAnds();
    //                stripped.add(ee);
    //            }
    //        }
    //
    //
    //    }
    //
    //    public void stripSeriesVarsFromModelImpliesSeriesYears() {
    //        assert !isDirty();
    //        assert isOpen();
    ////        System.err.println("<ModelImpliesSeriesYears>");
    //        DynComplex stripped = new DynComplex(space);
    //        for (Exp e : complex) {
    //            Exp ee = e.stripSeriesVarsFromModelImpliesSeriesYears();
    //            stripped.add(ee);
    //        }
    //        this.complex = stripped;
    ////        System.err.println("</ModelImpliesSeriesYears>");
    //    }
    //
    //
    //    public void replaceSeriesWithModels() {
    //        assert !isDirty();
    //        assert isOpen();
    //
    //        Multimap<SeriesYear, Var> map = computeSeriesYearToModelMultiMap();
    //
    ////        System.err.println("<replaceSeriesWithModels>");
    //        DynComplex stripped = new DynComplex(space);
    //        for (Exp e : complex) {
    //            if (e.isModelImpliesSeriesYear()) {
    //                stripped.add(e);
    //            } else if (e.isSeriesXor()) {
    //                stripped.add(e);
    //            } else if (e.isOrContainsSeriesYearAndPlus()) {
    ////                System.err.println("Before: " + e);
    //                Exp ee = e.replaceSeriesWithModels(map);
    ////                System.err.println("After: " + ee);
    //                stripped.add(ee);
    //            } else {
    //                stripped.add(e);
    //            }
    //        }
    //        this.complex = stripped;
    ////        System.err.println("</replaceSeriesWithModels>");
    //    }
    //
    //
    //    public void stripSeriesYearsFromAcyImpliesModelYears() {
    //        assert !isDirty();
    //        assert isOpen();
    //        System.err.println("<AcyImpliesModelYears>");
    //        DynComplex stripped = new DynComplex(space);
    //        for (Exp e : complex) {
    //            Exp ee = e.stripSeriesYearsFromAcyImpliesModelYears();
    //            stripped.add(ee);
    //        }
    //        this.complex = stripped;
    //        System.err.println("</AcyImpliesModelYears>");
    //    }
    //
    //
    //    public void removeCoreFactoryConstraints() {
    //        assert isClean();
    //        assert isOpen();
    //        VarSet coreVars = space.getCoreVars();
    //        DynComplex keep = new DynComplex(space);
    //        for (Exp exp : complex) {
    //            boolean isCore = coreVars.containsAll(exp.getVars());
    //            if (!isCore) {
    //                keep.add(exp);
    //            }
    //        }
    //        complex = keep;
    //    }
    //
    //    public void removeAllFactoryConstraints() {
    //        assert isClean();
    //        assert isOpen();
    //        complex = new DynComplex(space);
    //        simple = new DynCube(space);
    //    }
    //
    //
    //    int getPrefixOccurrenceCountLight(String sPrefix) {
    //        return space.getPrefixOccurrenceCountLight(sPrefix);
    //    }
    //
    //    public Xor getBestXorSplit() {
    //        return XorCounts.getMax(this);
    //    }
    //
    //    public Csp printXorStats() {
    //        XorCounts counts = XorCounts.count(this);
    //        counts.print();
    //        return this;
    //    }
    //
    //
    //    public class VVsConditionPropagator extends Propagator {
    //
    //        private final Collection<Exp> subsumedVvs;
    //        private final Exp vvp;
    //        private Exp out;
    //
    //
    //        public VVsConditionPropagator(Collection<Exp> subsumedVvs, Exp vvp) {
    //            this.subsumedVvs = subsumedVvs;
    //            this.vvp = vvp;
    //        }
    //
    //
    //        @Override
    //        public void execute(Csp csp) {
    //            Exp nnf = vvp.toNnf();
    //            Exp out = nnf;
    //            for (Exp subsumedVv : subsumedVvs) {
    //                out = out.conditionVV(subsumedVv);
    //            }
    //            if (out != vvp) {
    //                csp.removeConstraint(vvp);
    //                csp.addConstraint(out);
    //            }
    //            this.out = out;
    //        }
    //
    //        public Exp getOut() {
    //            return out;
    //        }
    //    }
    //
    ////    public class NewVV extends Propagator {
    ////        private final Exp vv;
    ////
    ////        public NewVV(Exp vv) {
    ////            assert vv.isVV();
    ////            this.vv = vv;
    ////        }
    ////
    ////        @Override
    ////        public void enqueue(Csp csp) {
    ////            csp.qVV.add(this);
    ////        }
    ////
    ////        @Override
    ////        public void execute(Csp csp) {
    ////            csp.simplifyForNewVV(vv);
    ////        }
    ////    }
    //
    ////    public void addConstraint(Imp imp) {
    ////        Exp arg1 = imp.getArg1();
    ////        Exp arg2 = imp.getArg2();
    ////        addBinaryOr(arg1.flip(), arg2);
    ////    }
    ////
    ////    public void addConstraint(Rmp rmp) {
    ////        Exp arg1 = rmp.getArg1();
    ////        Exp arg2 = rmp.getArg2();
    ////        addBinaryOr(arg1, arg2.flip());
    ////    }
    ////
    ////    public void addConstraint(Iff iff) {
    ////        Exp arg1 = iff.getArg1();
    ////        Exp arg2 = iff.getArg2();
    ////        addImp(arg1, arg2);
    ////        addImp(arg2, arg1);
    ////    }
    ////
    ////    public void addImp(Exp arg1, Exp arg2) {
    ////        addBinaryOr(arg1.flip(), arg2);
    ////    }
    ////
    ////    public void addRmp(Exp arg1, Exp arg2) {
    ////        addBinaryOr(arg1, arg2.flip());
    ////    }
    ////
    ////    public void addIff(Exp arg1, Exp arg2) {
    ////        if (arg1 == arg2) return;
    ////
    ////
    ////        if (arg1.isFalse() && arg2.isFalse()) return;
    ////        if (arg1.isTrue() && arg2.isTrue()) return;
    ////
    ////        if (arg1.isFalse() && arg2.isTrue()) {
    ////            failCspFalseConstraintAdded();
    ////            return;
    ////        }
    ////
    ////        if (arg1.isTrue() && arg2.isFalse()) {
    ////            failCspFalseConstraintAdded();
    ////            return;
    ////        }
    ////
    ////        if (arg1.isTrue() && arg2.isOpen()) {
    ////            addConstraint(arg2);
    ////            return;
    ////        }
    ////
    ////
    ////        if (arg1.isOpen() && arg2.isTrue()) {
    ////            addConstraint(arg1);
    ////            return;
    ////        }
    ////
    ////        if (arg1.isFalse() && arg2.isOpen()) {
    ////            addConstraint(arg2.flip());
    ////            return;
    ////        }
    ////
    ////        if (arg1.isOpen() && arg2.isFalse()) {
    ////            addConstraint(arg1.flip());
    ////            return;
    ////        }
    ////
    ////        addImp(arg1, arg2);
    ////        addImp(arg2, arg1);
    ////
    ////    }
    //
    ////    public void addConstraint(ArgBuilder b) {
    ////
    ////        if (b.isTrue()) {
    ////            return;
    ////        }
    ////
    ////        if (b.isFalse()) {
    ////            failCspFalseConstraintAdded();
    ////            return;
    ////        }
    ////
    ////        if (b.isUnary()) {
    ////            Exp first = b.first();
    ////            addConstraint(first);
    ////            return;
    ////        }
    ////
    ////        if (b.isBinaryOr()) {
    ////            Iterator<Exp> it = b.iterator();
    ////            Exp arg1 = it.next();
    ////            Exp arg2 = it.next();
    ////            addBinaryOr(arg1, arg2);
    ////            return;
    ////        }
    ////
    ////        if (b.isNaryOr()) {
    ////            addNaryOr(b);
    ////            return;
    ////        }
    ////
    ////        if (b.isAndLike()) {
    ////            for (Exp exp : b) {
    ////                addConstraint(exp);
    ////            }
    ////        }
    ////
    ////        fail();
    ////    }
    //
    ////    public void addNaryOr(ArgBuilder b) {
    ////        assert b.isNaryOr();
    ////
    ////    }
    //
    //
    ////    public void addBinaryOr(Exp arg1, Exp arg2) {
    ////
    ////        if (arg1.isTrue() || arg2.isTrue()) {
    ////            return;
    ////        }
    ////
    ////        if (arg1 == arg2) {
    ////            addConstraint(arg1);
    ////            return;
    ////        }
    ////
    ////        if (arg1 == arg2.flip()) {
    ////            return;
    ////        }
    ////
    ////        if (arg1.isFalse()) {
    ////            addConstraint(arg2);
    ////        }
    ////        if (arg2.isFalse()) {
    ////            addConstraint(arg1);
    ////        }
    ////
    ////        if (arg1.isAnd()) {
    ////            for (Exp a1 : arg1.getArgs()) {
    ////                addBinaryOr(arg2, a1);
    ////            }
    ////            return;
    ////        }
    ////
    ////        if (arg2.isAnd()) {
    ////            for (Exp a2 : arg2.getArgs()) {
    ////                addBinaryOr(arg1, a2);
    ////            }
    ////            return;
    ////        }
    ////
    ////        ArgBuilder b = new ArgBuilder(space, Op.Or);
    ////        b.add(arg1);
    ////        b.add(arg2);
    ////        Exp or = b.mk();
    ////
    ////        k.addConstraint(or);
    ////        k._addComplex(or);
    ////
    ////    }
    //
    //
    ////    //vars have already been added - this method skips vars line
    ////    public void addConstraints(String[] lines) {
    ////        Iterable<String> it = Its.itForArray(lines);
    ////        addConstraints(it);
    ////    }
    //
    //
    ////
    ////    public void addConstraints(Sequence<Exp> constraints, Cube cube) {
    ////        Function1<Exp, Exp> ff = CspK.conditionFunctionKotlin(cube);
    ////        k.addConstraints2(constraints, ff);
    ////    }
    //
    //
    ////    public void addConstraints(Sequence<Exp> constraints, Function<Exp, Exp> ff) {
    ////        Function1<Exp, Exp> kFF = Fn.toKFunction(ff);
    ////        k.addConstraints2(constraints, kFF);
    ////    }
    ////
    ////    public void addConstraints(Sequence<Exp> constraints, Function1<Exp, Exp> ff) {
    ////        k.addConstraints2(constraints, ff);
    ////    }
    //
    //    public void addXImpliesAnd(Exp x, And and) {
    //        for (Exp arg : and.getArgs()) {
    //            Exp binaryOr = space.mkOr(x.flip(), arg);
    //            addConstraint(binaryOr);
    //        }
    //    }
    //
    //    public void addXImpliesNonAnd(Exp x, Exp nonAnd) {
    //        assert !nonAnd.isAnd();
    //        Exp binaryOr = space.mkOr(x.flip(), nonAnd);
    //        addConstraint(binaryOr);
    //    }
    //
    //
    //    public void simplifyForNewVV(Exp newVv) {
    //        if (!isOpen()) {
    //            return;
    //        }
    //
    //        VarSet newVvVars = newVv.getVars();
    //
    //        for (Exp e : complex) {
    //            if (e == newVv) continue;
    //            if (!e.isVv()) continue;
    //            VarSet eVars = e.getVars();
    //            if (eVars.containsAllVars(newVvVars)) {
    //
    ////                System.err.println(e);
    ////                System.err.println(newVv);
    ////                System.err.println();
    //            }
    //        }
    //
    //    }
    //
    //    public static int boom; //3858
    //
    //    public void maybeProcessVVs() {
    //
    //        if (!isOpen()) return;
    //
    //
    //        ArrayList<Exp> vvs = new ArrayList<Exp>();
    //        ArrayList<Exp> vvps = new ArrayList<Exp>();
    //
    //        for (Exp e : complex) {
    //            if (e.isVv()) {
    //                vvs.add(e);
    //            } else {
    //                assert e.isVVPlus();
    //                vvps.add(e);
    //            }
    //        }
    //
    //        if (vvs.isEmpty()) return;
    //        if (vvps.isEmpty()) return;
    //
    //        for (Exp vv : vvs) {
    //            VarSet vvVars = vv.getVars();
    //            for (Exp vvp : vvps) {
    //                VarSet vvpVars = vvp.getVars();
    //                if (vvpVars.containsAllVars(vvVars)) {
    //                    boom++;
    //                }
    //
    //            }
    //        }
    //    }
    //
    //
    //    public void toNnfOld() {
    //        toBnf();
    //        bnfToNnf();
    //    }
    //
    //    public void toNnfKeepXors2() {
    //
    //        propagate();
    //
    //        DynComplex f = new DynComplex(space);
    //
    //        for (Exp e : complex) {
    //            if (skipNnf(e)) {
    //                f.add(e);
    //            } else {
    //                f.add(e.toNnf());
    //            }
    //        }
    //
    //        complex = f;
    //
    //    }
    //
    //
    //    private boolean skipNnf(Exp constraint) {
    //        return constraint.isXorOrContainsXor();
    //    }
    //
    //    public void printVvStats(String label) throws Exception {
    //        System.err.println("vv[" + label + "]:");
    //        List<Exp> vvs = getVVConstraints();
    //        List<Exp> vvpsSeriesAndYear = getVvpsWithSeriesAndYear();
    //        List<Exp> vvpsSeriesAndModel = getVvpsWithSeriesAndModel();
    //        System.err.println("  vvs [" + vvs.size() + "]");
    //        System.err.println("  vvpsSeriesAndYear [" + vvpsSeriesAndYear.size() + "]");
    //        System.err.println("  vvpsSeriesAndModel[" + vvpsSeriesAndModel.size() + "]");
    //    }
    //
    //    public void printVvpsWithSeriesAndYear() throws Exception {
    //        List<Exp> vvps = getVvpsWithSeriesAndYear();
    //        if (vvps.size() == 0) {
    //            System.err.println("no series and year vvps");
    //            return;
    //        }
    //        for (Exp vvp : vvps) {
    //            System.err.println("vvp: " + vvp);
    //        }
    //    }
    //
    //    public List<Exp> getVvpsWithSeriesAndYear() {
    //        if (complex.isEmpty()) return ImmutableList.of();
    //        ImmutableList.Builder<Exp> b = ImmutableList.builder();
    //        for (Exp exp : complex) {
    //            if (exp.isVVPlusWithSeriesAndYear()) {
    //                b.add(exp);
    //            }
    //        }
    //        return b.build();
    //    }
    //
    //    public List<Exp> getVvpsWithSeriesAndModel() {
    //        if (complex.isEmpty()) return ImmutableList.of();
    //        ImmutableList.Builder<Exp> b = ImmutableList.builder();
    //        for (Exp exp : complex) {
    //            if (exp.isVVPlusWithSeriesAndModel()) {
    //                b.add(exp);
    //            }
    //        }
    //        return b.build();
    //    }
    //
    //
    //    public boolean anyVvpsWithSeriesAndYear() {
    //        if (complex.isEmpty()) return false;
    //        for (Exp exp : complex) {
    //            if (exp.isXorOrContainsXor()) continue;
    //            if (exp.isIffOrContainsIff()) continue;
    //            if (exp.isVVPlusWithSeriesAndYear()) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
    //
    //    public boolean anyVvpsWithSeriesAndModel() {
    //        if (complex.isEmpty()) return false;
    //        for (Exp exp : complex) {
    //            if (exp.isXorOrContainsXor()) continue;
    //            if (exp.isIffOrContainsIff()) continue;
    //            if (exp.isVVPlusWithSeriesAndModel()) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
    //
    //
    //    public static List<Exp> findSubsumedVVs(Exp vvp, Iterable<Exp> vvs) {
    //        ArrayList<Exp> subsumedVVs = new ArrayList<Exp>();
    //        for (Exp vv : vvs) {
    //            if (vvp.vvpSubsumesVV(vv)) {
    //                subsumedVVs.add(vv);
    //            }
    //        }
    //        return subsumedVVs;
    //
    //    }
    //
    ////    public void simplifyBasedOnVvsUntilStable() {
    ////        if (complex.isEmpty() || complex.isEmpty()) {
    ////            complex = null;
    ////            return;
    ////        }
    ////
    ////        int iteration = 0;
    ////        while (!isFailed()) {
    ////            iteration++;
    ////            System.err.println("*** vv iteration [" + iteration + "] ***");
    ////            simplifyBasedOnVvs();
    ////            boolean vvps = anyVvpsWithSeriesAndYear();
    ////            if (!vvps) break;
    ////        }
    ////
    ////    }
    //

    //
    //
    //    public void printComplexConstraintsSortedByStrLen(int n) {
    //        ImmutableList<Exp> exps = sortComplexConstraintsByStrLen();
    //        int i = 0;
    //        for (Exp e : exps) {
    //            if (i > n) break;
    //            System.err.println(e);
    //            i++;
    //        }
    //    }
    //



    //
    ////    public Formula getFormula() {
    ////        propagate();
    ////        assert isStable();
    ////        assert !isFailed();
    ////        assert !isSolved();
    ////        assert complex != null;
    ////        assert !complex.isEmpty();
    ////        return complex.mkFormula().asFormula();
    ////    }
    //

    //
    //
    //    public Space getSpace() {
    //        return space;
    //    }
    //
    //
    //    public VarSet getInvAcyVars() {
    //        space.checkVarInfo();
    //
    //        VarMeta varMeta = space.getVarMeta();
    //        VarSet formulaVars = getFormula().getVars();
    //        VarSetBuilder b = space.newMutableVarSet();
    //        for (Var var : formulaVars.varIt()) {
    //            if (varMeta.isInvAcyVar(var)) {
    //                b.addVar(var);
    //            }
    //        }
    //        return b.build();
    //    }
    //
    //    public Csp atRefine() {
    //        return conditionOnAtVars();
    //    }
    //
    //    public void conditionOutAtVars() {
    //        maybeAddAlwaysTrueVars();
    //        propagate();
    //    }
    //

    //
    //    public static enum ProposeResult {
    //        TT, //Completely Open
    //        TF, //Implied True
    //        FT, //Implied False
    //        FF  //Cannot be assigned True or False - Csp Failure
    //    }
    //
    //
    //    public boolean isOpen() {
    //        if (isFailed()) {
    //            return false;
    //        }
    //
    //        if (isSolved()) {
    //            return false;
    //        }
    //        return true;
    //    }
    //
    //    public boolean isConstant() {
    //        return isFailed() || isSolved();
    //    }
    //
    //

    //
    //    public Exp getModelXorDeep() {
    //        return getXorDeep(MDL_PREFIX);
    //    }
    //
    //    public Exp getXorDeep(String prefix) {
    //        for (Exp exp : getComplex1()) {
    //            if (exp == null) {
    //                continue;
    //            }
    //            if (exp.isXorOrContainsXor(prefix)) {
    //                return exp;
    //            }
    //        }
    //        return null;
    //    }
    //
    //    public List<Var> getVarList() {
    //        return space.getVarList();
    //    }
    //
    //
    //    public LitPair createLitPair(int lit1, int lit2) {
    //        Exp arg1 = getLit(lit1);
    //        Exp arg2 = getLit(lit2);
    //        return new LitPair(arg1, arg2);
    //    }
    //
    //    public Exp getLit(String signedVarCode) {
    //        return space.mkLit(signedVarCode);
    //    }
    //
    //    public Exp getLit(int lit) {
    //        int varId = Head.getVarId(lit);
    //        boolean sign = Head.getSign(lit);
    //        return space.getLit(varId, sign);
    //    }
    //
    //
    ////    public Set<Exp> getTrueVars() {
    ////        HashSet<Exp> set = new HashSet<Exp>();
    ////
    ////        Set<Map.Entry<Exp, Boolean>> entries = simpleConstraints.entrySet();
    ////        for (Map.Entry<Exp, Boolean> entry : entries) {
    ////            if (entry.getValue()) {
    ////                set.add(entry.getKey());
    ////            }
    ////        }
    ////
    ////        return set;
    ////    }
    ////
    ////    public Set<Exp> getFalseVars() {
    ////        HashSet<Exp> set = new HashSet<Exp>();
    ////
    ////        Set<Map.Entry<Exp, Boolean>> entries = simpleConstraints.entrySet();
    ////        for (Map.Entry<Exp, Boolean> entry : entries) {
    ////            if (!entry.getValue()) {
    ////                set.add(entry.getKey());
    ////            }
    ////        }
    ////
    ////        return set;
    ////    }
    //
    //
    ////    public SpaceCsp refine(int varId, boolean sign) {
    ////        SpaceCsp copy = copy();
    ////        try {
    ////            Exp vr = copy.getVr(varId);
    ////            if (sign) {
    ////                copy.addConstraint(vr);
    ////            } else {
    ////                copy.addConstraint(vr.flip());
    ////            }
    ////            copy.propagate();
    ////            return copy;
    ////        } catch (FailedCspException e) {
    ////            return null;
    ////        }
    ////    }
    //
    //    public boolean isAssigned(Var var) {
    //        return simple.isAssigned(var);
    //    }
    //
    //    public boolean isOpen(Var vr) {
    //        return !isAssigned(vr);
    //    }
    //
    //    @Override
    //    public boolean isTrue(Var vr) {
    //        return simple.isTrue(vr);
    //    }
    //
    //    @Override
    //    public boolean isFalse(Var vr) {
    //        return simple.isFalse(vr);
    //    }
    //
    //
    //    @Override
    //    public String idToCode(int varId) {
    //        Var var = getVar(varId);
    //        return var.getVarCode();
    //    }
    //
    //    @Override
    //    public int codeToId(String code) {
    //        Var var = getVar(code);
    //        return var.getVarId();
    //    }
    //
    //
    ////    public void computeBB() {
    ////            IntList careVars = getCareVars();
    ////            for (int varId : careVars) {
    ////                Var vr = getVr(varId).asVar();
    ////                System.err.println("spExamining Var: " + vr);
    ////                OpenVarState openVarState = validateOpenVar(vr);
    ////                System.err.println("\t " + openVarState);
    ////            }
    ////        }
    ////
    ////    public OpenVarState validateOpenVar(Var vr) {
    ////           assert isOpen(vr);
    ////
    ////           System.err.println("spValidateOpenVar[" + vr + "]");
    ////           boolean d = isDontCare(vr);
    ////           System.err.println("\t spDC: " + d);
    ////
    ////           if (d) {
    ////               return OpenVarState.DontCare;
    ////           }
    ////
    ////           boolean t = proposeTrue(vr.getVarId());
    ////           System.err.println("\t spProposeTrue:" + t);
    ////
    ////           boolean f = proposeFalse(vr.getVarId());
    ////           System.err.println("\t spProposeFalse:" + f);
    ////
    ////
    ////           if (!t && f) {
    ////               //vr must be assigned false
    ////               return OpenVarState.False;
    ////           } else if (t && !f) {
    ////               //vr must be assigned true
    ////               return OpenVarState.True;
    ////           } else if (t && f) {
    ////               //vr must be assigned false
    ////               return OpenVarState.CareVar;
    ////           } else {
    ////               throw new IllegalStateException();
    ////           }
    ////
    ////
    ////       }
    ////
    //
    //
    //    public List<Exp> createMasterList() {
    //        ImmutableList.Builder<Exp> a = ImmutableList.builder();
    //        if (complex != null) {
    //            a.addAll(complex);
    //        }
    //        return a.build();
    //    }
    //
    //
    //    public Iterator<Exp> complexIterator() {
    //        if (complex.isEmpty()) return It.INSTANCE.emptyIter();
    //        return complex.iterator();
    //    }
    //
    //    public DynComplex getComplex1() {
    //        return complex;
    //    }
    //
    //
    //    public List<Exp> getComplexSeq() {
    //        return createMasterList();
    //    }
    //
    //    public List<Exp> getAllConstraints() {
    //        final ImmutableList.Builder<Exp> b = ImmutableList.builder();
    //        if (complex != null) {
    //            b.addAll(complex);
    //        }
    //
    //        if (simple != null) {
    //            b.addAll(simple.litIt());
    //        }
    //
    //        return b.build();
    //
    //    }
    //
    //    public Set<Exp> getAllNonXorConstraints() {
    //        final ImmutableSet.Builder<Exp> b = ImmutableSet.builder();
    //        if (complex != null) {
    //            for (Exp exp : complex) {
    //                if (!exp.isXor()) {
    //                    b.add(exp);
    //                }
    //            }
    //        }
    //        if (simple != null) {
    //            b.addAll(simple.litIt());
    //        }
    //        return b.build();
    //    }
    //
    //    public Set<PosOp> getOps() {
    //        HashSet<PosOp> s = new HashSet<PosOp>();
    //        List<Exp> allConstraints = getComplexSeq();
    //        for (Exp c : allConstraints) {
    //            PosOp posOp = c.getPosOp();
    //            s.add(posOp);
    //        }
    //
    //        return s;
    //
    //    }
    //
    //
    //    public boolean isSatLame() {
    //        propagate();
    //        if (isFailed()) {
    //            return false;
    //        }
    //        return true;
    //    }
    //
    //
    //    public Set<Var> getOpenVars() {
    //        Sets.SetView<Var> diff = Sets.difference(getVarSet(), getAssignedVars());
    //        return ImmutableSortedSet.copyOf(diff);
    //    }
    //
    //
    //    public int getMaxAndSize() {
    //        Exp exp = getExpWithLargestAnd();
    //        if (exp == null) return 0;
    //        Exp biggestAnd = exp.getAndWithHighestLitArgCount();
    //        if (biggestAnd == null) return 0;
    //        return biggestAnd.getAndLitArgCount();
    //    }
    //
    //
    //    public void printComplexityReport() {
    //        if (complex.isEmpty()) return;
    //        int maxAndSize = getMaxAndSize();
    //
    //        ArrayList<Exp> expsWithMaxAnd = new ArrayList<Exp>();
    //        for (Exp exp : complex) {
    //            And and = exp.getAndWithHighestLitArgCount();
    //            if (and != null && and.getAndLitArgCount() == maxAndSize) {
    //                expsWithMaxAnd.add(exp);
    //            }
    //        }
    //        System.err.println("  expsWithMaxAnd:" + expsWithMaxAnd.size() + ":");
    //        for (Exp exp : expsWithMaxAnd) {
    //            System.err.println("  " + exp);
    //            System.err.println("    " + exp.getAndWithHighestLitArgCount());
    //        }
    //
    //
    //    }
    //
    //
    //    public Set<String> getModelCodesForSeries(String seriesName) throws Exception {
    //        if (!seriesName.startsWith("SER")) {
    //            seriesName = "SER_" + seriesName;
    //        }
    //        Formula f = refineFormulaOnly(seriesName);
    //        Exp n = f.toDnnf();
    //        Exp nn = n.copyToOtherSpace();
    //
    //        VarSet outVars = nn.getSpace().getVars(Prefix.MDL);
    //
    //        Exp projection = nn.project(outVars);
    //        Set<Cube> cubes = projection.getCubesSmooth();
    //
    //        HashSet<String> s = new HashSet<String>();
    //        for (Cube cube : cubes) {
    //            Var firstTrueVar = cube.getTrueVars().getFirstVar();
    //            s.add(firstTrueVar.getVarCode());
    //        }
    //        return s;
    //    }
    //
    //    public Set<String> getAlwaysTrueVars() {
    //        ImmutableSet.Builder<String> b = ImmutableSet.builder();
    //        for (String atVarCode : Space.alwaysTrueVars1) {
    //            if (space.containsVarCode(atVarCode)) {
    //                b.add(atVarCode);
    //            }
    //        }
    //        return b.build();
    //    }
    //
    //
    //    public void conditionOutUnVtcVars() {
    //        VarSet unVtcVars = space.getUnVtcVars();
    //        for (Var unVtcVar : unVtcVars) {
    //            addConstraint(unVtcVar.mkNegLit());
    //        }
    //        propagate();
    //    }
    //
    //    /**
    //     * Combines Space.getCoreXorsFromSpace() with Csp.getXorConstraints
    //     */
    //    public Set<Exp> getAllXorConstraints() {
    //        HashSet<Exp> xors = new HashSet<Exp>();
    //        Collection<Exp> xors1 = getSpace().getCoreXorsFromSpace();
    //        Collection<Exp> xors2 = getXorConstraints();
    //        xors.addAll(xors1);
    //        xors.addAll(xors2);
    //        return ImmutableSet.copyOf(xors);
    //    }
    //
    //
    //    public boolean addConstraint(Exp exp, Condition condition) {
    //        return k.addConstraint(exp, condition);
    //    }
    //
    //    public boolean addConstraint(Exp exp) {
    //        return k.addConstraint(exp);
    //    }
    //
    //    public boolean addConstraintClob(String clob, Condition condition) {
    //        return k.addConstraintsClob(clob, condition);
    //    }
    //
    //    public boolean addConstraintLines(Sequence<String> lines, Condition condition) {
    //        return k.addConstraintLines(lines, condition);
    //    }
    //
    //    public boolean addConstraints(Sequence<Exp> constraints, Condition condition) {
    //        return k.addConstraints(constraints, condition);
    //    }
    //
    //    public boolean addConstraints(DynComplex constraints, Condition condition) {
    //        return k.addConstraints(constraints, condition);
    //    }
    //
    //    private static Logger log = Logger.getLogger(Csp.class.getName());
    //
    //
}