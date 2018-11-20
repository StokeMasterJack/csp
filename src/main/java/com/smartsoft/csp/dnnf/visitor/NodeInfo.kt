package com.smartsoft.csp.dnnf.visitor

import com.google.common.collect.HashMultiset
import com.smartsoft.csp.ast.Exp

import com.smartsoft.csp.ssutil.Console.prindent


class NodeInfo : NodeHandler() {

    internal var mm: HashMultiset<Class<*>> = HashMultiset.create()

    internal var complexNodeCount: Int = 0
    internal var complexVarCount: Int = 0
    internal var maxComplexVarCount: Int = 0
    internal var nodesWithMoreThan64Vars: Int = 0
    internal var nodesWithMoreThan32Vars: Int = 0
    internal var nodesWithMoreThan10Vars: Int = 0
    internal var nodesWithMoreThan05Vars: Int = 0

    override fun onHead(n: Exp) {

        mm.add(n.javaClass)

        if (n.isComplex) {
            complexNodeCount++
            val varCount = n.varCount
            complexVarCount += varCount

            if (varCount > 64) {
                nodesWithMoreThan64Vars++
            }

            if (varCount > 32) {
                nodesWithMoreThan32Vars++
            }

            if (varCount > 10) {
                nodesWithMoreThan10Vars++
            }

            if (varCount > 5) {
                nodesWithMoreThan05Vars++
            }

            if (varCount > maxComplexVarCount) {
                maxComplexVarCount = varCount
            }


        }


    }


    fun print(depth: Int) {


        val classes = mm.elementSet()
        classes.toList().map { "${it.simpleName}: ${mm.count(it)}" }.sorted().joinToString("   ").let {
            prindent(depth,"  NodeCounts: $it")
        }
//        for (cls in classes) {
//            val count = mm.count(cls)
//            System.err.print(cls.simpleName + ":" + count + "  ")
//        }
        System.err.println()

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
    }


}
