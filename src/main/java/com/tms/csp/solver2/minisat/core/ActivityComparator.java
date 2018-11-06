package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Utility class to sort the fact according to their activity.
 * 
 * @author daniel
 * 
 */
public class ActivityComparator implements Comparator<Constr>, Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Constr c1, Constr c2) {
        long delta = Math.round(c1.getActivity() - c2.getActivity());
        if (delta == 0) {
            return c1.size() - c2.size();
        }
        return (int) delta;
    }
}