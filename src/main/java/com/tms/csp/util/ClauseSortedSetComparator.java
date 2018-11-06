package com.tms.csp.util;

import java.util.Comparator;
import java.util.TreeSet;

public class ClauseSortedSetComparator implements Comparator<TreeSet<String>> {
    @Override
    public int compare(TreeSet<String> clause1, TreeSet<String> clause2) {

        Integer size1 = clause1.size();
        Integer size2 = clause2.size();

        int c1 = size1.compareTo(size2);
        if (c1 != 0) {
            return c1;
        }

        String string1 = clause1.toString();
        String string2 = clause2.toString();

        return string1.compareTo(string2);
    }
}
