package com.smartsoft.csp.ast;

public class Cum {

    public long copy = 0;
    public long addConstraint = 0;
    public long propagate = 0;
    public long remove = 0;
    public long simplify = 0;
    public long caresAbout;

    public void print() {
        System.err.println("copy[" + copy + "]");
        System.err.println("addConstraint[" + addConstraint + "]");
        System.err.println("propagate[" + propagate + "]");
        System.err.println("remove[" + remove + "]");
        System.err.println("simplify[" + simplify + "]");
        System.err.println("caresAbout[" + caresAbout + "]");
        System.err.println();
    }
}
