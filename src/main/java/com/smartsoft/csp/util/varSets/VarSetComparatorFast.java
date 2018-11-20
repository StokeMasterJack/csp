package com.smartsoft.csp.util.varSets;

import com.smartsoft.csp.ast.Var;

public class VarSetComparatorFast {


    public static int compare(VarSet varSet1, VarSet varSet2) {

        int size1 = varSet1.size();
        int size2 = varSet2.size();

        if (size1 < size2) {
            return -1;
        }

        if (size1 > size2) {
            return 1;
        }

        //same size
        assert size1 == size2;

        if (size1 == 0) {
            return 0;
        }

        if (size1 == 1) {
            Var v1 = varSet1.getFirstVar();
            Var v2 = varSet2.getFirstVar();
            return v1.compareFast(v2);
        }

        if (size1 == 2) {
            VarPair vs1 = varSet1.asVarPair();
            VarPair vs2 = varSet2.asVarPair();
            return compareVarPairs(vs1, vs2);
        }

        if (size1 > 2) {
            VarSetBuilder vs1 = varSet1.asVarSetBuilder();
            VarSetBuilder vs2 = varSet2.asVarSetBuilder();
            return compareVarSetBuilders(vs1, vs2);
        }

        throw new UnsupportedOperationException();
    }

    public static int compareVarPairs(VarPair varSet1, VarPair varSet2) {
        int min1 = varSet1.getMinVarId();
        int min2 = varSet2.getMinVarId();

        if (min1 < min2) {
            return -1;
        }
        if (min1 > min2) {
            return 1;
        }


        int max1 = varSet1.getMaxVarId();
        int max2 = varSet2.getMaxVarId();

        if (max1 < max2) {
            return -1;
        }
        if (max1 > max2) {
            return 1;
        }

        return 0;
    }

    public static int compareVarSetBuilders(VarSetBuilder varSet1, VarSetBuilder varSet2) {

        long[] words1 = varSet1.words;
        long[] words2 = varSet2.words;

        assert words1.length == words2.length;

        int L = words1.length;

        for (int i = 0; i < L; i++) {
            long l1 = words1[i];
            long l2 = words2[i];

            if (l1 < l2) {
                return -1;
            }

            if (l1 > l2) {
                return 1;
            }

        }

        return 0;
    }

}
