package com.smartsoft.csp.ast;

public enum SignSign {

    FF(false, false), FT(false, true), TF(true, false), TT(true, true);

    private final boolean sign1;
    private final boolean sign2;

    SignSign(boolean sign1, boolean sign2) {
        this.sign1 = sign1;
        this.sign2 = sign2;
    }

    public static SignSign create(boolean sign1, boolean sign2) {
        for (SignSign ss : values()) {
            if (ss.sign1 == sign1 && ss.sign2 == sign2) {
                return ss;
            }
        }

        throw new IllegalStateException();
    }

    public boolean isSign1() {
        return sign1;
    }

    public boolean isSign2() {
        return sign2;
    }

    public int getIndex() {
        return ordinal();
    }
}
