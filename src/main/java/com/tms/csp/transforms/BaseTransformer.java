package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class BaseTransformer extends Transformer {

    protected final boolean repeatUntilStable;

    protected BaseTransformer(boolean repeatUntilStable) {
        this.repeatUntilStable = repeatUntilStable;
    }

    protected BaseTransformer() {
        this.repeatUntilStable = true;
    }

    @Nonnull
    @Override
    public Exp transform(@Nonnull Exp in) {
        checkNotNull(in);

        while (true) {
            Exp out;
            if (in.isSimple() || in.isConstant()) {
                out = in;
            } else {
                out = transformComplex(in);
            }

            if (out == in) {
                return out;
            } else if (out == null) {
                throw new IllegalStateException();
            } else {
                in = out;
            }

        }


    }

    protected Exp transformComplex(Exp in) {
        Exp out = processArgs(in);

        if (executeLocal(out)) {
            Exp retVal = local(out);
            if (retVal == null) {
                throw new IllegalStateException(this.getClass().getName());
            }
            return retVal;
        } else {
            return out;
        }
    }

    protected Exp processArgs(Exp before) {
        checkState(before.isComplex() || before.isNot());

        if (before.isNot()) {
            Exp argBefore = before.getArg();
            Exp argAfter = this.transform(argBefore);
            if (argAfter == argBefore) {
                return before;
            } else {
                return before.getSpace().mkNot(argAfter);
            }

        } else if (before.isComplex()) {
            List<Exp> beforeArgs;
            if (before.isPosVarsExp()) {
                beforeArgs = new ArrayList<Exp>();
                for (Exp exp : before.argIt()) {
                    beforeArgs.add(exp);
                }
            } else {
                beforeArgs = before.asPosComplex().getArgs();
            }

            List<Exp> argsAfter = transformArgs(beforeArgs);

            if (argsAfter == beforeArgs) {
                return before;
            } else {
                PosOp op = before.asPosComplex().getPosOp();
                Space space = before.getSpace();
                return space.mkPosComplex(op, argsAfter);
            }
        } else {
            throw new IllegalStateException();
        }


    }

    abstract protected boolean executeLocal(Exp in);

    abstract protected Exp local(Exp in);


}
