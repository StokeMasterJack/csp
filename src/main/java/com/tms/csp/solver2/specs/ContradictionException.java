package com.tms.csp.solver2.specs;

/**
 * That exception is launched whenever a trivial contradiction is found (e.g.
 * null clause).
 * 
 * @author leberre
 */
public class ContradictionException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public ContradictionException() {
        super();
    }

    /**
     * @param message
     *            un message
     */
    public ContradictionException(final String message) {
        super(message);
    }

    /**
     * @param cause
     *            la cause de l'exception
     */
    public ContradictionException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     *            un message
     * @param cause
     *            une cause
     */
    public ContradictionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
