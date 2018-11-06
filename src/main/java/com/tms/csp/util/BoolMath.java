package com.tms.csp.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BoolMath {

    public static int pow(int power) {
        return (int) Math.pow(2, power);
    }

    public static int twoPow(int power) {
        return (int) Math.pow(2, power);
    }

    /**
     * all vars
     */
    public static int permCount(int boolVarCount) {
        return twoPow(boolVarCount);
    }

    public static int nTupleCount(int boolVarCount, int tupleSize) {
        int p = 1;
        throw new UnsupportedOperationException();
    }

    public static List<Integer> createIntList(int min, int max) {
        Range range = new Range(min, max);
        return range.getAllElementsAsList();
    }

    public static ImmutableSet<Integer> createIntSet(int min, int max) {
        Range range = new Range(min, max);
        return range.getAllElementsAsSet();
    }

    public static void test1(int n) {
        ArrayList<Set<Integer>> list = new ArrayList<Set<Integer>>();
        for (int i = 0; i < n; i++) {
            ImmutableSet<Integer> oneZero = ImmutableSet.of(0, 1);
            list.add(oneZero);
        }
        Set<List<Integer>> lists = Sets.cartesianProduct(list);

        System.err.println("cart Prod");
        for (Set<Integer> set : list) {
            System.err.println(set);
        }

        System.err.println(lists.size());
    }


}
