package com.tms.csp.ast;

import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.argBuilder.ArgBuilder;

public class Nand extends PosComplexMultiVar {

    public static final PosOp OP = PosOp.NAND;

    public Nand(Space space, int expId, Exp[] args) {
        super(space, expId, args);
        assert isBinary();
    }


    public Exp toOr() {
        Exp e1 = getArg1();
        Exp e2 = getArg2();
        return getSpace().mkOr(e1.flip(), e2.flip());
    }

    public boolean isBinaryType() {
        return true;
    }


    @Override
    public Exp condition(Cube ctx) {
        if (isVarDisjoint(ctx)) return this;

        Exp e1 = getExpr1();
        Exp e2 = getExpr2();


        Exp v1 = e1.condition(ctx);
        Exp v2 = e2.condition(ctx);

        if (v1.isFalse() || v2.isFalse()) {
            return mkTrue();
        } else if (v1.isTrue() && v2.isTrue()) {
            return mkFalse();
        } else if (v1.isTrue() && v2.isOpen()) {
            return v2.flip();
        } else if (v2.isTrue() && v1.isOpen()) {
            return v1.flip();
        } else if (v1.isOpen() && v2.isOpen()) {
            if (v1 == e1 && v2 == e2) {
                return this;
            } else {
                return mkNand(v1, v2);
            }
        } else {
            throw new IllegalStateException();
        }

    }

    public Exp flattenNand() {
        Lit var;
        Or or;
        if (getArg1().isPosLit() && getArg2().isOr()) {
            var = getArg1().asLit();
            or = getArg2().asOr();
        } else if (getArg2().isPosLit() && getArg1().isOr()) {
            var = getArg2().asLit();
            or = getArg1().asOr();
        } else {
            return this;
        }

        ArgBuilder andArgs = new ArgBuilder(_space,Op.And);
        for (Exp orArg : or.argIt()) {
            Exp nand = _space.mkBinaryNand(var, orArg);
            andArgs.addExp(nand);
        }

        return andArgs.mk();
    }


    @Override
    public PosOp getPosOp() {
        return OP;
    }


}
