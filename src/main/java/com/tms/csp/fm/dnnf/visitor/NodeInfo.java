package com.tms.csp.fm.dnnf.visitor;

import com.tms.csp.ast.Exp;

import static com.tms.csp.ssutil.Console.prindent;


public class NodeInfo extends NodeHandler {

    int complexNodeCount;
    int complexVarCount;
    int maxComplexVarCount;
    int nodesWithMoreThan64Vars;
    int nodesWithMoreThan32Vars;
    int nodesWithMoreThan10Vars;
    int nodesWithMoreThan05Vars;

    @Override
    public void onHead(Exp n) {

        if (n.isComplex()) {
            complexNodeCount++;
            int varCount = n.getVarCount();
            complexVarCount += varCount;

            if (varCount > 64) {
                nodesWithMoreThan64Vars++;
            }

            if (varCount > 32) {
                nodesWithMoreThan32Vars++;
            }

            if (varCount > 10) {
                nodesWithMoreThan10Vars++;
            }

            if (varCount > 5) {
                nodesWithMoreThan05Vars++;
            }

            if (varCount > maxComplexVarCount) {
                maxComplexVarCount = varCount;
            }


        }


    }

//    public void print(int depth) {
//        prindent(depth, "NodeInfo:");
//
//
//        int avgComplexVarCount = complexVarCount / complexNodeCount;
//
//        double percentMoreThan64 = ((double) nodesWithMoreThan64Vars / (double) complexNodeCount) * 100.00;
//        double percentMoreThan32 = ((double) nodesWithMoreThan32Vars / (double) complexNodeCount) * 100.00;
//        double percentMoreThan10 = ((double) nodesWithMoreThan10Vars / (double) complexNodeCount) * 100.00;
//        double percentMoreThan05 = ((double) nodesWithMoreThan05Vars / (double) complexNodeCount) * 100.00;
//
//        System.err.println("complexNodeCount    [" + complexNodeCount + "]");
//        System.err.println("complexVarCount     [" + complexVarCount + "]");
//        System.err.println("avgComplexVarCount  [" + avgComplexVarCount + "]");
//        System.err.println("maxComplexVarCount  [" + maxComplexVarCount + "]");
//
//        System.err.println("percentMoreThan64   [" + percentMoreThan64 + "]");
//        System.err.println("percentMoreThan32   [" + percentMoreThan32 + "]");
//        System.err.println("percentMoreThan10   [" + percentMoreThan10 + "]");
//        System.err.println("percentMoreThan05   [" + percentMoreThan05 + "]");
//
//
//        System.err.println();
//    }


}
