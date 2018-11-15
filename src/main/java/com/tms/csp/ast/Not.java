package com.tms.csp.ast;

import com.google.common.collect.Iterators;
import com.tms.csp.argBuilder.ArgBuilder;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.varSets.VarSet;

import javax.annotation.Nonnull;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Not extends Complex {

    private final PosComplexMultiVar pos;
    private boolean isNew;

    public Not(@Nonnull Exp pos, int expId) {
        super(pos.getSpace(), expId);
        checkNotNull(pos);
        checkArgument(pos.isPosComplex());
        this.pos = pos.asPosComplex();
        this.isNew = true;
    }


    @Override
    public Exp flip() {
        return pos;
    }

    @Override
    public boolean isPos() {
        return false;
    }

    @Override
    public Exp getPos() {
        return pos;
    }

    @Override
    public Exp getNeg() {
        return this;
    }

    @Override
    public boolean isNot() {
        return true;
    }

    @Override
    public boolean isComplex() {
        return pos.isComplex();
    }

    @Override
    public boolean anyVarOverlap(Exp exp) {
        return pos.anyVarOverlap(exp);
    }

    @Override
    public Exp pushNotsIn() {
        Exp ret =  pushNotsInInternal();
        //        System.err.println("Not.pushNotsIn: ");
//        System.err.println("  Before: " + this);
//        System.err.println("  After: " + ret);

        return ret;
    }

    private Exp pushNotsInInternal() {
        if (pos.isAnd()) {
            pos.asAnd().createHardFlip();
            return pos.asAnd().createHardFlip();
        } else if (pos.isOr()) {
            return pos.asOr().createHardFlip();
        } else {
            return this;
        }
    }

    @Override
    public boolean isNegComplex() {
        return isComplex();
    }

    @Override
    public PosOp getPosOp() {
        return pos.getPosOp();
    }

    @Override
    public Var getFirstVar() {
        return pos.getFirstVar();
    }


    @Override
    public boolean containsVarId(int varId) {
        return pos.containsVarId(varId);
    }

    @Override
    public boolean isOrContainsConstant() {
        return pos.isOrContainsConstant();
    }

    @Override
    public Op getOp() {
        return Op.Not;
    }

    @Override
    public Exp condition(Cube cube) {
        assert !isConstant();

        Exp sPos = pos.condition(cube);

        if (sPos == pos) {
                        return this;
        } else {
                        return sPos.flip();
        }

    }

    public Exp simplify() {
        throw new UnsupportedOperationException();
//        EvalContext ctx = null;
//        Exp simplify = pos.simplify(ctx);
//        return simplify.flip();
    }


    @Override
    public Exp condition(Lit lit) {

//        System.err.println("Not.condition(lit)");
        if (!containsVar(lit)) {
            return this;
        }


        Exp sPos = pos.condition(lit);

        if (sPos.isNot()) {
                        if (true) throw new IllegalStateException();
            return sPos.getArg();
        } else {
            return sPos.flip();
        }


    }

    @Override
    final public Exp getArg(int i) {
        if (i == 0) return pos;
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getArgCount() {
        return 1;
    }


    public Iterator<Exp> argIter() {
        Exp pp = pos;
        return Iterators.singletonIterator(pp);
    }

    @Override
    public VarSet getVars() {
        return pos.getVars();
    }

    public void serialize(Ser a) {
        if (pos.isConstantTrue()) {
            a.constantFalse();
        } else if (pos.isPosComplex()) {
            a.bang();
            pos.serialize(a);
        } else {
            System.err.println("Should never get here: Not.serialize");
            throw new IllegalStateException();
        }
    }

    public Exp hardFlip() {
        System.err.println("Not.hardFlip");
        PosComplexMultiVar pos = getPos().asPosComplex();

        ArgBuilder b = pos.flipArgs();
        Space space = getSpace();
        if (pos.isAnd()) {
            assert b.getOp().isAnd();
            return b.mk();
        }
        if (pos.isOr()) {
            assert b.getOp().isOr();
            return b.mk();
        }

        return this;
    }

    public Exp toNnf() {
        assert !isXorOrContainsXor();
        Exp pNnf = pos.toNnf(false);
        assert pNnf.isOr() || pNnf.isAnd();

        Op newOp;
        if (pos.isOr()) {
            newOp = Op.And;
        } else {
            newOp = Op.Or;
        }
        ArgBuilder b = new ArgBuilder(_space, newOp);
        for (Exp arg : pNnf.argIt()) {
            b.addExp(arg.flip());
        }

        return b.mk();
    }


    @Override
    public boolean isSat() {
        return toNnf().isSat();
    }

    @Override
    public boolean hasFlip() {
        return true;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }


    public void notNew() {
        isNew = false;
    }

    public boolean canPushNotsIn() {
        return pos.isOr() || pos.isAnd();
    }

    public boolean samePos(Not that) {
        return pos.equals(that.pos);
    }

    public Exp toDnnf() {
        Exp nnf = toNnf();
        assert !nnf.isNot();
        return nnf.toDnnf();
    }

    public long satCountPL() {
        return toNnf().satCountPL();
    }

}
