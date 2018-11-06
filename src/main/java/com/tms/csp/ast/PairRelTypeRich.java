package com.tms.csp.ast;

public class PairRelTypeRich {

    /**
     *
     * 0-15
     * 0:  TRUE: no dead pairs (all perms allowed)
     * 16: FALSE: :all pairs are dead (no perms allowed)
     */
    int index;

    //dead cubes
    boolean ff;   //bit 0
    boolean ft;   //bit 1
    boolean tf;   //bit 2
    boolean tt;   //bit 3


    boolean instantSimplify;

    String cnf;



}
