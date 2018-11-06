package com.tms.csp.ast.formula;

import com.google.common.collect.ArrayListMultimap;
import com.tms.csp.Structure;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.*;
import com.tms.csp.ssutil.Console;
import com.tms.csp.util.UnionFind;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;

import javax.annotation.Nullable;
import java.util.*;

//import com.tms.csp.ast.*;

/**
 * represents a set of complex fact
 * <p>
 * A formula is an and with all complex args
 * <p>
 * if may be an fcc, or not
 */
public class Formula extends And implements FConstraintSet {

    //cache computed values

    /*
     fcc = null:  fcc and complexFccs have not been computed yet
     fcc != null: fcc and complexFccs have been computed
        fcc = true:  means this *is* an fcc,
            complexFccs set to null
        fcc = false: means this is not an fcc, complexFccs should be non-null, size >= 2
     */

    public Boolean fcc;
    private Exp complexFccs;

    private FVars fVars;

    private Exp dnnf;
    private DynCube bb;

    public KFormula k;
    public int depth;

    public Formula(Space space, int expId, Exp[] args, Boolean fcc) {
        super(space, expId, args);
        this.fcc = fcc;
    }

//    public Formula(Space space, int expId, Exp[] fixedArgs) {
//        this(space, expId, fixedArgs, null);
//    }

    public int getDepth() {
        return depth;
    }

//    public KFormula mkK() {
//        if (k == null) {
//            k = new KFormula(this);
//        }
//        return k;
//    }


    @Override
    public boolean computeIsSat() {
        Xor xor = getBestXorSplit();
        if (xor != null) {
            XorSplit split = new XorSplit(this, xor);
            return split.isSat();
        } else {
            Var var = decide();
            DecisionSplit split = new DecisionSplit(this, var);
            return split.isSat();
        }
    }


//    public void transform(Transformer t) {
//        DynFormula d = new DynFormula();
//        Formula tmp = this;
//        for (Exp b : tmp) {
//            if (isFailed()) {
//                return;
//            }
//            Exp a = t.transform(b);
//            addConstraint(a);
//        }
//    }


    @Override
    public Formula asFormula() {
        return this;
    }

    @Override
    public Exp toDnnf() {
        if (dnnf == null) {
            dnnf = toDnnfInternal1();
        }
        assert dnnf != null;
        assert dnnf.isDnnf() : dnnf;
//        assert dnnf.checkDnnf();
        return dnnf;
    }

    private Exp toDnnfDeepLearnedLits(DynCube learnedLits) {
        assert learnedLits != null && learnedLits.getSize() > 0;
        Exp conditioned = this.condition(learnedLits);
        assert conditioned.isVarDisjoint(learnedLits.getVars());

        if (conditioned.isConstantTrue()) {
            return learnedLits.mkExp();
        } else if (conditioned.isConstantFalse()) {
            return mkFalse();
        } else if (conditioned.isLit()) {
            ArgBuilder b = new ArgBuilder(_space, Op.DAnd);
            b.addExpIt(learnedLits.argIt());
            b.addExp(conditioned.asLit());
            return b.mk();
        } else if (conditioned.isAnd()) {
            if (conditioned.isFormula()) {
                assert conditioned.isAllComplex() : conditioned;
                Exp dnnf = conditioned.toDnnf();
                ArgBuilder b = new ArgBuilder(_space, Op.DAnd);
                b.addExpIt(learnedLits.litIt());
                b.addExp(dnnf);
                return b.mk();
            } else if (conditioned.isAllLits()) {
                ArgBuilder b = new ArgBuilder(_space, Op.DAnd);
                b.addExpIt(learnedLits.litIt());
                b.addExpIt(conditioned.litItFromExpArray());
                return b.mk();
            } else {
                //mixed

                And mixed = conditioned.asAnd();
                Exp dnnf = mixed.toDnnf();

                ArgBuilder b1 = new ArgBuilder(_space, Op.DAnd);
                b1.addExpIt(learnedLits.litIt());
                b1.addExp(dnnf);

                return b1.mk();

            }

        } else if (conditioned.isOr()) {
            ArgBuilder b = new ArgBuilder(_space, Op.DAnd);
            b.addExpIt(learnedLits.litIt());
            b.addExp(conditioned.toDnnf());
            return b.mk();
        } else {
            throw new UnsupportedOperationException(conditioned.toString());
        }


    }

    @Nullable
    private Exp toDnnfTopXorSplit() {
        Xor xor = getTopXorSplit();
        if (xor != null) {
            return xorSplitToDnnf(xor);
        } else {
            return null;
        }
    }


    @Nullable
    private Exp toDnnfBestXorSplit() {
        Xor xor = getBestXorSplit();
        if (xor != null) {
            return xorSplitToDnnf(xor);
        } else {
            return null;
        }
    }

    @Nullable
    private Exp toDnnfAfterDeeperInference() {
        if (true) return null;

        DynCube learnedLits = tryForDeeperInference(); //optional
        if (learnedLits != null && !learnedLits.isEmpty()) {
            return toDnnfDeepLearnedLits(learnedLits);
        } else {
            return null;
        }

    }


    private Exp toDnnfInternal1() {

        Exp dTopXor = toDnnfTopXorSplit();
        if (dTopXor != null) return dTopXor;

        if (fcc == null) {
            assert complexFccs == null;

            Exp fccs = getComplexFccs(); //returns null if *this* is an fcc
            if (fccs == null) {
                fcc = true;
                complexFccs = null;
                Exp retVal = fccSplitDnnf();
                assert retVal != null;
                return retVal;
            } else {
                fcc = false;
                complexFccs = fccs;
                Exp retVal = fccs.toDnnf();
                assert retVal != null : fccs.getClass() + " returned null from toDnnf()";
                return retVal;
            }

        } else if (fcc.equals(Boolean.TRUE)) {

            Exp dBestXor = toDnnfBestXorSplit();
            if (dBestXor != null) return dBestXor;

            Var var = decide();
            return decisionSplit(var);
        } else if (fcc.equals(Boolean.FALSE)) {
            System.err.println("complexFccs: " + complexFccs);
            assert complexFccs != null;  //boom
            Exp retVal = complexFccs.toDnnf();
            assert retVal != null;
            return retVal;
        } else {
            throw new IllegalStateException();
        }

    }


    public DynCube tryForDeeperInference() {
        int maxVarsToTry = 10;
        FVars fVars = getFVars();
        int i = 0;
        List<FVar> sortedList = fVars.getSortedFVarList();
        int L;
        if (sortedList.size() < maxVarsToTry) {
            L = sortedList.size();
        } else {
            L = maxVarsToTry;
        }

        DynCube learnedLits = null;
        while (i < L) {
            FVar var = sortedList.get(i);
            Lit lit = proposeBothWaysLite(var.vr);
            if (lit != null) {
                if (learnedLits == null) {
                    learnedLits = new DynCube(getSpace());
                }
                learnedLits.assign(lit);
//                System.err.println("Learned lit[" + lit + "]");
            }
            i++;
        }


        return learnedLits;

    }


    public Exp getComplexFccs() {
        if (fcc == null) {
            complexFccs = computeComplexFccs();
        }
        return complexFccs;
    }

    /**
     * return null if this *is* an fcc
     */
    public Exp computeComplexFccs() {
        assert isAllComplex() : this;
        assert isFormula();
        assert !isDnnf();

        assert this.fcc == null;

        UnionFind uf = computeUnionFind();
        uf.processAllUniquePairs();

        int fccCount = uf.getFccCount();

        if (fccCount == 1) {
            this.fcc = true;
            return null;
        }

        ArrayListMultimap<Integer, Exp> mm = ArrayListMultimap.create();

        for (int i = 0; i < getConstraintCount(); i++) {
            int fcc = uf.getFccFor(i);
            Exp constraint = getArg(i);
            mm.put(fcc, constraint);
        }

        Set<Integer> keySet = mm.keySet();

        ArgBuilder bFccs = new ArgBuilder(_space, Op.DAnd);
        for (Integer key : keySet) {
            if (bFccs.isShortCircuit()) {
                break;
            }
            List<Exp> fccConstraints = mm.get(key);
            ArgBuilder bFcc = new ArgBuilder(_space, Op.Formula, fccConstraints);

            bFcc.setStructure(Structure.Fcc);

            Exp fccExp = bFcc.mk();

            if (fccExp.isFalse()) {
                return _space.mkFalse();
            }

            Exp fccExpDnnf = fccExp.toDnnf();

            if (fccExpDnnf.isFalse()) {
                return _space.mkFalse();
            }

            bFccs.addExp(fccExpDnnf);
        }


        return bFccs.mk();


    }


    /**
     * return null if this *is* an fcc
     */
    public List<List<Exp>> computeComplexFccs2() {
        assert isAllComplex();
        assert isFormula();
        assert !isDnnf();

        assert this.fcc == null;

        UnionFind uf = computeUnionFind();
        uf.processAllUniquePairs();

        int fccCount = uf.getFccCount();

        if (fccCount == 1) {
            this.fcc = true;
            return null;
        }

        ArrayListMultimap<Integer, Exp> mm = ArrayListMultimap.create();
        for (int i = 0; i < getConstraintCount(); i++) {
            int fccKey = uf.getFccFor(i);
            Exp constraint = getArg(i);
            mm.put(fccKey, constraint);
        }

        List<List<Exp>> disjoint = new ArrayList<List<Exp>>();
        for (Integer key : mm.keySet()) {
            disjoint.add(mm.get(key));
        }

        return disjoint;

    }


//    public VarSet getVars() {
//        if (vars == null) {
//            vars = computeVars();
//        }
//        return vars;
//    }
//
//
//    private VarSet computeVars() {
//        VarSetBuilder b = getSpace().varSetBuilder();
//        for (Exp e : args) {
//            assert !e.isLeaf();
//            b.addVars(e.getVars());
//        }
//        return b.build();
//    }

//    public boolean isSat() {
//        Xor xor = getBestXorSplit();
//        if (xor != null) {
//            XorSplit split = new XorSplit(this, xor);
//            return split.isSat();
//        } else {
//            Var vr = decide();
//            DecisionSplit split = new DecisionSplit(this, vr);
//            return split.isSat();
//        }
//    }


    public Exp fccSplitDnnf() {
        return fccSplitDnnf3();
    }

    public Exp fccSplitDnnf1() {
        Xor xor = getBestXorSplit();
        if (xor != null) {
            System.err.println("XorSplitting c " + xor);
            XorSplit split = new XorSplit(this, xor);
            return split.toDnnf();
        } else {
            Var var = decide();
            DecisionSplit split = new DecisionSplit(this, var);
            return split.toDnnf();
        }
    }

    public Exp fccSplitDnnf3() {

        Xor xor = getBestXorSplit();
        if (xor != null) {
            return xorSplitToDnnf(xor);
        } else {
            Var var = decide();
            return decisionSplit(var);
        }
    }

    private Exp xorSplitToDnnf(Exp xor) {
        XorSplit split = new XorSplit(this, xor.asXor());
        return split.toDnnf();
    }

    private Exp decisionSplit(Var decisionVar) {
        DecisionSplit split = new DecisionSplit(this, decisionVar);
        return split.toDnnf();
    }

    public int getConstraintCount() {
        return getArgCount();
    }


    public Iterator<Exp> iterator() {
        return argIter();
    }


    public boolean isDirectlyRelated(int index1, int index2) {
        Exp complex1 = args[index1];
        Exp complex2 = args[index2];

        assert complex1.isComplex();
        assert complex2.isComplex();

        VarSet vs1 = complex1.getVars();
        VarSet vs2 = complex2.getVars();

        return vs1.anyVarOverlap(vs2);
    }

    public Exp getFConstraint(int fLocalConstraintIndex1) {
        return args[fLocalConstraintIndex1];
    }

//    public Exp mkXorSplit() {
//        ArgBuilder b = new ArgBuilder(_space, Op.DOr).addExpArray(args);
//        return b.mk();
//    }


    public Iterable<Var> varIt() {
        return getVars().varIt();
    }

    private Xor _getLargestXor() {

        Xor best = null;
        int bestSize = 0;

        for (Exp e : argIt()) {
            if (e.isXor()) {
                if (e.getArgCount() > bestSize) {
                    best = e.asXor();
                    bestSize = e.getArgCount();
                }
            }
        }

        return best;
    }


    @Nullable
    public Xor getBestXorSplit() {
        if (_space.hasPrefixes()) {
            return XorCounts.getMax(this);
        } else {
            List<Exp> xors = getXorConstraints();
            return xors.isEmpty() ? null : xors.get(0).asXor();
        }
    }

    @Nullable
    public Xor getTopXorSplit() {
        Xor x;

        x = getYearXor();
        if (x != null) return x;

        x = getSeriesXor();
        if (x != null) return x;

        return getModelXor();
    }

    public Xor getYearXor() {
        return getXor(YR_PREFIX);
    }

    public Xor getSeriesXor() {
        return getXor(SER_PREFIX);
    }

    public Xor getModelXor() {
        return getXor(MDL_PREFIX);
    }

    public Xor getXColXor() {
        return getXor(XCOL_PREFIX);
    }

    public Xor getDealerXor() {

        for (Exp e : argIt()) {
            if (e.isXor()) {
                if (e.hasDealers()) {
                    return e.asXor();
                }
            }
        }

        return null;
    }


    private IXor _getBestXorMIXY() {
        VarSet vars = getVars();
        return getSpace().getBestCoreXor(vars);
    }

    private IXor _getBestXorMIXYPlus() {
        IXor xor1 = _getBestXorMIXY();
        if (xor1 != null) {
            return xor1;
        }
        return _getLargestXor();
    }

    private Xor _getSmallestXor() {

        Xor best = null;
        int bestSize = Integer.MAX_VALUE;

        for (Exp e : argIt()) {
            if (e.isXor() && e.getArgCount() < bestSize) {
                best = e.asXor();
                bestSize = e.getArgCount();
            }
        }

        return best;
    }


    public Var decide() {
        FVars fVars = getFVars();
        try {
            FVar d = fVars.decide();
            return d.vr;
        } catch (NoVarsException e) {
            throw new NoVarsException(this.toString());
        }


    }

//    public Exp mkDecisionSplit() {
//        assert isFcc();
//        Var var = decide();
//        ArgBuilder b = new ArgBuilder(_space, Op.DOr).addExpArray(args);
//        b.addComplex(this.argIt());
//        return b.mk();
//    }
//
//    public Csp refine(String sLit) {
//        Lit lit = _space.mkLit(sLit);
//        return new Csp(this, lit);
//    }

    public FVars getFVars() {
        if (fVars == null) {
            fVars = new FVars(this);
            if (fVars.sortedList.isEmpty()) {
                throw new IllegalStateException();
            }
        } else {
            if (fVars.sortedList.isEmpty()) {
                throw new IllegalStateException();
            }
        }
        return fVars;
    }


    public List<Exp> getXorConstraints() {
        return Csp.getXorConstraints(argIt());
    }


    void prindentXorChild(Space space, IXor xor, Var var) {

        String prefix = xor.getPrefix();

        if (prefix.equals("MDL")) {
            String msg = "  creating disjunct[" + var + "]";
            int depth = 0;
            Console.prindent(depth, msg);
        }

    }


    public Set<String> getXorPrefixes() {
        return getVars().getXorPrefixes();
    }

    public Set<String> getNonXorPrefixes() {
        return getVars().getNonXorPrefixes();
    }


    public DynCube computeBB() {
        VarSet vars = getVars();

        DynCube aa = new DynCube(getSpace());

        for (String prefix : vars.getXorPrefixes()) {
            computeBbForXorPrefix(prefix, aa);
        }


        for (String prefix : vars.getNonXorPrefixes()) {
            computeBbForNonXorPrefix(prefix, aa);
        }
        return aa;
    }


    public SortedSet<String> getBBCodes() {
        return getBB().toSortedCodes();
    }
//
//    public boolean contains(Exp e) {
//        return fact.contains(e);
//    }


    public DynCube computeBbForPrefix(String prefix) {
        DynCube bb = new DynCube(getSpace());
        computeBbForPrefix(prefix, bb);
        return bb;
    }

    public DynCube computeBbForPrefix(Prefix prefix) {
        return computeBbForPrefix(prefix.getName());
    }

    public DynCube computeBbYears() {
        return computeBbForPrefix(Prefix.YR);
    }

    public DynCube computeBbModels() {
        return computeBbForPrefix(Prefix.MDL);
    }

    public DynCube computeBbDrives() {
        return computeBbForPrefix(Prefix.DRV);
    }

    public boolean isDisjoint(DynCube ass) {
        if (ass == null) return true;
        return ass.isVarDisjoint(this);
    }

    public boolean containsAllVars(DynCube a) {
        if (a == null) return true;
        VarSet vars = getVars();
        if (vars == null) return false;
        return vars.containsAllVars(a.getVars());
    }


    public VarSet getVarForPrefix(Prefix prefix) {
        VarSet b = _space.newMutableVarSet();
        for (Var var : varIt()) {
            if (var.is(prefix)) {
                b.addVar(var);
            }
        }
        return b;
    }


    public void computeBbForPrefix(String prefix, DynCube bb) {
        if (isXorPrefix(prefix)) {
            computeBbForXorPrefix(prefix, bb);
        } else {
            computeBbForNonXorPrefix(prefix, bb);
        }
    }


    public boolean isXorPrefix(String prefix) {
        if (Prefix.isXor(prefix)) {
            return true;
        }
        Var var = getFirstVarForPrefix(prefix);
        return var.isXorChild();
    }


    public boolean isEmpty() {
        return false;
    }


    public Var getFirstVarForPrefix(String prefix) {
        VarSet vars = getVars();
        return vars.getFirstVarForPrefix(prefix);
    }

    public Xor getXor(String xorPrefix) {
        for (Exp exp : argIt()) {
            if (exp.isXor() && exp.asXor().getPrefix().equalsIgnoreCase(xorPrefix)) {
                return exp.asXor();
            }
        }
        return null;
    }

    private VarSet getVarsForPrefix(String prefix) {
        return getVars().filter(prefix);
    }

    private void computeBbForXorPrefix(final String xorPrefix, DynCube bb) {
//        System.err.println("testing xor prefix[" + xorPrefix + "] for dead vars: ");

        final VarSet xorVars;
        Xor xor = getXor(xorPrefix);
        if (xor == null) {
            xorVars = getVarsForPrefix(xorPrefix);
//            xor = new Xor(xorVars, Integer.MAX_VALUE);
            Exp exp = _space.mkXor(xorVars);
            if (exp.isXor()) {
                xor = exp.asXor();
            } else {
                computeBbForNonXorPrefix(xorPrefix, bb);
                return;
//                throw new IllegalStateException("Prefix[" + xorPrefix + "] not an xor: [" + exp + "]");
            }

        } else {
            xorVars = xor.getVars();
        }

        XorSplit split = new XorSplit(this, xor);

        VarSetBuilder xorFalseVars = getSpace().newMutableVarSet();

        Var lastOpenVar = null;
        for (Var trueVar : xorVars) {

//            System.err.println("    testing vr[" + vr + "]");


            Csp r = split.mkCsp(trueVar);

            boolean sat = r.isSat();
            if (!sat) {
                xorFalseVars.add(trueVar);
//                System.err.println("bbLit[" + vr.nLit() + "]");
            } else {
                lastOpenVar = trueVar;
            }
        }

        for (Var xorFalseVar : xorFalseVars) {
            bb.assign(xorFalseVar, false);
        }

        int openCount = xorVars.size() - xorFalseVars.size();
        if (openCount == 1) {
            bb.assign(lastOpenVar, true);
        }


    }


    public DynCube getBB() {
        if (bb == null) {
            bb = computeBB();
        }
        return bb;
    }

    private Lit proposeBothWays(Var var) {
        DecisionSplit split = new DecisionSplit(this, var);
        Csp tt = split.mkCsp(true);
        if (!tt.isSat()) {
            //must be f
            //            System.err.println("  found bb lit[" + vr.mkNegLit() + "]");
            return var.nLit();
        } else {

            Csp ff = split.mkCsp(false);
            if (!ff.isSat()) {
                //must be t
                //                System.err.println("  found bb lit[" + vr.mkPosLit() + "]");
                return var.pLit();
            } else {
                return null; //open
            }
        }
    }

    private Lit proposeBothWaysLite(Var var) {
        DecisionSplit split = new DecisionSplit(this, var);
        Csp tt = split.mkCsp(true);

        if (tt.isFailed()) {
            //must be f
            //            System.err.println("  found bb lit[" + vr.mkNegLit() + "]");
            return var.nLit();
        } else {

            Csp ff = split.mkCsp(false);
            if (ff.isFailed()) {
                //must be t
                //                System.err.println("  found bb lit[" + vr.mkPosLit() + "]");
                return var.pLit();
            } else {
                return null; //open
            }
        }
    }

    private void computeBbForNonXorPrefix(String prefix, DynCube bb) {
//        System.err.println("testing prefix[" + prefix + "] for dead vars");


        VarSet vars = getVars().filter(prefix);

        for (Var var : vars) {
//            System.err.println("    testing vr[" + vr + "]");
            Lit bbLit = proposeBothWays(var);
            if (bbLit != null) {
                bb.assign(bbLit);
//                System.err.println("bbLit[" + bbLit + "]");
            }
        }

    }

    private void computeBbForNonXorPrefixLite(String prefix, DynCube bb) {
//        System.err.println("testing prefix[" + prefix + "] for dead vars");


        VarSet vars = getVars().filter(prefix);

        for (Var var : vars) {
//            System.err.println("    testing vr[" + vr + "]");
            Lit bbLit = proposeBothWaysLite(var);
            if (bbLit != null) {
                bb.assign(bbLit);
//                System.err.println("bbLit[" + bbLit + "]");
            }
        }

    }

    public boolean isFormula() {
        return true;
    }

    @Override
    public void print() {
        for (Exp arg : args) {
            System.err.println("  " + arg);
        }
    }


    public boolean isFcc() {
        return fcc != null && fcc;
    }


    public Op getOp() {
        return Op.Formula;
    }


}

abstract class Fcc {
    public static final IsFcc IS_FCC = new IsFcc();
    public static final Unknown UNKNOWN = new Unknown();
}

final class IsFcc extends Fcc {
}

final class Unknown extends Fcc {
}

final class HasFccs extends Fcc {
    private final List<Exp> fccs;

    public HasFccs(List<Exp> fccs) {
        this.fccs = fccs;
    }
}
