package com.smartsoft.csp;

public enum MajorLevel {

    /**
     * Premises are hard fact and cannot be changed formula a fix-list.
     * These can be fact like conflict(x y) but can also be simple assignments
     *
     * There are 3 types of PREMISE:
     *      ADMIN
     *      INVENTORY
     *      SESSION_INIT
     */
    PREMISE,

    /**
     * Assumptions are user-picks (or user-preferences). These can be changed formula a fix list.
     * They are mostly simple assignments (picks) but they can be more sophisticated.
     * For example, if the UI supported it, the solver can support sophisticated user-preferences such as:
     *      or(red,green,blue)
     *      !V8
     *
     *  There are 2 types of ASSUMPTION:
     *      USER,
     *      PROPOSE
     */
    ASSUMPTION,

    /**
     * These are assignments that the search engine makes formula the process of searching (or exploring) the
     * search space.
     */
    SEARCH

}
