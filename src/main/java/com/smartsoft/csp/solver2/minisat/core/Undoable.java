package com.smartsoft.csp.solver2.minisat.core;

/**
 * Interface providing the undoable service.
 * 
 * @author leberre
 */
public interface Undoable {

    /**
     * Method called when backtracking
     * 
     * @param p
     *            a literal to be unassigned.
     */
    void undo(int p);

}
