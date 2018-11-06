package com.tms.csp.ast;


import com.tms.csp.fm.dnnf.products.Cube;

/**
 *
 * iff(a and(z y z)).toCnf =
 *
 *      or(!a x)
 *      or(!a y)
 *      or(!a z)
 *      or(!z !y !x a)
 *
 *      all horn clauses
 *
 */
public class Iff extends PosComplexMultiVar {

    public static final PosOp OP = PosOp.IFF;

    public Iff(Space space, int expId, Exp[] fixedArgs) {
        super(space, expId, fixedArgs);
        assert args.length == 2;
        assert args[0].getExpId() < args[1].getExpId();
    }

    public boolean isBinaryType() {
        return true;
    }


    @Override
    public Exp condition(Cube ctx) {
        if (isVarDisjoint(ctx)) return this;

        Exp sExpr1 = getExpr1().condition(ctx);
        Exp sExpr2 = getExpr2().condition(ctx);

        if (sExpr1 == sExpr2) return mkTrue();

        if (sExpr1.isFalse() && sExpr2.isFalse()) return mkTrue();
        else if (sExpr1.isTrue() && sExpr2.isTrue()) return mkTrue();
        else if (sExpr1.isFalse() && sExpr2.isTrue()) return mkFalse();
        else if (sExpr1.isTrue() && sExpr2.isFalse()) return mkFalse();

        else if (sExpr1.isTrue() && sExpr2.isOpen()) return sExpr2;
        else if (sExpr1.isOpen() && sExpr2.isTrue()) return sExpr1;

        else if (sExpr1.isFalse() && sExpr2.isOpen()) return sExpr2.flip();
        else if (sExpr1.isOpen() && sExpr2.isFalse()) return sExpr1.flip();


        else if (sExpr1 == getExpr1() && sExpr2 == getExpr2()) {
            return this;
        } else {

            return mkIff(sExpr1, sExpr2);
        }

    }

    @Override
    public Exp condition(Lit lit) {

        if (!containsVar(lit)) {
            return this;
        }

        Exp sExpr1 = getExpr1().condition(lit);
        Exp sExpr2 = getExpr2().condition(lit);

        if (sExpr1 == sExpr2) return mkTrue();

        if (sExpr1.isFalse() && sExpr2.isFalse()) return mkTrue();
        else if (sExpr1.isTrue() && sExpr2.isTrue()) return mkTrue();
        else if (sExpr1.isFalse() && sExpr2.isTrue()) return mkFalse();
        else if (sExpr1.isTrue() && sExpr2.isFalse()) return mkFalse();

        else if (sExpr1.isTrue() && sExpr2.isOpen()) return sExpr2;
        else if (sExpr1.isOpen() && sExpr2.isTrue()) return sExpr1;

        else if (sExpr1.isFalse() && sExpr2.isOpen()) return sExpr2.flip();
        else if (sExpr1.isOpen() && sExpr2.isFalse()) return sExpr1.flip();


        else if (sExpr1 == getExpr1() && sExpr2 == getExpr2()) {
            return this;
        } else {

            return mkIff(sExpr1, sExpr2);
        }

    }


    @Override
    public PosOp getPosOp() {
        return OP;
    }

    @Override
    public Op getOp() {
        return Op.Iff;
    }


}
