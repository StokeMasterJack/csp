package com.tms.csp.misc;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.formula.Formula;
import com.tms.csp.data.CspSample;
import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.util.varSets.VarSet;
import org.junit.Test;

import java.util.Random;

public class AnyIntersectionTest extends CspBaseTest2 {



    @Test
    public void testUFWithCamry() throws Exception {

        Csp csp = Csp.parse(CspSample.Camry2011);

        Formula formula = csp.mkFormula().asFormula();

        VarSet vars = formula.getVars();
        System.err.println("formula _vars[" + vars.size() + "]");
        System.err.println("space _vars  [" + csp.getSpace().getVars().size() + "]");

        int cc = formula.getConstraintCount();
        System.err.println("cc[" + cc + "]");
        System.err.println();
        System.err.println();

        Random r = new Random();

        for (int i = 0; i < 10; i++) {


            int i1 = r.nextInt(105);
            int i2 = r.nextInt(105);

            System.err.println("i1 i2[" + i1 + " " + i2 + "]");

            Exp e1 = formula.get(i1);
            Exp e2 = formula.get(i2);

            System.err.println("e1[" + e1 + "]");
            System.err.println("e2[" + e2 + "]");

            boolean directlyRelated = formula.isDirectlyRelated(i1, i2);
            System.err.println("directlyRelated[" + directlyRelated + "]");


            System.err.println();
        }


//        UnionFind unionFind = formula.computeUnionFind();
//
//        int fccCount = unionFind.getFccCount();
//        System.err.println("unionFind[" + unionFind + "]");
//
//        assertEquals(2, fccCount);
//
//        System.err.println("ss");

    }

    Csp loadCamry() {
        return CspSample.Camry2011.csp();
    }
}
