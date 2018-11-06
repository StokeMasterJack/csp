package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.PLConstants;

public class Transforms implements PLConstants {


    public static Transformer bnf() {
        return Transformer.BNF;
    }

    public static Transformer pushNotsIn() {
        return Transformer.PUSH_NOTS_IN;
    }

    public static Transformer nnf() {
        return Transformer.NNF;
    }

    public static Transformer identity() {
        return Transformer.IDENTITY;
    }


    public static Exp xorToCnf(Exp in) {
        return new XorsToCnf().transform(in);
    }

    public static Exp removeConflicts(Exp in) {
        return new ConflictsToCnf().transform(in);
    }

    public static Exp removeIffs(Exp in) {
        return new IffToCnf().transform(in);
    }

    public static Exp flattenImps(Exp in) {
        return new FlattenImps().transform(in);
    }

    public static Exp removeImps(Exp in) {
        return new ImpsToCnf().transform(in);
    }

    public static Exp flattenOrs(Exp in) {
        return new FlattenOrs().transform(in);
    }

    public static Exp flattenAnd(Exp in) {
        return new FlattenAnds().transform(in);
    }

    public static Exp unFlattenAnd(Exp in) {
        return andToBinary(in);
    }

    public static Exp andToBinary(Exp in) {
        return new AndsToBinary().transform(in);
    }

    public static Exp pushNotsIn(Exp in) {
        return new PushNotsIn().transform(in);
    }

    public static Exp pushUpAnds(Exp in) {
        return new PushAndsOut().transform(in);
    }


}
