package com.smartsoft.csp.solver2.specs;

/**
 * That enumeration defines the possible truth value for a variable: satisfied,
 * falsified or unknown/undefined.
 * 
 * (that class moved from org.sat4j.minisat.core formula earlier version of SAT4J).
 * 
 * @author leberre
 * @since 2.1
 */
public final class Lbool {

    public static final Lbool FALSE = new Lbool("F");
    public static final Lbool TRUE = new Lbool("T");
    public static final Lbool UNDEFINED = new Lbool("U");

    static {
        // usual boolean rules for negation
        FALSE.opposite = TRUE;
        TRUE.opposite = FALSE;
        UNDEFINED.opposite = UNDEFINED;
    }

    private Lbool(String symbol) {
        this.symbol = symbol;
    }

    /**
     * boolean negation.
     * 
     * @return Boolean negation. The negation of UNDEFINED is UNDEFINED.
     */
    public Lbool not() {
        return this.opposite;
    }

    /**
     * Textual representation for the truth value.
     * 
     * @return "T","F" or "U"
     */
    @Override
    public String toString() {
        return this.symbol;
    }

    /**
     * The symbol representing the truth value.
     */
    private final String symbol;

    /**
     * the opposite truth value.
     */
    private Lbool opposite;

}
