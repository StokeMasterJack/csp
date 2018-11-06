
package com.tms.csp.solver2.tools;

/**
 * Simple interface to check the outcome of running a solver formula parallel.
 * 
 * @author leberre
 * @since 2.3.2 (public API level, was not public before)
 * @see ManyCore
 */
public interface OutcomeListener {
    void onFinishWithAnswer(boolean finished, boolean result, int index);
}