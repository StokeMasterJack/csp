package com.tms.csp.ast;


import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.ints.IntIterator;
import com.tms.csp.util.varSets.VarSet;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class Or extends PosComplexMultiVar {

    public static final PosOp OP = PosOp.OR;

    public Or(Space space, int expId, Exp[] fixedArgs) {
        super(space, expId, fixedArgs);
        assert fixedArgs.length > 1 : this;
        assert checkFixedArgs(fixedArgs);
        assert !isNestedOr();

        if (false) {
            if (args.length == 2) {
                if (Exp.isFlip(args[0], args[1])) {
                    System.err.println("DontCare[" + args[0].getVr() + "]");
                }
            }
        }

    }

    @Override
    public Exp toDnnf() {

        Var decisionVar = decide();
        Space space = decisionVar.getSpace();

        Lit tLit = decisionVar.mkPosLit();
        Exp t = this.condition(tLit);
        Exp tt = t.toDnnf();
        Exp ttt = space.mkDAnd(tLit, tt);

        Lit fLit = decisionVar.mkNegLit();
        Exp f = this.condition(fLit);
        Exp ff = f.toDnnf();
        Exp fff = space.mkDAnd(fLit, ff);

        return space.mkDOr(ttt, fff);

    }

    @Override
    public void serializeGiantOr(Ser a) {
        String token = getPosComplexOpToken(a);
        a.append(token);
        a.append(LPAREN);
        a.newLine();

        Iterable<Exp> argsCopy = argIt();
//        argsCopy = ExpComparator.sortCopy(args);

        Iterator<Exp> it = argsCopy.iterator();
        while (it.hasNext()) {
            Exp arg = it.next();
            a.append("  ");
            arg.serialize(a);
            a.newLine();
        }

        a.append(RPAREN);
    }

    public Var decide() {
        VarSet vars = getVars();
        return vars.getFirstVar();
    }

    @Override
    public Exp condition(Lit lit) {
        if (!containsVar(lit)) {
            return this;
        }
        return argBuilder(op())
                .addExpArray(args, Condition.fromLit(lit))
                .mk();
    }


    @Override
    public Exp condition(Cube cube) {
        if (!anyVarOverlap(cube)) {
            return this;
        }
        return argBuilder(op())
                .addExpArray(args, Condition.fromCube(cube))
                .mk();
    }

    @Override
    public PosComplexMultiVar asPosComplex() {
        return this;
    }


    @Override
    public Or asOr() {
        return this;
    }

    @Override
    public boolean isOr() {
        return true;
    }


    @Override
    @Nonnull
    public Exp conditionVV(Exp vv) {
        if (vv == this) return vv;

        if (!this.anyVarOverlap(vv)) {
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
                if (ll.tt()) {
                    return vv;
                }
//                else if (ll.ft()) {
//                    skip = vv.arg1().flip();
//                } else if (ll.tf()) {
//                    skip = vv.arg2().flip();
//                }
            }

        }

        //todo
        //2 bugs here:
        //  UNFIXED: 1. if !containsLocalLitWithVar it shouldn't try to create a new Exp
        //  FIXED:   2. if space is allowing dup expressions (much bigger bug)

        ArgBuilder a = new ArgBuilder(_space, Op.Or);
        Iterator<Exp> argIt = argIter();
        while (argIt.hasNext() && !a.isShortCircuit()) {
            Exp e = argIt.next();
            if (e != skip) {
                Exp v = e.conditionVV(vv);
                a.addExp(v);
            }
        }
        return a.mk();

    }


    public boolean canVVSimplifyLocal(PosComplexMultiVar.LL ll) {
        if (ll == null) return false;
        if (ll.tt()) {
            return true;
        } else if (ll.ft()) {
            return true;
        } else if (ll.tf()) {
            return true;
        }
        return false;
    }


    @Override
    public PosOp getPosOp() {
        return OP;
    }


    @Override
    public Op getOp() {
        return Op.Or;
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


    public Exp createEquivAnd() {
        Exp hardFlip = createHardFlip();
        return hardFlip.flip();
    }

    public Exp createHardFlip() {
        ArgBuilder b = flipArgs();
        return b.mk();
//        return getSpace().mkAnd(b);
    }

    public ArgBuilder flipArgs() {
        return new ArgBuilder(_space, Op.And, argItFlipped());
    }


    @Override
    public boolean computeIsSat() {
        for (Exp arg : args) {
            if (arg.isSat()) {
                return true;
            }
        }
        return false;
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

}
