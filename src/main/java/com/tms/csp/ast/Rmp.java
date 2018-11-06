package com.tms.csp.ast;


import com.tms.csp.fm.dnnf.products.Cube;

public class Rmp extends PosComplexMultiVar {

    public static final PosOp OP = PosOp.RMP;

    public Exp or;

    public Rmp(Space space, int expId, Exp[] args) {
        super(space, expId, args);
        assert isBinary();
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

        if (v2.isFalse() || v1.isTrue()) {
            return mkTrue();
        }

        if (v2.isTrue()) {
            return v1;
        }

        if (v1.isFalse()) {
            return v2.flip();
        }

        if (v1 == e1 && v2 == e2) {
            return this;
        }

        Exp retVal = mkRmp(v1, v2);

        assert !anyConstants(retVal.argIt());

        return retVal;

    }


    public Exp toCnf() {

        if (arg2().isAnd()) {
            Exp[] a = new Exp[arg2().size()];
            Exp e1 = arg1();
            for (int i = 0; i < a.length; i++) {
                Exp e2 = arg2().getArg(i);


//                a[i] = new Imp(e1, e2);
                a[i] = mkImp(e1, e2);
            }
            return mkAnd(a);
        }


//        if (arg1().isVar() && arg2().isCube()) {
//            Exp[] a = new Exp[arg2().argCount() + 1];
//            a[0] = arg1().flip();
//            for (int i = 0; i < arg2().args().length; i++) {
//                a[i + 1] = arg2().getArg(i);
//            }
//            return new Or(a);
//        } else if (arg2().isVar() && arg1().isCube()) {
//            Exp[] a = new Exp[arg1().argCount() + 1];
//            a[0] = arg2();
//            for (int i = 0; i < arg1().args().length; i++) {
//                a[i + 1] = arg1().getArg(i).flip();
//            }
//            return new Or(a);
//        }

        return mkOr(getArg1().flip(), getArg2());
    }

    @Override
    public PosOp getPosOp() {
        return OP;
    }


    public Exp toOr() {
        if (or == null) {
            this.or = _space.mkOr(getArg1(), getArg2().flip());
//            this.or = ez().mkOr(getArg1(), getArg2().flip());
        }
        return or;
    }


}
