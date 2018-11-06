package com.tms.csp.ast;

public class LitPair {

    private final Exp lit1;
    private final Exp lit2;

    public LitPair(Exp lit1, Exp lit2) {
        this.lit1 = lit1;
        this.lit2 = lit2;
    }

    public Exp getLit1() {
        return lit1;
    }

    public Exp getLit2() {
        return lit2;
    }

    @Override
    public String toString() {
        return lit1 + " " + lit2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LitPair)) return false;

        LitPair litPair = (LitPair) o;

        if (!lit1.equals(litPair.lit1)) return false;
        if (!lit2.equals(litPair.lit2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lit1.hashCode();
        result = 31 * result + lit2.hashCode();
        return result;
    }

    /**
     * Assuming this is failed LitPair, convert to a cnf clause.
     * aka convert
     *      from: !and(e1 e2)
     *      to:   or(!e1 !2e)
     */
    public Exp toClause() {
        Space space = lit1.getSpace();
        return space.mkOr(lit1.flip(), lit2.flip());
    }
}
