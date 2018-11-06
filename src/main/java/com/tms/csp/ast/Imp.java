package com.tms.csp.ast;


import com.tms.csp.fm.dnnf.products.Cube;

public class Imp extends PosComplexMultiVar {

    public static final PosOp OP = PosOp.IMP;

    public Exp or;


    public Imp(Space space, int expId, Exp[] args) {
        super(space, expId, args);
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


        if (v1.isFalse() || v2.isTrue()) {
            return mkTrue();
        }


        if (v1.isTrue()) {
            return v2.condition(ctx);
        }

        if (v2.isTrue()) {
            return mkTrue();
        }


        if (v2.isFalse()) {
            return v1.flip().condition(ctx);
        }
        if (v1 == e1 && v2 == e2) {
            return this;
        }

        return mkImp(v1, v2).condition(ctx);
    }


    public Exp toCnf() {

        if (arg2().isAnd()) {
            Exp[] a = new Exp[arg2().size()];
            Exp e1 = arg1();
            for (int i = 0; i < arg2().getArgCount(); i++) {
                Exp e2 = arg2().getArg(i);
                a[i] = mkOr(e1.flip(), e2);
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

            Exp a1 = getArg1();
            Exp a2 = getArg2();
            Exp na1 = a1.flip();

            Space space = getSpace();

            this.or = space.mkOr(na1, a2);
        }
        return or;
    }


}
