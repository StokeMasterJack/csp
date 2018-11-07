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


    public Formula(Space space, int expId, Exp[] args, Boolean fcc) {
        super(space, expId, args);
        this.fcc = fcc;
    }

//    public Formula(Space space, int expId, Exp[] fixedArgs) {
//        this(space, expId, fixedArgs, null);
//    }

//    public int getDepth() {
//        return depth;
//    }

//    public KFormula mkK() {
//        if (k == null) {
//            k = new KFormula(this);
//        }
//        return k;
//    }





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

//
//    @Override
//    public Formula asFormula() {
//        return this;
//    }

//


//    public VarSet get_vars() {
//        if (_vars == null) {
//            _vars = computeVars();
//        }
//        return _vars;
//    }
//
//
//    private VarSet computeVars() {
//        VarSetBuilder b = getSpace().varSetBuilder();
//        for (Exp e : args) {
//            assert !e.isLeaf();
//            b.addVars(e.get_vars());
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








    public Exp getFConstraint(int fLocalConstraintIndex1) {
        return args[fLocalConstraintIndex1];
    }

//    public Exp mkXorSplit() {
//        ArgBuilder b = new ArgBuilder(_space, Op.DOr).addExpArray(args);
//        return b.mk();
//    }




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





    public SortedSet<String> getBBCodes() {
        return getBB().toSortedCodes();
    }
//
//    public boolean contains(Exp e) {
//        return fact.contains(e);
//    }






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






    public boolean isEmpty() {
        return false;
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

    private void computeBbForNonXorPrefixLite(String prefix, DynCube bb) {
//        System.err.println("testing prefix[" + prefix + "] for dead _vars");


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
