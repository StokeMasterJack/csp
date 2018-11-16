package com.tms.csp.ast;

import com.google.common.collect.ImmutableSet;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.ast.formula.FConstraintSet;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.UnionFind;
import com.tms.csp.util.ints.IntIterator;
import com.tms.csp.util.varSets.VarSet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class And extends PosComplexMultiVar implements FConstraintSet {

    public static final PosOp OP = PosOp.AND;


    public And(Space space, int expId, Exp[] fixedArgs) {
        super(space, expId, fixedArgs);
        assert checkFixedArgs(fixedArgs);

        if (args.length == 2) {
            if (Exp.isFlip(args[0], args[1])) {
                throw new IllegalStateException();
            }
        }

    }

    @NotNull
    @Override
    public Iterable<Exp> getArgIt() {
        return argIt();
    }

    public Iterable<Exp> argIt() {
        return this::argIter;
    }


    @Override
    public PosComplexMultiVar asPosComplex() {
        return this;
    }

    @Override
    public And asAnd() {
        return this;
    }

    public boolean computeIsSat() {
        if (isCube()) {
            return true;
        } else {

            //convert and to csp

            return new Csp(this).isSat();
        }

    }


    //    and(x y z)  cond or(x y)   => adds nothing
//    and(x y z)  cond or(!x y)  => skip y
//    and(x y z)  cond or(x !y)  => y
//    and(x y z)  cond or(!x !y) => false
    @Nonnull
    public Exp conditionVV(Exp vv) {
        if (!anyVarOverlap(vv)) {
            return this;
        }

        VarSet vars = vv.getVars();
        IntIterator it = vars.intIterator();
        int var1 = it.next();
        int var2 = it.next();

        Exp skip = null;

        if (containsLocalLitWithVar(var1, var2)) {
            LL ll = containsVVArgs(vv);

            if (ll != null) {
                if (ll.ft()) {
                    skip = vv.arg2();
                } else if (ll.tf()) {
                    skip = vv.arg1();
                } else if (ll.ff()) {
                    return mkFalse();
                }
            }

        }

        Op and = this.getOp();  //could be And or DAnd
        ArgBuilder a = new ArgBuilder(_space, and);
        Iterator<Exp> argIt = argIter();
        while (argIt.hasNext()) {

            if (a.isShortCircuit()) {
                break;
            }
            Exp e = argIt.next();
            if (e != skip) {
                Exp v = e.conditionVV(vv);
                a.addExp(v);
            }
        }

        return a.mk();

    }

    public UnionFind computeUnionFind() {
        UnionFind unionFind = new UnionFind(this);
        unionFind.processAllUniquePairs();
        return unionFind;
    }

    public Exp createHardFlip() {
        ArgBuilder argBuilder = flipArgs();
        return argBuilder.mk();
    }

    public ArgBuilder flipArgs() {
        ArgBuilder b = argBuilder(Op.Or);
        for (Exp arg : args) {
            b.addExp(arg.flip());
        }
        return b;
    }

    public boolean canVVSimplifyLocal(PosComplexMultiVar.LL ll) {
        if (ll == null) return false;
        if (ll.ft()) {
            return true;
        } else if (ll.tf()) {
            return true;
        } else if (ll.ff()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean isAnd() {
        return true;
    }

    @Override
    public boolean isAndOfClauses() {
        return isAllClauses();
    }

    @Override
    public boolean isNegationNormalForm() {
        for (Exp arg : args()) {
            if (!arg.isNegationNormalForm()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public PosOp getPosOp() {
        return OP;
    }


    @Override
    public Op getOp() {
        return Op.And;
    }

    public Exp flatten() {
        Space space = getSpace();

        if (isFlat()) {
            return this;
        }

        ImmutableSet.Builder<Exp> b = ImmutableSet.builder();
        for (Exp arg : args) {
            arg = arg.flatten();
            if (arg.isAnd()) {
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

        return space.mkAnd(retVal);
    }


    @Override
    public Exp pushNotsIn() {
        ArgBuilder b = new ArgBuilder(_space, op());
        for (Exp arg : args) {
            Exp s = arg.pushNotsIn();
            b.addExp(s);
        }
        return b.mk();
    }

    @Override
    public int getConstraintCount() {
        return getArgCount();
    }

    @Override
    public boolean isDirectlyRelated(int c1, int c2) {
        Exp arg1 = getArg(c1);
        Exp arg2 = getArg(c2);
        return arg1.anyVarOverlap(arg2);
    }

    @Override
    public Exp getFirstConjunctContaining(String varCode) {
        for (Exp arg : args) {

            if (arg.containsVar(varCode)) {
                return arg;
            }
        }
        return null;
    }

    public boolean isDisjoint() {
        return checkDisjointConjuncts(false);
    }

    public boolean checkDisjointConjuncts() {
        return checkDisjointConjuncts(true);

    }

    public boolean checkDisjointConjuncts(boolean logging) {
        VarSet v = _space.newMutableVarSet();
        for (Exp arg : args) {
            for (Var argVar : arg.varIt()) {
                int varId = argVar.getVarId();
                boolean added = v.addVarId(varId);
                if (!added) {
                    if (logging) {
                        System.err.println("args are not disjoint: ");
                        System.err.println("    Var[" + argVar + "] appears formula more than one conjunct");
                        System.err.println("  " + this);
                    }
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public Exp condition(Lit lit) {

        return k().condition(this, lit);
    }

    @Override
    public Exp condition(Cube cube) {
        return k().condition(this, cube);
    }

    @Override
    public Exp toDnnf() {
        return k().toDnnf(this);
    }
}
