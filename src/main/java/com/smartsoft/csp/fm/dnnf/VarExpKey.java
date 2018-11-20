package com.smartsoft.csp.fm.dnnf;

public class VarExpKey {

    private final int varId;
    private final int expId;

    public VarExpKey(int varId, int expId) {
        this.varId = varId;
        this.expId = expId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VarExpKey varExpKey = (VarExpKey) o;

        if (expId != varExpKey.expId) return false;
        if (varId != varExpKey.varId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = varId;
        result = 31 * result + expId;
        return result;
    }


}
