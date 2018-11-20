package com.smartsoft.csp.ast;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.util.Set;

public class StringCube {

    @Nonnull
    private final ImmutableSet<String> t;

    @Nonnull
    private final ImmutableSet<String> f;

    public StringCube(ImmutableSet<String> t, ImmutableSet<String> f) {
        this.t = t;
        this.f = f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringCube that = (StringCube) o;
        return t.equals(that.t) && f.equals(that.f);
    }

    @Override
    public int hashCode() {
        int result = t.hashCode();
        result = 31 * result + f.hashCode();
        return result;
    }

    public static StringCube create(Set<Lit> lits) {
        ImmutableSet.Builder<String> tb = ImmutableSet.builder();
        ImmutableSet.Builder<String> fb = ImmutableSet.builder();

        for (Lit dLit : lits) {
            if (dLit.isPos()) {
                tb.add(dLit.getVarCode());
            }
            if (dLit.isNeg()) {
                fb.add(dLit.getVarCode());
            }
        }

        ImmutableSet<String> t = tb.build();
        ImmutableSet<String> f = fb.build();

        return new StringCube(t, f);
    }

    @Override
    public String toString() {
        Ser a = new Ser();
        serialize(a);
        return a.toString().trim();
    }

    public void serialize(Ser a) {
        for (String tVar : t) {
            a.ap(tVar);
            a.argSep();
        }
        for (String fVar : t) {
            a.bang();
            a.ap(fVar);
            a.argSep();
        }
    }

    public void print() {
//        System.err.println("tVars: " + ParseUtil.serializeCodes2(tCon));
//        System.err.println("fVars: " + ParseUtil.serializeCodes2(fCon));
    }
}
