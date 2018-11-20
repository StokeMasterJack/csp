package com.smartsoft.csp.ast;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class VarCodePair {

    public final String var1;
    public final String var2;

    public static VarCodePair parse(String vv) {
        vv = vv.replace("!", "");
        String[] a = vv.split(" ");
        return new VarCodePair(a[0], a[1]);
    }

    public VarCodePair(String varCode1, String varCode2) {
        checkNotNull(varCode1);
        checkNotNull(varCode2);
        checkArgument(!varCode1.equals(varCode2));

        int i = varCode1.compareTo(varCode2);
        if (i < 0) {
            var1 = varCode1;
            var2 = varCode2;
        } else if (i > 0) {
            var1 = varCode2;
            var2 = varCode1;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarCodePair oo = (VarCodePair) o;
        return var1.equals(oo.var1) && var2.equals(oo.var2);

    }

    @Override
    public int hashCode() {
        int result = var1.hashCode();
        result = 31 * result + var2.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return var1 + ' ' + var2;
    }

    public String getAndVarCode() {
        return "AND__" + var1 + "__" + var2;
    }
}
