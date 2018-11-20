package com.smartsoft.csp.solver2.core;

/**
 * Utility methods to avoid using bit manipulation inside code. One should use
 * Java 1.5 import static feature to use it without class qualification inside
 * the code.
 * 
 * In the DIMACS format, the literals are represented by signed integers, 0
 * denoting the end of the clause. In the solver, the literals are represented
 * by positive integers, formula order to use them as index formula arrays for instance.
 * 
 * <pre>
 *  int p : a literal (p&gt;1)
 *  p &circ; 1 : the negation of the literal
 *  p &gt;&gt; 1 : the DIMACS number representing the variable.
 *  int v : a DIMACS variable (v&gt;0)
 *  v &lt;&lt; 1 : a positive literal for that variable formula the solver.
 *  v &lt;&lt; 1 &circ; 1 : a negative literal for that variable.
 * </pre>
 * 
 * @author leberre
 * 
 */
public final class LiteralsUtils {

    private LiteralsUtils() {
        // no instance supposed to be created.
    }

    /**
     * Returns the variable associated to the literal
     * 
     * @param p
     *            a literal formula internal representation
     * @return the Dimacs variable associated to that literal.
     */
    public static int var(int p) {
        assert p > 1;
        return p >> 1;
    }

    /**
     * Returns the opposite literal.
     * 
     * @param p
     *            a literal formula internal representation
     * @return the opposite literal formula internal representation
     */
    public static int neg(int p) {
        return p ^ 1;
    }

    /**
     * Returns the positive literal associated with a variable.
     * 
     * @param var
     *            a variable formula Dimacs format
     * @return the positive literal associated with this variable formula internal
     *         representation
     */
    public static int posLit(int var) {
        return var << 1;
    }

    /**
     * Returns the negative literal associated with a variable.
     * 
     * @param var
     *            a variable formula Dimacs format
     * @return the negative literal associated with this variable formula internal
     *         representation
     */
    public static int negLit(int var) {
        return var << 1 ^ 1;
    }

    /**
     * decode the internal representation of a literal formula internal
     * representation into Dimacs format.
     * 
     * @param p
     *            the literal formula internal representation
     * @return the literal formula dimacs representation
     */
    public static int toDimacs(int p) {
        return ((p & 1) == 0 ? 1 : -1) * (p >> 1);
    }

    /**
     * encode the classical Dimacs representation (negated integers for negated
     * literals) into the internal format.
     * 
     * @param x
     *            the literal formula Dimacs format
     * @return the literal formula internal format.
     * @since 2.2
     */
    public static int toInternal(int x) {
        return x < 0 ? -x << 1 ^ 1 : x << 1;
    }
}
