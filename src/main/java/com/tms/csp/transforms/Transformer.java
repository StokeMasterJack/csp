package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.PLConstants;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tms.csp.ssutil.Strings.getSimpleName;

public abstract class Transformer extends Transforms implements PLConstants {

    public static final Transformer FLATTEN_ANDS = new FlattenAnds();
    public static final Transformer FLATTEN_IMPS = new FlattenImps();
    public static final Transformer FLATTEN_RMPS = new FlattenRmps();
    public static final Transformer FLATTEN_ORS = new FlattenOrs();
    public static final Transformer OR_TO_AND = new OrToAnd();
    public static final Transformer AND_TO_BINARY = new AndsToBinary();
    public static final Transformer FLATTEN = new UntilStableTransformer(FLATTEN_ANDS, FLATTEN_ORS);


    public String name;

    public String getName() {
        if (name == null) {
            name = getSimpleName(this);
        }
        return name;
    }

    public Object howToHandle(@Nonnull Exp in) {
        return null;
    }


    @Nonnull
    public abstract Exp transform(@Nonnull Exp in);

    public Exp transformUntilStable(@Nonnull Exp in) {
        return transformUntilStable(this, in);
    }

    public List<Exp> transformArgs(List<Exp> args) {
        List<Exp> exps = transformArgs(this, args);
        if (exps == null) {
            throw new IllegalStateException();
        }
        return exps;
    }

    public Iterable<Exp> transformArgs2(Iterable<Exp> args) {
        Iterable<Exp> exps = transformArgs2(this, args);
        if (exps == null) {
            throw new IllegalStateException();
        }
        return exps;
    }


    static int outMatchesInCount = 0;

    @Nonnull
    public static Exp transformUntilStable(Transformer transformer, @Nonnull Exp in) {

        Exp out = transformer.transform(in);

        if (out == in) {
            return out;
        } else {
            return transformUntilStable(transformer, out);
        }
    }

    public static List<Exp> transformList(Transformer transformer, List<Exp> list) {
        return ListTransform.transform(list, transformer);
    }

    /**
     * Leave Exp.type the same
     */
    protected Exp transformArgsOnly(Exp in) {
        checkNotNull(in);
        if (in.isConstant() || in.isLit()) {
            return in;
        } else if (in.isNegComplex()) {
            Exp argBefore = in.getArg();
            Exp argAfter = this.transform(argBefore);
            if (argAfter == argBefore) {
                return in;
            } else {
                return argAfter.flip();
            }
        } else if (in.isPosComplex()) {
            Iterable<Exp> inArgs = in.args();
            Iterable<Exp> outArgs = transformArgs2(inArgs);

            Exp retVal;
            if (outArgs == inArgs) {
                retVal = in;
            } else {
                retVal = in.asPosComplex().newComplex(outArgs);
            }

            checkNotNull(retVal);

            return retVal;
        } else {
            throw new IllegalArgumentException();
        }


    }

    public static List<Exp> transformArgs(Transformer transformer, List<Exp> argsIn) {
        List<Exp> listOut = ListTransform.transform(argsIn, transformer);
        if (argsIn == listOut) {
            return argsIn;
        } else {
            return listOut;
        }
    }

    public static Iterable<Exp> transformArgs2(Transformer transformer, Iterable<Exp> argsIn) {
        Iterable<Exp> listOut = ListTransform.transform2(argsIn, transformer);
        if (argsIn == listOut) {
            return argsIn;
        } else {
            return listOut;
        }
    }

//    public ExpSet applyDelta(Exp[] delta) {
//        if (delta == null) {
//            return this;
//        }
//
//        Exp[] ret = new Exp[size()];
//        for (int i = 0; i < size(); i++) {
//            if (delta[i] != null) {
//                ret[i] = delta[i];
//            } else {
//                ret[i] = get(i);
//            }
//        }
//
//
//        return new NSet(ret);
//    }


    public static final Transformer IDENTITY = new IdentityTransformer();


    public static final Transformer PUSH_NOTS_IN = new UntilStableTransformer(new CompoundTransformer(
            new PushNotsIn(),
            FLATTEN,
            new OneArgOr(), new OneArgAnd()
    ));

    public static final Transformer PUSH_ANDS_OUT = new PushAndsOut();


    /**
     * BNF:mBase Normal Form
     * Allow:
     * And(N)
     * Not
     * Or(N)
     * Var
     */
    public static final Transformer BNF = new UntilStableTransformer("BNF", new CompoundTransformer(
            new FlattenImps(),
            new FlattenRmps(),
            new XorsToCnf(),
            new ConflictsToCnf(),
            new IffToCnf(),
            new ImpsToCnf(),
            new RmpsToCnf(),
            new FlattenOrs(),
            new FlattenAnds(),
            new OneArgOr(),
            new OneArgAnd()
    ));

    public static final Transformer BNF_KEEP_XORS = new UntilStableTransformer("BNF", new CompoundTransformer(
            new FlattenImps(),
            new FlattenRmps(),
            new ConflictsToCnf(),
            new IffToCnf(),
            new ImpsToCnf(),
            new RmpsToCnf(),
            new FlattenOrs(),
            new FlattenAnds(),
            new OneArgOr(),
            new OneArgAnd()
    ));


    public static final Transformer BNF_TO_NNF = new UntilStableTransformer("BNF_TO_NNF", PUSH_NOTS_IN, FLATTEN);

    /**
     * Only allow:
     * Same as BNF,but only _complexVars can be negated
     */
    public static final Transformer NNF = new UntilStableTransformer("NNF", BNF, BNF_TO_NNF);

    public static final Transformer BNF_TO_AIG = new UntilStableTransformer("AIG",
            OR_TO_AND,
            AND_TO_BINARY
    );


    public static final Transformer NNF_TO_CNF = new UntilStableTransformer("NNF_TO_CNF",
            new PushAndsOut(),
            new OneArgOr(),
            new OneArgAnd(),
            FLATTEN
    );

    public static final Transformer CNF = new UntilStableTransformer("CNF", NNF_TO_CNF, NNF);

    public static final Transformer AIG = new CompoundTransformer("AIG", BNF, BNF_TO_AIG);


}
