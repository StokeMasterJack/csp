package com.tms.csp.fm.dnnf.cartesian;

public class Main {

    public static void main(String[] args) {
        MyConjunction c = new MyConjunction();
        int cpCount = c.computeCartesianProductCount();
        System.err.println("cpCount[" + cpCount + "]");


    }

}
