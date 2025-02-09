package com.smartsoft.csp.fcc;

import com.smartsoft.csp.ast.Csp;
import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.FConstraintSet;
import com.smartsoft.csp.data.CspSample;
import com.smartsoft.csp.util.CspBaseTest2;
import com.smartsoft.csp.util.UnionFind;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class UnionFindTest extends CspBaseTest2 {


    @Test
    public void testWithFakeConstraintSet() throws Exception {
        UnionFind uf1 = UnionFind.compute(new MyConstraintSet());
        int fccCount = uf1.getFccCount();
        assertEquals(3, fccCount);
    }

    @Test
    public void testUFWithTiny() throws Exception {
        Csp csp = loadCsp(CspSample.TinyDc);
        UnionFind unionFind = csp.computeUnionFind();
        int fccCount = unionFind.getFccCount();
        assertEquals(1, fccCount);
    }

    @Test
    public void testUFWithTrimColorOptions() throws Exception {
        Csp csp = loadCsp(CspSample.TrimColorOptionsDc);
        UnionFind unionFind = csp.computeUnionFind();
        int fccCount = unionFind.getFccCount();
        assertEquals(1, fccCount);
    }


    @Test
    public void testUFWithCamry()  {
        Csp csp = loadCsp(CspSample.Camry2011Dc);
        UnionFind<Exp> unionFind = csp.computeUnionFind();
        int fccCount = unionFind.getFccCount();
        assertEquals(2, fccCount);


    }





    //44s  49s  51s 49s  16s 13s 24s 8.4s
    @Test
    public void testUFWithEfc() throws Exception {
//        String clob = loadText(efcOriginalFile);
        String clob = loadResource(efcOriginal);

        Csp csp = Csp.parse(clob);

        long t1 = System.currentTimeMillis();
        UnionFind<Exp> unionFind = csp.computeUnionFind();
        int fccCount = unionFind.getFccCount();
        long t2 = System.currentTimeMillis();
        assertEquals(1, fccCount);

        System.err.println("testUFWithSpaceCspEfc Delta: " + (t2 - t1));

    }

    public static class MyConstraintSet implements FConstraintSet<int[]> {

        int[][] constraints = {
                {1, 2, 3},
                {18, 44},
                {1, 2, 5},
                {8, 9, 10},
                {1, 2, 4},
                {7, 2, 3}

        };

        public MyConstraintSet() {
            for (int i = 0; i < constraints.length; i++) {
                Arrays.sort(constraints[i]);
            }
        }

        @NotNull
        @Override
        public Iterable<Exp> getArgIt() {
            return null;
        }


        @Override
        public int getConstraintCount() {
            return constraints.length;
        }

        @Override
        public boolean isDirectlyRelated(int fLocalConstraintIndex1, int fLocalConstraintIndex2) {
            return areConstraintsDirectlyRelated(fLocalConstraintIndex1, fLocalConstraintIndex2);
        }

        public boolean areConstraintsDirectlyRelated(int constraint1, int constraint2) {
            return anyIntersection(constraints[constraint1], constraints[constraint2]);
        }

        public boolean anyIntersection(int[] constraint1, int[] constraint2) {
            for (int i1 = 0; i1 < constraint1.length; i1++) {
                int v1 = constraint1[i1];
                if (constraintContainsVar(constraint2, v1)) {
                    return true;
                }
            }
            return false;
        }

        public boolean constraintContainsVar(int[] constraint, int var) {
            int index = Arrays.binarySearch(constraint, var);
            return index >= 0;
        }

        @NotNull
        @Override
        public int[] getConstraint(int index) {
            return constraints[index];
        }
    }


}
