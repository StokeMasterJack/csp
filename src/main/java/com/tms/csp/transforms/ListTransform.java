package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;
import com.tms.csp.util.ConstraintSink;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public abstract class ListTransform {

    @Nonnull
    public abstract List<Exp> transform(@Nonnull List<Exp> in);

    public static void transform(List<Exp> listIn, Transformer t, ConstraintSink out) {
        for (Exp e : listIn) {
            Exp s = t.transform(e);
            out.addConstraint(s);
        }
    }

    public static List<Exp> transform(List<Exp> listIn, Transformer... transformer) {
        if (transformer.length == 0) {
            return listIn;
        } else if (transformer.length == 1) {
            return transformList(listIn, transformer[0]);
        } else {
            return multiTransformList(listIn, transformer);
        }
    }

    public static List<Exp> transformList(List<Exp> listIn, Transformer transformer) {
        int elementsChanged = 0;
        List<Exp> listOut = new ArrayList<Exp>();
        for (Exp argIn : listIn) {
            checkState(argIn != null);
            Exp argOut = transformer.transform(argIn);
            if (argOut == null) {
                throw new IllegalArgumentException("transform returned null. Transformer[" + transformer.getClass().getName() + "]  expIn[" + argIn + "]");
            } else if (argOut != argIn) {
                elementsChanged++;
                listOut.add(argOut);
            } else {
                listOut.add(argOut);
            }
        }

        if (elementsChanged > 0) {
            return listOut;
        } else {
            return listIn;
        }

    }

    public static Iterable<Exp> transform2(Iterable<Exp> listIn, Transformer transformer) {
        int elementsChanged = 0;
        List<Exp> listOut = new ArrayList<Exp>();
        for (Exp argIn : listIn) {
            checkState(argIn != null);
            Exp argOut = transformer.transform(argIn);
            if (argOut == null) {
                throw new IllegalArgumentException("transform returned null. Transformer[" + transformer.getClass().getName() + "]  expIn[" + argIn + "]");
            } else if (argOut != argIn) {
                elementsChanged++;
                listOut.add(argOut);
            } else {
                listOut.add(argOut);
            }
        }

        if (elementsChanged > 0) {
            return listOut;
        } else {
            return listIn;
        }

    }

    public static List<Exp> multiTransformList(List<Exp> listIn, Transformer[] transformers) {
        List<Exp> out = listIn;
        for (Transformer transformer : transformers) {
            List<Exp> in = out;
            out = transformList(in, transformer);
        }
        return out;
    }


}
