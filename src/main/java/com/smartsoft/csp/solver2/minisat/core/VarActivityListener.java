package com.smartsoft.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * Interface providing the capability to increase the activity of a given
 * variable.
 * 
 * @author leberre
 */
public interface VarActivityListener extends Serializable {

    /**
     * Update the activity of a variable v.
     * 
     * @param p
     *            a literal (v<<1 or v<<1^1)
     */
    void varBumpActivity(int p);
}
