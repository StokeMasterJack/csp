package com.tms.csp.solver2.specs;

/**
 * Exception launched when the solver cannot solve a problem within its allowed
 * time. Note that the name of that exception is subject to change since a
 * TimeoutException must also be launched by incomplete solvers to reply
 * "Unknown".
 * 
 * @author leberre
 */
public class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for TimeoutException.
     */
    public TimeoutException() {
        super();
    }

    /**
     * Constructor for TimeoutException.
     * 
     * @param message
     *            the error message
     */
    public TimeoutException(String message) {
        super(message);
    }

    /**
     * Constructor for TimeoutException.
     * 
     * @param message
     *            the error message
     * @param cause
     *            the cause of the exception
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for TimeoutException.
     * 
     * @param cause
     *            the cause of the exception
     */
    public TimeoutException(Throwable cause) {
        super(cause);
    }

}
